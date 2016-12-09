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
    private static final int PIPE_HEADER_LEN = 12;
    private final static Logger LOG = LoggerFactory.getLogger(TrustSocket.class);

    private enum Action {
        CONNECT, DISCONNECT, SEND, SHUTDOWN;

        public static Action fromCode(int n) {
            return values()[n];
        }

    }

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
            LOG.info("[{}] Binding ", server.socket());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public void run() {
        while (true) {
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
                        handleAccept(key); // new connection
                    } else if (key.isReadable()) {
                        if (source == key.channel()) {
                            // read the data from the pipe and enqueue it for sending
                            if (!handlePipe()) {
                                // shutDown
                                return;
                            }
                        } else {
                            handleSocketData(key); // read the data from a network socket and hand it to a worker
                        }
                    } else if (key.isWritable()) {
                        flushToSocket(key); // write to socket
                    }
                }
            } catch (Exception e) {
                LOG.error("Error occurred: {}", e.getLocalizedMessage());
            }
        }
    }

    private boolean handlePipe() throws IOException {
        int numRead;
        try {
            readBuffer.clear();

            // read the size (4 bytes)
            readBuffer.limit(4);
            numRead = source.read(readBuffer);

            if (numRead == -1) {
                LOG.error("[SYS] Pipe failure: read -1");
                return false;
            }

            // read next the payload (N bytes)
            readBuffer.flip();
            final int size = readBuffer.getInt();
            readBuffer.clear();

            readBuffer.limit(size);
            numRead = source.read(readBuffer);
            if (numRead == -1) {
                LOG.error("[SYS] Pipe failure: read -1");
                return false;
            }
        } catch (Exception e) {
            LOG.error("[SYS] Pipe failure", e);
            return false;
        }

        // get sender and port
        readBuffer.flip();
        final byte[] addressBytes = new byte[4];
        readBuffer.get(addressBytes);

        final InetAddress address = InetAddress.getByAddress(addressBytes);
        final int port = readBuffer.getInt();
        final Action action = Action.fromCode(readBuffer.getInt());

        if (action == Action.CONNECT) {
            final SocketChannel existingChannel = getSocket(address, port);
            if (existingChannel != null) {
                LOG.warn("[{}] Already connected, skipping.", existingChannel.getRemoteAddress());
                return true;
            }

            final SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(address, port));
            channel.configureBlocking(false);

            // create an outgoing message queue for this socket
            outQueues.put(channel, new ArrayDeque<>());

            // notify when there's data to be read
            channel.register(selector, SelectionKey.OP_READ);

            LOG.debug("[SYS] outQueues: {}", outQueues);
            LOG.info("[{}] Connected", channel.getRemoteAddress());
        } else if (action == Action.DISCONNECT) {
            final SocketChannel channel = getSocket(address, port);

            if (channel == null) {
                LOG.warn("[{}:{}] Not connected, skipping.", address, port);
                return true;
            }

            final SelectionKey key = channel.keyFor(selector);
            disconnectSocket(key);
        } else if (action == Action.SEND) {
            // copy data into new buffer
            final byte[] data = new byte[numRead - PIPE_HEADER_LEN];
            System.arraycopy(readBuffer.array(), PIPE_HEADER_LEN, data, 0, numRead - PIPE_HEADER_LEN);
            readBuffer.clear();

            // enqueue the data to the outgoing channel
            final SocketChannel channel = getSocket(address, port);
            if (channel == null) {
                LOG.warn("[{}:{}] Not connected, skipping.", address, port);
                return true;
            }
            outQueues.get(channel).add(ByteBuffer.wrap(data));

            // make the target channel signal when ready for writing
            final SelectionKey chanKey = channel.keyFor(selector);
            chanKey.interestOps(SelectionKey.OP_WRITE);

            LOG.debug("[{}] Enqueued {} bytes", channel.getRemoteAddress(), numRead - PIPE_HEADER_LEN);
        } else {
            assert action == Action.SHUTDOWN;
            LOG.warn("[SYS] Shutdown");
            return false;
        }

        return true;
    }

    private SocketChannel getSocket(InetAddress hostname, int port) throws IOException {
        for (SocketChannel sc : outQueues.keySet()) {
            final InetSocketAddress address = (InetSocketAddress) sc.getRemoteAddress();

            if (hostname.equals(address.getAddress()) && port == address.getPort()) {
                return sc;
            }
        }

        return null;
    }

    private void flushToSocket(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final Queue<ByteBuffer> queue = outQueues.get(channel);
        LOG.debug("Flushing {} to {}", queue, channel);

        // write everything
        int numBytes = 0;
        while (!queue.isEmpty()) {
            final ByteBuffer buf = queue.peek();
            numBytes += channel.write(buf);
            if (buf.remaining() > 0) {
                LOG.warn("[{}] Sent ({}B); flush incomplete, delaying.", channel.getRemoteAddress(), numBytes);
                break;
            }
            queue.remove();
        }

        if (queue.isEmpty()) {
            // flushed entire queue; stop listening to write-ready events
            LOG.info("[{}] Sent ({}B)", channel.getRemoteAddress(), numBytes);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void handleSocketData(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        LOG.debug("[{}] Reading ...", channel.getRemoteAddress());

        readBuffer.clear();
        final int numRead;
        try {
            numRead = channel.read(readBuffer);
        } catch (IOException e) {
            // unexpected shutDown
            LOG.warn("[{}] Disconnected unexpectedly; closing.", channel.getRemoteAddress());
            disconnectSocket(key);
            return;
        }

        if (numRead == -1) {
            // clean shutDown
            LOG.debug("[{}] Disconnected cleanly, closing {}", channel.getRemoteAddress());
            disconnectSocket(key);
            return;
        }

        // hand the data to the service thread
        final byte[] data = new byte[numRead];
        System.arraycopy(readBuffer.array(), 0, data, 0, numRead);

        service.handle(this, (InetSocketAddress) channel.getRemoteAddress(), data);
        LOG.info("[{}] Read ({}B)", channel.getRemoteAddress(), numRead);
    }

    private void disconnectSocket(SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final String address = channel.getRemoteAddress().toString();
        outQueues.remove(channel);
        channel.close();
        key.cancel();

        LOG.debug("[SYS] outQueues: {}", outQueues);
        LOG.info("[{}] Disconnected", address);
    }

    /**
     * Accepts a new connection, creates a new outgoing queue and registers the selector
     * to notify for new data.
     *
     * @param key selected key
     * @throws IOException when an I/O error occurs
     */
    private void handleAccept(SelectionKey key) throws IOException {
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

    private synchronized void sendCommand(ByteBuffer buff) throws IOException {
        sink.write(buff);
    }

    public void send(InetAddress address, int port, byte[] data) throws IOException {
        final ByteBuffer buff = ByteBuffer.allocate(4 + PIPE_HEADER_LEN + data.length)
                .putInt(PIPE_HEADER_LEN + data.length)
                .put(address.getAddress())
                .putInt(port)
                .putInt(Action.SEND.ordinal())
                .put(data);
        buff.flip();
        sendCommand(buff);
    }

    void connect(InetAddress address, int port) throws IOException {
        final ByteBuffer buff = ByteBuffer.allocate(4 + PIPE_HEADER_LEN)
                .putInt(PIPE_HEADER_LEN)
                .put(address.getAddress())
                .putInt(port)
                .putInt(Action.CONNECT.ordinal());
        buff.flip();
        sendCommand(buff);
    }

    void disconnect(InetAddress address, int port) throws IOException {
        final ByteBuffer buff = ByteBuffer.allocate(4 + PIPE_HEADER_LEN)
                .putInt(PIPE_HEADER_LEN)
                .put(address.getAddress())
                .putInt(port)
                .putInt(Action.DISCONNECT.ordinal());
        buff.flip();
        sendCommand(buff);
    }

    void shutDown() throws IOException {
        final ByteBuffer buff = ByteBuffer.allocate(4 + PIPE_HEADER_LEN)
                .putInt(PIPE_HEADER_LEN)
                .put(InetAddress.getLocalHost().getAddress())
                .putInt(0)
                .putInt(Action.SHUTDOWN.ordinal());
        buff.flip();
        sendCommand(buff);
    }
}
