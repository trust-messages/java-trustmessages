package com.david;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
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
    private final Pipe pipe;
    private final Pipe.SourceChannel source;
    private final Pipe.SinkChannel sink;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private final TrustService service;

    private final ConcurrentMap<SocketChannel, Queue<ByteBuffer>> outgoingQueues = new ConcurrentHashMap<>();

    public TrustSocket(InetAddress hostAddress, int port, TrustService service) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.service = service;

        this.pipe = Pipe.open();
        this.sink = pipe.sink();
        this.source = pipe.source();
        this.source.configureBlocking(false);
        source.register(selector, SelectionKey.OP_READ);

        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
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
                        if (key.channel().equals(source)) {
                            // read the data
                            final int numRead;
                            try {
                                readBuffer.clear();
                                numRead = source.read(readBuffer);
                                if (numRead == -1) {
                                    throw new Error("Pipe failure: read returned -1");
                                }
                            } catch (IOException e) {
                                throw new Error("Pipe failure.", e);
                            }

                            // get address and port
                            readBuffer.flip();
                            final byte[] addrBytes = new byte[4];
                            readBuffer.get(addrBytes);
                            final InetAddress address = InetAddress.getByAddress(addrBytes);
                            final int port = readBuffer.getInt();

                            // copy data into new buffer
                            final byte[] data = new byte[numRead - 8];
                            System.arraycopy(readBuffer.array(), 8, data, 0, numRead - 8);

                            // enqueue the data
                            final SocketChannel channel = getSocket(address, port);
                            outgoingQueues.get(channel).add(ByteBuffer.wrap(data));

                            // make the target channel signal when ready for writing
                            final SelectionKey chanKey = channel.keyFor(selector);
                            chanKey.interestOps(SelectionKey.OP_WRITE);
                        } else {
                            read(key);
                        }
                    } else if (key.isWritable()) {
                        write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SocketChannel getSocket(InetAddress hostname, int port) throws IOException {
        for (SocketChannel sc : outgoingQueues.keySet()) {
            final InetSocketAddress address = (InetSocketAddress) sc.getRemoteAddress();

            if (hostname.equals(address.getAddress()) && port == address.getPort()) {
                return sc;
            }
        }

        throw new IllegalArgumentException("No socket for: " + hostname.getHostAddress() + ":" + port);
    }

    public synchronized void send(InetAddress address, int port, byte[] data) throws IOException {
        final ByteBuffer buff = ByteBuffer.allocate(data.length + 8)
                .put(address.getAddress())
                .putInt(port)
                .put(data);
        buff.flip();

        sink.write(buff);
    }

    private void write(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        final Queue<ByteBuffer> queue = outgoingQueues.get(channel);

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
        readBuffer.clear();

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
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        final SocketChannel socket = serverSocketChannel.accept();
        socket.configureBlocking(false);

        // Create an outgoing message queue for this socket
        outgoingQueues.put(socket, new ConcurrentLinkedQueue<>());

        System.out.printf("[%s]: <CONNECTED>%n",
                socket.socket().getRemoteSocketAddress());

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socket.register(selector, SelectionKey.OP_READ);
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
