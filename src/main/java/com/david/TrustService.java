package com.david;

import com.david.messages.AssessmentResponse;
import com.david.messages.Message;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TrustService implements Runnable {
    private final BlockingQueue<TrustSocketRequest> queue = new LinkedBlockingQueue<>();
    private final QTMDb db = new QTMDb();

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

                // provisional test
                try {
                    final Message req = new Message();
                    req.decode(new ByteArrayInputStream(request.data), null);

                    if (req.assessmentRequest != null) {
                        final AssessmentResponse ar = new AssessmentResponse();
                        ar.rid = req.assessmentRequest.rid;
                        ar.response = new AssessmentResponse.Response();
                        ar.response.seqOf = db.getAssessments(req.assessmentRequest.query);

                        final Message res = new Message(null, ar, null, null, null, null, null);
                        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
                        res.encode(baos, true);
                        request.trustSocket.send(request.socketChannel, baos.getArray());
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    System.out.println("Could not respond properly, echoing ...");
                    request.trustSocket.send(request.socketChannel, request.data);
                }

            } catch (InterruptedException e) {
            }
        }
    }
}
