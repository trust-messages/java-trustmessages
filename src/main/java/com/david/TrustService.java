package com.david;

import com.david.messages.AssessmentResponse;
import com.david.messages.FormatResponse;
import com.david.messages.Message;
import com.david.messages.TrustResponse;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.david.Utils.decode;
import static com.david.Utils.encode;

class TrustService implements Runnable, IncomingDataHandler {
    private final static Logger LOG = LoggerFactory.getLogger(TrustService.class);

    private class TrustMessage {
        final TrustSocket socket;
        final InetSocketAddress sender;
        final byte[] data;

        TrustMessage(TrustSocket socket, InetSocketAddress sender, byte[] data) {
            this.socket = socket;
            this.sender = sender;
            this.data = data;
        }

        @Override
        public String toString() {
            return "TM{sender=" + sender + ", len(data)=" + data.length + '}';
        }
    }

    private final BlockingQueue<TrustMessage> queue = new LinkedBlockingQueue<>();
    private final QTMDb db = new QTMDb();

    @Override
    public void handle(TrustSocket socket, InetSocketAddress sender, byte[] data) {
        try {
            queue.put(new TrustMessage(socket, sender, data));
        } catch (InterruptedException e) {
            LOG.error("An error occurred while enqueueing a request.", e);
        }
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                final TrustMessage packet = queue.take();
                LOG.debug("Processing: {}", packet);

                final Message incoming = decode(packet.data);
                LOG.debug("Decoded: {}", incoming);

                final Message outgoing = new Message();

                if (incoming.assessmentRequest != null) {
                    LOG.info("Assessment request: {}", incoming.assessmentRequest.query);
                    final AssessmentResponse ar = new AssessmentResponse(
                            incoming.assessmentRequest.rid,
                            new AssessmentResponse.Response());
                    ar.response.seqOf = db.getAssessments(incoming.assessmentRequest.query);
                    outgoing.assessmentResponse = ar;
                } else if (incoming.trustRequest != null) {
                    LOG.info("Trust request: {}", incoming.trustRequest.query);
                    final TrustResponse tr = new TrustResponse(
                            incoming.trustRequest.rid,
                            new TrustResponse.Response());
                    tr.response.seqOf = db.getTrust(incoming.trustRequest.query);
                    outgoing.trustResponse = tr;
                } else if (incoming.formatRequest != null) {
                    LOG.info("Format request.");
                    final FormatResponse fr = new FormatResponse();
                    fr.tms = QTMDb.ID;
                    fr.assessment = new BerPrintableString(db.getFormat().get("assessment").getBytes());
                    fr.trust = new BerPrintableString(db.getFormat().get("trust").getBytes());
                    outgoing.formatResponse = fr;
                } else if (incoming.trustResponse != null) {
                    LOG.info("Trust response: {}", incoming.trustResponse.response.seqOf);
                    continue;
                } else if (incoming.assessmentResponse != null) {
                    LOG.info("Assessment response: {}", incoming.assessmentResponse.response.seqOf);
                    continue;
                } else if (incoming.formatResponse != null) {
                    LOG.info("Format response: {}", incoming.formatResponse);
                    continue;
                } else {
                    throw new IOException("Unknown message: " + incoming);
                }

                packet.socket.send(
                        packet.sender.getAddress(),
                        packet.sender.getPort(),
                        encode(outgoing));
            } catch (InterruptedException | IOException e) {
                LOG.warn("Exception", e);
            }
        }
    }
}
