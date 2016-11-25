package com.david;

import com.david.messages.AssessmentResponse;
import com.david.messages.Message;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TrustService implements Runnable, IncomingDataHandler {
    class IncomingData {
        final TrustSocket socket;
        final InetSocketAddress sender;
        final byte[] data;

        IncomingData(TrustSocket socket, InetSocketAddress sender, byte[] data) {
            this.socket = socket;
            this.sender = sender;
            this.data = data;
        }
    }

    private final BlockingQueue<IncomingData> queue = new LinkedBlockingQueue<>();
    private final QTMDb db = new QTMDb();

    @Override
    public void handle(TrustSocket socket, InetSocketAddress remoteAddress, byte[] data) {
        try {
            queue.put(new IncomingData(socket, remoteAddress, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                final IncomingData request = queue.take();

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

                        request.socket.send(request.sender.getAddress(),
                                request.sender.getPort(), baos.getArray());
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
