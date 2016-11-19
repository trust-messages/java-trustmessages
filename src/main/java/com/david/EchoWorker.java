package com.david;

import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class EchoWorker implements Runnable {
    private final BlockingQueue<ServerDataEvent> queue = new LinkedBlockingQueue<>();

    void processData(TrustSocket server, SocketChannel socket, byte[] data, int count) {
        final byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);

        try {
            queue.put(new ServerDataEvent(server, socket, dataCopy));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                final ServerDataEvent dataEvent = queue.take();
                dataEvent.server.send(dataEvent.socket, dataEvent.data);
            } catch (InterruptedException e) {
            }
        }
    }
}
