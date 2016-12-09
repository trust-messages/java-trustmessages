package com.david.tms;

import com.david.messages.*;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QTMDb extends InMemoryTrustDb {
    private static final List<Trust> TRUST = new ArrayList<>();
    private static final List<Assessment> ASSESSMENTS = new ArrayList<>();
    private static final Map<String, String> FORMAT = new HashMap<>();

    private static final Iterator<Integer> VALUES = IntStream.iterate(0, i -> (i + 1) % 5).iterator();
    private static final SystemIdentity ID = new SystemIdentity(new int[]{1, 1, 1});

    static {
        for (String target : USERS) {
            for (String service : SERVICES) {
                final Trust t = new Trust();
                t.tms = ID;
                t.target = new Entity(target.getBytes());
                t.service = new Service(service.getBytes());
                t.date = new BinaryTime(TIME.next());

                final QTM v = new QTM(VALUES.next());
                final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(3, true);
                try {
                    v.encode(baos, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                t.value = new BerAny(baos.getArray());
                TRUST.add(t);
            }
        }

        for (String source : USERS) {
            for (String target : USERS) {
                if (source.equals(target)) {
                    continue;
                }

                for (String service : SERVICES) {
                    final Assessment a = new Assessment();
                    a.tms = ID;
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
    public SystemIdentity getId() {
        return ID;
    }

    @Override
    public List<Assessment> getAssessments(Query query) {
        return ASSESSMENTS.stream().filter(createAssessmentPredicate(query)).collect(Collectors.toList());
    }

    @Override
    public List<Trust> getTrust(Query query) {
        return TRUST.stream().filter(createTrustPredicate(query)).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getFormat() {
        return FORMAT;
    }
}
