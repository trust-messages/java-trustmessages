package trustmessages;

import org.openmuc.jasn1.ber.ReverseByteArrayOutputStream;
import trustmessages.asn.Message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Measurement {

    public static final int ITERATIONS = 500000;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.printf("Usage: %s %s%n", args[0], "encode|decode");
            System.exit(1);
        }

        switch (args[0]) {
            case "encode":
                encode(ITERATIONS, "..");
                break;
            case "decode":
                decode(ITERATIONS, "..");
                break;
            default:
                System.out.printf("Usage: %s %s%n", args[0], "encode|decode");
                System.exit(1);
                break;
        }
    }

    public static void encode(int iterations, String pathname) throws IOException {
        System.out.println("Filename, Average, Total, Bytes");

        for (File file : getFileNames(pathname)) {
            final byte[] content = Files.readAllBytes(file.toPath());

            final Message message = new Message();
            final ByteArrayInputStream is = new ByteArrayInputStream(content);
            message.decode(is);

            double total = 0;

            for (int i = 0; i < iterations; i++) {
                final ReverseByteArrayOutputStream os = new ReverseByteArrayOutputStream(content.length, false);

                final long start = System.nanoTime();
                message.encode(os);
                final long stop = System.nanoTime();

                assert Arrays.equals(os.getArray(), content);

                total += stop - start;
            }

            // from nano- to seconds
            total = total / Math.pow(10, 9);

            System.out.printf("%s, %f, %f, %d%n",
                    file.toPath(), total / iterations, total, content.length);
        }
    }

    public static void decode(int iterations, String pathname) throws IOException {
        System.out.println("Filename, Average, Total, Bytes");

        for (File file : getFileNames(pathname)) {
            final byte[] content = Files.readAllBytes(file.toPath());

            double total = 0;

            for (int i = 0; i < iterations; i++) {
                final Message message = new Message();
                final ByteArrayInputStream is = new ByteArrayInputStream(content);

                final long start = System.nanoTime();
                message.decode(is);
                final long stop = System.nanoTime();

                total += stop - start;
            }

            // from nano- to seconds
            total = total / Math.pow(10, 9);

            System.out.printf("%s, %f, %f, %d%n",
                    file.toPath(), total / iterations, total, content.length);
        }
    }

    public static File[] getFileNames(String pathname) {
        final File dir = new File(pathname);
        return dir.listFiles((dir1, name) -> name.endsWith(".ber"));
    }
}
