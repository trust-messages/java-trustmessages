package com.david;

import com.david.messages.QTM;
import com.david.messages.SL;
import com.david.messages.Assessment;
import com.david.messages.Trust;
import com.david.tms.InMemoryTrustDb;
import com.david.tms.QTMDb;
import com.david.tms.SLDb;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class DbTest extends TestCase {
    private static final InMemoryTrustDb QTM_DB = new QTMDb();
    private static final InMemoryTrustDb SL_DB = new SLDb();

    public DbTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DbTest.class);
    }

    private static class ValueComparator<T extends Comparable<? super T>> implements Comparator<T> {
        public int compare(T a, T b) {
            return a.compareTo(b);
        }
    }

    public void testComparator() {
        final ValueComparator vc = new ValueComparator<>();

        final String s1 = "aaa", s2 = "bbbb";
        final Integer i1 = 1, i2 = 2;
        s1.compareTo(s2);
        i1.compareTo(i2);

        final Comparator<Comparable> cmp = Comparator.naturalOrder();





    }

    public void testQTMTrustQuery() {
        final List<Trust> trust = QTM_DB.getTrust(
                Utils.getQuery("(service = seller OR service = buyer) AND target = david"));
        assertTrue(trust.size() > 0);

        trust.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david"));

            try {
                new QTM().decode(new ByteArrayInputStream(t.value.value), true);
            } catch (IOException e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    public void testQTMAssessmentQuery() {
        final List<Assessment> assessments = QTM_DB.getAssessments(
                Utils.getQuery("(service = seller OR service = buyer) AND (target = david OR target = alice)"));
        assertTrue(assessments.size() > 0);

        assessments.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david") || t.target.toString().equals("alice"));

            try {
                new QTM().decode(new ByteArrayInputStream(t.value.value), true);
            } catch (IOException e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    public void testSLTrustQuery() {
        final List<Trust> trust = SL_DB.getTrust(
                Utils.getQuery("(service = seller OR service = buyer) AND target = david"));
        assertTrue(trust.size() > 0);

        trust.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david"));

            try {
                new SL().decode(new ByteArrayInputStream(t.value.value), false);
            } catch (IOException e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    public void testSLAssessmentQuery() {
        final List<Assessment> assessments = SL_DB.getAssessments(
                Utils.getQuery("(service = seller OR service = buyer) AND (target = david OR target = alice)"));
        assertTrue(assessments.size() > 0);

        assessments.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david") || t.target.toString().equals("alice"));

            try {
                new SL().decode(new ByteArrayInputStream(t.value.value), false);
            } catch (IOException e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }
}
