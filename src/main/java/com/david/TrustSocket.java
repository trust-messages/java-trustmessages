package com.david;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class TrustSocket implements Runnable {
    private final static Logger LOG = LoggerFactory.getLogger(TrustSocket.class);

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
                LOG.debug("Waiting ...");
                selector.select();

                final Iterator selectedKeys = selector.selectedKeys().iterator();

                while (selectedKeys.hasNext()) {
                    final SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        LOG.warn("Invalid key: {} ", key);
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept(key); // new connection
                    } else if (key.isReadable()) {
                        if (source == key.channel()) {
                            enqueueOutgoing(); // receive data from the pipe and enqueue it for sending
                        } else {
                            read(key); // read the data from a network socket
                        }
                    } else if (key.isWritable()) {
                        write(key); // write to socket
                    }
                }
            } catch (IOException e) {
                LOG.warn("Exception", e);
            }
        }
    }

    private void enqueueOutgoing() throws IOException {
        // FIX BUG: it can happen that the pipe enqueues two messages in succession;
        // as it is currently implemented, the socket will process them as a single message
        // solution: prefix the message with its size and do processing on discrete messages
        final int numRead;
        try {
            readBuffer.clear();
            numRead = source.read(readBuffer);
            if (numRead == -1) {
                throw new Error("Pipe failure: read returned -1");
            }
        } catch (Exception e) {
            throw new Error("Pipe failure.", e);
        }

        // get sender and port
        readBuffer.flip();
        final byte[] addressBytes = new byte[4];
        readBuffer.get(addressBytes);
        final InetAddress address = InetAddress.getByAddress(addressBytes);
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

        LOG.info("[{}] Enqueued {} bytes (minus 8)", channel.getRemoteAddress(), numRead);
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
        LOG.debug("Flushing {} to {}", queue, channel);

        // write everything
        int numBytes = 0;
        while (!queue.isEmpty()) {
            final ByteBuffer buf = queue.peek();
            numBytes += channel.write(buf);
            if (buf.remaining() > 0) {
                LOG.warn("[{}] Flushed {} bytes. Output buffer is full. Delaying write.", channel.getRemoteAddress(), numBytes);
                break;
            }
            queue.remove();
        }

        if (queue.isEmpty()) {
            // stop listening to write-ready events
            LOG.info("[{}] Flushed the entire queue ({} bytes)", channel.getRemoteAddress(), numBytes);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        LOG.debug("[{}] Reading ...", channel.getRemoteAddress());

        readBuffer.clear();
        final int numRead;
        try {
            numRead = channel.read(readBuffer);
        } catch (IOException e) {
            // unexpected shutdown
            LOG.warn("[{}] Disconnected unexpectedly; closing.", channel.getRemoteAddress());
            disconnectSocket(key);
            return;
        }

        if (numRead == -1) {
            // clean shutdown
            LOG.debug("[{}] Disconnected cleanly, closing {}", channel.getRemoteAddress());
            disconnectSocket(key);
            return;
        }

        // hand the data to the service thread
        final byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);

        service.handle(this, (InetSocketAddress) channel.getRemoteAddress(), data);
        LOG.info("[{}] Read {} bytes; passing to {}", channel.getRemoteAddress(), numRead, service);
    }

    private void disconnectSocket(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final String address = channel.getRemoteAddress().toString();
        outQueues.remove(channel);
        channel.close();
        key.cancel();

        LOG.debug("[SYS] Existing outQueues: {}", outQueues);
        LOG.info("[{}] Disconnected", address);
    }

    /**
     * Accepts a new connection, creates a new outgoing queue and registers the selector
     * to notify for new data.
     *
     * @param key selected key
     * @throws IOException when an I/O error occurs
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

        LOG.debug("[SYS] outQueues: {}", outQueues);
        LOG.info("[{}] Connected", socket.getRemoteAddress());
    }

    public InetSocketAddress getEndpoint() throws IOException {
        return (InetSocketAddress) server.getLocalAddress();
    }
}
