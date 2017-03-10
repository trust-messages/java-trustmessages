package trustmessages;

import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trustmessages.asn.DataRequest;
import trustmessages.asn.FormatRequest;
import trustmessages.asn.Message;
import trustmessages.asn.Query;
import trustmessages.socket.TrustSocket;
import trustmessages.tms.TrustService;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Node {
    private final static Logger LOG = LoggerFactory.getLogger(Node.class);

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            LOG.info("Usage: {} <port> qtm|sl tms-name", Node.class.getSimpleName());
            System.exit(1);
        }

        final int myPort;
        try {
            myPort = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            LOG.error("Invalid port number: {}", args[0]);
            System.exit(1);
            return;
        }

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final TrustService service;
        try {
            service = new TrustService(args[1], args[2]);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid trust service name: {}", e.getLocalizedMessage());
            System.exit(1);
            return;
        }

        final TrustSocket socket = new TrustSocket(myPort, service);

        executor.submit(service);
        executor.submit(socket);

        final Scanner s = new Scanner(System.in);
        final Random random = new Random();
        while (true) {
            String line, address = null, verb, criteria;
            String[] commands;
            int port = -1;
            try {
                line = s.nextLine().trim();

                if (line.equalsIgnoreCase("exit")) {
                    socket.shutDown();
                    service.shutDown();
                    break;
                }

                commands = line.split(" ", 4);
                address = commands[0];
                port = Integer.parseInt(commands[1]);
                verb = commands[2];
                criteria = commands.length == 4 ? commands[3] : null;

                if (verb.equalsIgnoreCase("connect")) {
                    socket.connect(InetAddress.getByName(address), port);
                } else if (verb.equalsIgnoreCase("disconnect")) {
                    socket.disconnect(InetAddress.getByName(address), port);
                } else if (verb.equalsIgnoreCase("treq") ||
                        verb.equalsIgnoreCase("areq")) {
                    final Message request = new Message();
                    final Query query = Utils.getQuery(criteria);
                    request.dataRequest = new DataRequest(
                            new BerInteger(random.nextInt()),
                            // trust or assessment request?
                            new BerEnum(verb.equalsIgnoreCase("treq") ? 0L : 1L),
                            query);
                    socket.send(InetAddress.getByName(address), port, Utils.encode(request));
                } else {
                    final Message request = new Message();
                    request.formatRequest = new FormatRequest();
                    socket.send(InetAddress.getByName(address), port, Utils.encode(request));
                }
            } catch (IOException e) {
                LOG.warn("Invalid address: {}", address, e);
            } catch (NumberFormatException e) {
                LOG.warn("Invalid port number: {}", port, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.warn("Too few arguments.", e);
            } catch (Exception e) {
                LOG.warn("General error: {}", e.getMessage(), e);
            }
        }
        executor.awaitTermination(1, TimeUnit.SECONDS);
        executor.shutdownNow();
    }
}
