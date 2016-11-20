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
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class TrustSocket implements Runnable {
    public final InetAddress hostAddress;
    public final int port;

    private final Selector selector = SelectorProvider.provider().openSelector();
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private final TrustService service;

    private final Queue<ChangeRequest> changeRequests = new ConcurrentLinkedQueue<>();
    private final ConcurrentMap<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();

    public TrustSocket(InetAddress hostAddress, int port, TrustService service) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.service = service;

        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                // Process any pending changes
                for (ChangeRequest change : changeRequests) {
                    switch (change.type) {
                        case ChangeRequest.CHANGEOPS:
                            final SelectionKey key = change.socket.keyFor(selector);
                            key.interestOps(change.ops);
                            break;
                    }
                }

                changeRequests.clear();

                selector.select();
                final Iterator selectedKeys = selector.selectedKeys().iterator();

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

        final Queue<ByteBuffer> queue = pendingData.get(channel);

        // Write until there's no more data ...
        while (!queue.isEmpty()) {
            final ByteBuffer buf = queue.peek();
            channel.write(buf);
            if (buf.remaining() > 0) {
                // ... or the socketChannel's buffer fills up
                break;
            }
            queue.remove();
        }

        if (queue.isEmpty()) {
            // Write completed; switch back to read events
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the socketChannel
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

        // Hand the data off to our service thread
        final byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
        service.enqueueRequest(this, channel, data, numRead);
    }

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the socketChannel must be a trustSocket socketChannel socketChannel.
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        final SocketChannel socket = serverSocketChannel.accept();
        socket.configureBlocking(false);

        System.out.printf("[%s]: <CONNECTED>%n",
                socket.socket().getRemoteSocketAddress());

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socket.register(selector, SelectionKey.OP_READ);
    }

    public void send(SocketChannel channel, byte[] data) {
        // XXX: Other threads may call this method.
        // Indicate we want the interest ops set changed
        changeRequests.add(new ChangeRequest(channel, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

        // And queue the data we want written
        Queue<ByteBuffer> queue = pendingData.get(channel);

        if (queue == null) {
            queue = new ConcurrentLinkedQueue<>();
            pendingData.put(channel, queue);
        }
        queue.add(ByteBuffer.wrap(data));

        // Finally, wake up our selecting thread so it can make the required changes
        selector.wakeup();
    }

    public static void main(String[] args) {
        try {
            final TrustService worker = new TrustService();
            new Thread(worker).start();
            new Thread(new TrustSocket(null, 6000, worker)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
