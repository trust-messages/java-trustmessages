package trustmessages.tms;

import org.openmuc.jasn1.ber.types.string.BerPrintableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustmessages.Utils;
import trustmessages.asn.*;
import trustmessages.socket.IncomingDataHandler;
import trustmessages.socket.TrustSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrustService implements Runnable, IncomingDataHandler {
    private final static Logger LOG = LoggerFactory.getLogger(TrustService.class);

    private static class TrustMessage {
        final static TrustMessage SHUTDOWN = new TrustMessage(null, null, null);

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
    private final InMemoryTrustDb db;
    private final String name;

    public TrustService(String service, String name) {
        this.name = name;

        if (service.equalsIgnoreCase("qtm")) {
            db = new QTMDb();
        } else if (service.equalsIgnoreCase("sl")) {
            db = new SLDb();
        } else {
            throw new IllegalArgumentException("Unknown service: " + service);
        }
    }

    @Override
    public void handle(TrustSocket socket, InetSocketAddress sender, byte[] data) {
        try {
            queue.put(new TrustMessage(socket, sender, data));
        } catch (InterruptedException e) {
            LOG.error("An error occurred while enqueueing a request.", e);
        }
    }

    public void shutDown() {
        try {
            queue.put(TrustMessage.SHUTDOWN);
        } catch (InterruptedException e) {
            LOG.error("An error occurred while enqueueing a request.", e);
        }
    }

    public void run() {
        LOG.info("Running Trust Service with {}", db.getClass().getSimpleName());

        while (true) {
            try {
                final TrustMessage packet = queue.take();
                LOG.debug("Processing: {} [size={}B]", packet, packet.data.length);

                if (packet == TrustMessage.SHUTDOWN) {
                    LOG.info("[SERVICE]: Shutdown");
                    return;
                }

                final Message incoming = Utils.decode(packet.data);
                LOG.debug("Decoded: {}", incoming);

                final Message outgoing = new Message();

                if (incoming.assessmentRequest != null) {
                    LOG.info("[assessment-request] ({}B): {}", packet.data.length, incoming.assessmentRequest.query);
                    final AssessmentResponse ar = new AssessmentResponse(
                            new Entity(name.getBytes()),
                            db.getId(),
                            incoming.assessmentRequest.rid,
                            new AssessmentResponse.Response());
                    ar.response.seqOf = db.getAssessments(incoming.assessmentRequest.query);
                    outgoing.assessmentResponse = ar;
                } else if (incoming.trustRequest != null) {
                    LOG.info("[trust-request] ({}B): {}", packet.data.length, incoming.trustRequest.query);
                    final TrustResponse tr = new TrustResponse(
                            new Entity(name.getBytes()),
                            db.getId(),
                            incoming.trustRequest.rid,
                            new TrustResponse.Response());
                    tr.response.seqOf = db.getTrust(incoming.trustRequest.query);
                    outgoing.trustResponse = tr;
                } else if (incoming.formatRequest != null) {
                    LOG.info("[format-request] ({}B)", packet.data.length);
                    final FormatResponse fr = new FormatResponse();
                    fr.format = db.getId();
                    fr.assessment = new BerPrintableString(db.getFormat().get("assessment").getBytes());
                    fr.trust = new BerPrintableString(db.getFormat().get("trust").getBytes());
                    outgoing.formatResponse = fr;
                } else if (incoming.trustResponse != null) {
                    LOG.info("[trust-response] ({}) {}", incoming.trustResponse.provider,
                            incoming.trustResponse.response.seqOf);
                    continue;
                } else if (incoming.assessmentResponse != null) {
                    LOG.info("[assessment-response] ({}) {}", incoming.assessmentResponse.provider,
                            incoming.assessmentResponse.response.seqOf);
                    continue;
                } else if (incoming.formatResponse != null) {
                    LOG.info("[format-response] {}", incoming.formatResponse);
                    continue;
                } else {
                    LOG.warn("[unknown-message] {}", incoming);
                }

                packet.socket.send(
                        packet.sender.getAddress(),
                        packet.sender.getPort(),
                        Utils.encode(outgoing));
            } catch (InterruptedException | IOException e) {
                LOG.warn("ERROR: {}", e.getLocalizedMessage(), e);
            }
        }
    }
}
