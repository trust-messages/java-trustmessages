package trustmessages;

import org.junit.Test;
import trustmessages.asn.*;
import trustmessages.tms.InMemoryTrustDb;
import trustmessages.tms.QTMDb;
import trustmessages.tms.SLDb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DbTest {
    private static final InMemoryTrustDb QTM_DB = new QTMDb();
    private static final InMemoryTrustDb SL_DB = new SLDb();


    @Test
    public void QTMTrustQuery() {
        final Query query = Utils.getQuery("(service = seller OR service = buyer) AND target = david");
        final List<Trust> trust = QTM_DB.getTrust(query);
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

    @Test
    public void QTMAssessmentQuery() {
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

    @Test
    public void SLTrustQuery() {
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

    @Test
    public void SLAssessmentQuery() {
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
