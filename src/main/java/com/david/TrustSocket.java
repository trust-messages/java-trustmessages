package com.david;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class TrustSocket implements Runnable {
    public final InetAddress hostAddress;
    public final int port;

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private final EchoWorker worker;

    private final List<ChangeRequest> changeRequests = new ArrayList<>();

    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    public TrustSocket(InetAddress hostAddress, int port, EchoWorker worker) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.worker = worker;

        selector = SelectorProvider.provider().openSelector();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.changeRequests) {
                    for (ChangeRequest change : changeRequests) {
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                final SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
                                break;
                        }
                    }
                    this.changeRequests.clear();
                }

                selector.select();
                final Iterator selectedKeys = this.selector.selectedKeys().iterator();

                while (selectedKeys.hasNext()) {
                    final SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        synchronized (pendingData) {
            final List<ByteBuffer> queue = pendingData.get(channel);

            // Write until there's no more data ...
            while (!queue.isEmpty()) {
                final ByteBuffer buf = queue.get(0);
                channel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // Write completed; switch back to read events
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        final int numRead;
        try {
            numRead = channel.read(readBuffer);
        } catch (IOException e) {
            // Forced shutdown (by remote)
            key.cancel();
            channel.close();
            return;
        }

        if (numRead == -1) {
            // Clean shutdown (by remote)
            key.channel().close();
            key.cancel();
            return;
        }

        // Hand the data off to our worker thread
        final byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
        worker.processData(this, channel, data, numRead);
    }

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        final SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.printf("[%s] <CONNECTED>%n",
                socketChannel.socket().getRemoteSocketAddress());

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        try {
            final EchoWorker worker = new EchoWorker();
            new Thread(worker).start();
            new Thread(new TrustSocket(null, 6000, worker)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(SocketChannel channel, byte[] data) {
        // XXX: Other threads may call this method.
        synchronized (changeRequests) {
            // Indicate we want the interest ops set changed
            changeRequests.add(new ChangeRequest(channel, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (pendingData) {
                List<ByteBuffer> queue = pendingData.get(channel);

                if (queue == null) {
                    queue = new ArrayList<>();
                    pendingData.put(channel, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        selector.wakeup();
    }
}
