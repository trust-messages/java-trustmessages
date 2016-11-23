package com.david;

import com.david.messages.AssessmentResponse;
import com.david.messages.Message;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TrustService implements Runnable {
    private TrustSocket socket;
    private final BlockingQueue<TrustSocket.TrustSocketRequest> queue = new LinkedBlockingQueue<>();
    private final QTMDb db = new QTMDb();

    public void setSocket(TrustSocket socket) {
        this.socket = socket;
    }

    void enqueueRequest(final TrustSocket.TrustSocketRequest request) {
        try {
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                final TrustSocket.TrustSocketRequest request = queue.take();

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

                        socket.send(request.remoteAddress.getAddress(),
                                request.remoteAddress.getPort(), baos.getArray());
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    System.out.printf("Could not respond properly: %s", e.getMessage());
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
