package trustmessages.tms;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;
import trustmessages.asn.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class QTMDb extends InMemoryTrustDb {
    private static final List<Rating> TRUST = new ArrayList<>();
    private static final List<Rating> ASSESSMENTS = new ArrayList<>();
    private static final Map<String, String> FORMAT = new HashMap<>();

    private static final Iterator<Integer> VALUES = IntStream.iterate(0, i -> (i + 1) % 5).iterator();
    private static final Format ID = new Format(new int[]{1, 1, 1});

    static {
        for (String source : USERS) {
            for (String target : USERS) {
                for (String service : SERVICES) {
                    final Rating t = new Rating();
                    t.source = new Entity(source.getBytes());
                    t.target = new Entity(target.getBytes());
                    t.service = new Service(service.getBytes());
                    t.date = new BinaryTime(TIME.next());

                    final QTM v = new QTM(VALUES.next());
                    final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(3, true);
                    try {
                        v.encode(baos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    t.value = new BerAny(baos.getArray());
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

                    final QTM v = new QTM(VALUES.next());
                    final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(3, true);
                    try {
                        v.encode(baos, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    a.value = new BerAny(baos.getArray());
                    ASSESSMENTS.add(a);
                }
            }
        }
        FORMAT.put("trust", "ValueFormat DEFINITIONS ::= BEGIN ValueFormat ::= " +
                "ENUMERATED { very-bad (0), bad (1), neutral (2), good (3), very-good (4) } END");
        FORMAT.put("assessment", FORMAT.get("trust"));
    }

    @Override
    public Format getId() {
        return ID;
    }

    @Override
    public Map<String, String> getFormat() {
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
