package com.david;

import com.david.messages.*;
import org.openmuc.jasn1.ber.types.BerInteger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node {
    public static void main(String[] args) throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final TrustService service = new TrustService();
        final TrustSocket socket = new TrustSocket(6000, service);

        executor.submit(service);
        executor.submit(socket);

        final Scanner s = new Scanner(System.in);
        final Random random = new Random();
        while (true) {
            try {
                final String[] commands = s.nextLine().split(" ", 4);
                final String address = commands[0];
                final int port = Integer.parseInt(commands[1]);
                final String verb = commands[2];
                final String criteria = commands.length == 4 ? commands[3] : null;

                if (criteria == null) {
                    System.out.println("TODO: handle connect verb");
                    continue;
                }

                final Message request = new Message();
                final Query query = Utils.getQuery(criteria);

                if (verb.equalsIgnoreCase("treq")) {
                    request.trustRequest = new TrustRequest(new BerInteger(random.nextInt()), query);
                } else if (verb.equalsIgnoreCase("areq")) {
                    request.assessmentRequest = new AssessmentRequest(new BerInteger(random.nextInt()), query);
                } else {
                    request.formatRequest = new FormatRequest();
                }

                socket.send(InetAddress.getByName(address), port, Utils.encode(request));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
