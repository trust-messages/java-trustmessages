package trustmessages.tms;

import org.openmuc.jasn1.ber.ReverseByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.jasn1.ber.types.BerReal;
import trustmessages.asn.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class SLDb extends InMemoryTrustDb {
    private static final List<Rating> TRUST = new ArrayList<>();
    private static final List<Rating> ASSESSMENTS = new ArrayList<>();

    private static final Map<Type, String> FORMAT = new HashMap<>();
    private static final Map<Type, Format> ID = new HashMap<>();
    private static final Random RANDOM = new Random();

    private static final Iterator<Triple> VALUES = Stream.generate(() -> {
        final double b = RANDOM.nextDouble();
        final double d = RANDOM.nextDouble() * b;
        return new Triple(b, d, 1d - b - d);
    }).iterator();

    private static class Triple {
        final BerReal b, d, u;

        private Triple(double b, double d, double u) {
            this.b = new BerReal(b);
            this.d = new BerReal(d);
            this.u = new BerReal(u);
        }
    }

    static {
        for (String source : USERS) {
            for (String target : USERS) {
                for (String service : SERVICES) {
                    final Rating t = new Rating();
                    t.source = new Entity(source.getBytes());
                    t.target = new Entity(target.getBytes());
                    t.service = new Service(service.getBytes());
                    t.date = new BinaryTime(TIME.next());
                    final Triple tv = VALUES.next();
                    final SL v = new SL(tv.b, tv.d, tv.u);
                    final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(64, true);

                    try {
                        v.encode(baos, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    t.value = new BerOctetString(baos.getArray());
                    TRUST.add(t);
                }
            }
        }

        for (String source : USERS) {
            for (String target : USERS) {
                if (source.equals(target)) {
                    continue;
                }

                for (String service : SERVICES) {
                    final Rating a = new Rating();
                    a.source = new Entity(source.getBytes());
                    a.target = new Entity(target.getBytes());
                    a.service = new Service(service.getBytes());
                    a.date = new BinaryTime(TIME.next());
                    final Triple tv = VALUES.next();
                    final SL v = new SL(tv.b, tv.d, tv.u);
                    final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(64, true);

                    try {
                        v.encode(baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    a.value = new BerOctetString(baos.getArray());
                    ASSESSMENTS.add(a);
                }
            }
        }
        ID.put(Type.TRUST, new Format(new int[]{2, 2, 2}));
        ID.put(Type.ASSESSMENT, ID.get(Type.TRUST));
        FORMAT.put(Type.TRUST, "ValueFormat DEFINITIONS ::= BEGIN ValueFormat ::= " +
                "SEQUENCE { b REAL, d REAL, u REAL } END");
        FORMAT.put(Type.ASSESSMENT, FORMAT.get(Type.TRUST));
    }

    @Override
    public Map<Type, Format> getId() {
        return ID;
    }

    @Override
    public Map<Type, String> getFormat() {
        return FORMAT;
    }

    @Override
    protected Stream<Rating> allAssessments() {
        return ASSESSMENTS.stream();
    }

    @Override
    protected Stream<Rating> allTrust() {
        return TRUST.stream();
    }
}
