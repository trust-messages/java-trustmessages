package com.david;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class TrustSocket implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel server;
    private final Pipe.SourceChannel source;
    private final Pipe.SinkChannel sink;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private final IncomingDataHandler service;

    private final Map<SocketChannel, Queue<ByteBuffer>> outQueues = new HashMap<>();

    public TrustSocket(int port, IncomingDataHandler service) {
        this.service = service;

        try {
            selector = SelectorProvider.provider().openSelector();

            final Pipe pipe = Pipe.open();
            sink = pipe.sink();

            source = pipe.source();
            source.configureBlocking(false);
            source.register(selector, SelectionKey.OP_READ);

            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress((InetAddress) null, port));
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new Error(e);
        }
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
                        // new connection
                        accept(key);
                    } else if (key.isReadable()) {
                        if (source == key.channel()) {
                            // receive outgoing data from the pipe and enqueue it for sending
                            enqueueOutgoing();
                        } else {
                            // read the data from a network socket
                            read(key);
                        }
                    } else if (key.isWritable()) {
                        // write to socket
                        write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void enqueueOutgoing() throws IOException {
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

        // get sender and port
        readBuffer.flip();
        final byte[] addrBytes = new byte[4];
        readBuffer.get(addrBytes);
        final InetAddress address = InetAddress.getByAddress(addrBytes);
        final int port = readBuffer.getInt();

        // copy data into new buffer
        final byte[] data = new byte[numRead - 8];
        System.arraycopy(readBuffer.array(), 8, data, 0, numRead - 8);
        readBuffer.clear();

        // enqueue the data to the outgoing channel
        final SocketChannel channel = getSocket(address, port);
        outQueues.get(channel).add(ByteBuffer.wrap(data));

        // make the target channel signal when ready for writing
        final SelectionKey chanKey = channel.keyFor(selector);
        chanKey.interestOps(SelectionKey.OP_WRITE);
    }

    private SocketChannel getSocket(InetAddress hostname, int port) throws IOException {
        for (SocketChannel sc : outQueues.keySet()) {
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

        final Queue<ByteBuffer> queue = outQueues.get(channel);

        // write everything
        while (!queue.isEmpty()) {
            final ByteBuffer buf = queue.peek();
            channel.write(buf);
            if (buf.remaining() > 0) {
                // stop if the socket's buffer fills up
                break;
            }
            queue.remove();
        }

        if (queue.isEmpty()) {
            // stop listening to write-ready events
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        // clear the read buffer
        readBuffer.clear();

        // read off the channel
        final int numRead;
        try {
            numRead = channel.read(readBuffer);
        } catch (IOException e) {
            // unexpected shutdown
            disconnectSocket(key);
            return;
        }

        if (numRead == -1) {
            // clean shutdown
            disconnectSocket(key);
            return;
        }

        // hand the data to the service thread
        final byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);

        service.handle(this, (InetSocketAddress) channel.getRemoteAddress(), data);
    }

    private void disconnectSocket(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final String address = channel.getRemoteAddress().toString();
        outQueues.remove(channel);
        channel.close();
        key.cancel();

        System.out.printf("[%s]: <DISCONNECTED>%n", address);
    }

    /**
     * Accepts a new connection, creates a new outgoing queue and registers the selector
     * to notify for new data.
     *
     * @param key
     * @throws IOException
     */
    private void accept(SelectionKey key) throws IOException {
        final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // accept the connection and make it non-blocking
        final SocketChannel socket = serverSocketChannel.accept();
        socket.configureBlocking(false);

        // create an outgoing message queue for this socket
        outQueues.put(socket, new ArrayDeque<>());

        // notify when there's data to be read
        socket.register(selector, SelectionKey.OP_READ);

        System.out.printf("[%s]: <CONNECTED>%n", socket.socket().getRemoteSocketAddress());
    }

    public InetSocketAddress getEndpoint() throws IOException {
        return (InetSocketAddress) server.getLocalAddress();
    }
}
