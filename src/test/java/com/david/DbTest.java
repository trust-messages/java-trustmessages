package com.david;

import com.david.messages.Assessment;
import com.david.messages.Trust;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

public class DbTest extends TestCase {
    private static final InMemoryTrustDb DB = new QTMDb();

    public DbTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DbTest.class);
    }

    public void testQTMTrustQuery() {
        final List<Trust> trust = DB.getTrust(
                Utils.getQuery("(service = seller OR service = buyer) AND target = david"));
        assertTrue(trust.size() > 0);

        trust.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david"));
        });
    }

    public void testQTMAssessmentQuery() {
        final List<Assessment> assessments = DB.getAssessments(
                Utils.getQuery("(service = seller OR service = buyer) AND (target = david OR target = alice)"));
        assertTrue(assessments.size() > 0);

        assessments.forEach(t -> {
            assertTrue(t.service.toString().equals("seller") || t.service.toString().equals("buyer"));
            assertTrue(t.target.toString().equals("david") || t.target.toString().equals("alice"));
        });
    }
}
