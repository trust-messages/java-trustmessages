package com.david;

import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TrustService implements Runnable {
    private final BlockingQueue<TrustSocketRequest> queue = new LinkedBlockingQueue<>();

    void enqueueRequest(TrustSocket server, SocketChannel channel, byte[] data, int count) {
        // Called from other threads
        final byte[] copy = new byte[count];
        System.arraycopy(data, 0, copy, 0, count);

        try {
            queue.put(new TrustSocketRequest(server, channel, copy));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                final TrustSocketRequest request = queue.take();
                request.trustSocket.send(request.socketChannel, request.data);
            } catch (InterruptedException e) {
            }
        }
    }
}
