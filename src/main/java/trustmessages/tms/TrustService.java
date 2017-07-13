package trustmessages.tms;

import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustmessages.Utils;
import trustmessages.asn.DataResponse;
import trustmessages.asn.Entity;
import trustmessages.asn.FormatResponse;
import trustmessages.asn.Message;
import trustmessages.socket.IncomingDataHandler;
import trustmessages.socket.TrustSocket;

import java.io.IOException;
import java.math.BigInteger;
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

                final Message.Payload payload = Utils.decode(packet.data).payload;

                LOG.debug("Decoded: {}", payload);

                final Message outgoing = new Message();
                outgoing.version = new BerInteger(1L);
                outgoing.payload = new Message.Payload();

                if (payload.dataRequest != null) {
                    LOG.info("[data-request] ({}B): {} / {}", packet.data.length, payload.dataRequest.type,
                            payload.dataRequest.query);
                    final DataResponse response = new DataResponse(
                            payload.dataRequest.rid,
                            db.getId().get(Type.fromEnum(payload.dataRequest.type)),
                            payload.dataRequest.type,
                            new Entity(name.getBytes()),
                            new DataResponse.Response());
                    // querying for trust or assessments?
                    response.response.seqOf = payload.dataRequest.type.value.equals(BigInteger.ZERO) ?
                            db.getTrust(payload.dataRequest.query) :
                            db.getAssessments(payload.dataRequest.query);
                    outgoing.payload.dataResponse = response;
                } else if (payload.formatRequest != null) {
                    LOG.info("[format-request] ({}B)", packet.data.length);
                    final FormatResponse fr = new FormatResponse();
                    fr.rid = new BerInteger(payload.formatRequest.value);
                    fr.assessmentId = db.getId().get(Type.ASSESSMENT);
                    fr.assessmentDef = new BerPrintableString(db.getFormat().get(Type.ASSESSMENT).getBytes());
                    fr.trustId = db.getId().get(Type.TRUST);
                    fr.trustDef = new BerPrintableString(db.getFormat().get(Type.TRUST).getBytes());
                    outgoing.payload.formatResponse = fr;
                } else if (payload.dataResponse != null) {
                    LOG.info("[data-response] ({}B): {} / ({}) {}",
                            packet.data.length,
                            payload.dataResponse.type,
                            payload.dataResponse.provider,
                            payload.dataResponse.response.seqOf);
                    continue;
                } else if (payload.formatResponse != null) {
                    LOG.info("[format-response] {}", payload.formatResponse);
                    continue;
                } else {
                    LOG.warn("[unknown-message] {}", payload);
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
