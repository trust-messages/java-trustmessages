package com.david;

import com.david.messages.AssessmentResponse;
import com.david.messages.FormatResponse;
import com.david.messages.Message;
import com.david.messages.TrustResponse;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.david.Utils.decode;
import static com.david.Utils.encode;

class TrustService implements Runnable, IncomingDataHandler {
    private class IncomingData {
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
    public void handle(TrustSocket socket, InetSocketAddress sender, byte[] data) {
        try {
            queue.put(new IncomingData(socket, sender, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                final IncomingData incoming = queue.take();
                final Message request = decode(incoming.data);
                final Message response = new Message();

                if (request.assessmentRequest != null) {
                    final AssessmentResponse ar = new AssessmentResponse(
                            request.assessmentRequest.rid,
                            new AssessmentResponse.Response());
                    ar.response.seqOf = db.getAssessments(request.assessmentRequest.query);
                    response.assessmentResponse = ar;
                } else if (request.trustRequest != null) {
                    final TrustResponse tr = new TrustResponse(
                            request.trustRequest.rid,
                            new TrustResponse.Response());
                    tr.response.seqOf = db.getTrust(request.trustRequest.query);
                    response.trustResponse = tr;
                } else if (request.formatRequest != null) {
                    final FormatResponse fr = new FormatResponse();
                    fr.tms = QTMDb.ID;
                    fr.assessment = new BerPrintableString(db.getFormat().get("assessment").getBytes());
                    fr.trust = new BerPrintableString(db.getFormat().get("trust").getBytes());
                    response.formatResponse = fr;
                } else if (request.trustResponse != null) {
                    System.out.println(request);
                } else if (request.assessmentResponse != null) {
                    System.out.println(request);
                } else if (request.formatResponse != null) {
                    System.out.println(request);
                } else {
                    throw new IOException("Unknown message: " + request);
                }

                incoming.socket.send(
                        incoming.sender.getAddress(),
                        incoming.sender.getPort(),
                        encode(response));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
