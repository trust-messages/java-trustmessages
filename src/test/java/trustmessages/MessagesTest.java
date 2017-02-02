package trustmessages;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;
import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerReal;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;
import trustmessages.asn.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessagesTest extends TestCase {
    public MessagesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MessagesTest.class);
    }

    public void testDecimal() throws IOException {
        final BerReal orig = new BerReal(0.7);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100,
                true);
        orig.encode(baos);

        final BerReal decoded = new BerReal();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.value, decoded.value, 0.001);
    }

    public void testQuantitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("aSMGAikBEwdhQHguY29tEwdiQHguY29tEwVidXllcgIBZAIBAQ==");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    public void testSL() throws IOException {
        final SL orig = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final SL decoded = new SL();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        System.out.println(orig);
        System.out.println(decoded);

        assertEquals(orig.toString(), decoded.toString());
        assertEquals(orig.b.value, decoded.b.value, 0.001);
        assertEquals(orig.d.value, decoded.d.value, 0.001);
        assertEquals(orig.u.value, decoded.u.value, 0.001);
    }

    public void testQTM() throws IOException {
        final QTM orig = new QTM(1);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(3, true);
        orig.encode(baos, true);

        final QTM decoded = new QTM();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), true);

        assertEquals(orig.value, decoded.value);
    }

    public void testQuantitativeAssessment() throws IOException {
        final Assessment orig = new Assessment();
        orig.tms = new SystemIdentity(new int[]{1, 1, 1});
        orig.source = new Entity("alice".getBytes());
        orig.target = new Entity("bob".getBytes());
        orig.service = new Service("seller".getBytes());
        orig.date = new BinaryTime(10);

        final SL sl = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));
        final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(32, true);
        sl.encode(osValue);

        orig.value = new BerAny(osValue.getArray());

        final BerByteArrayOutputStream osMessage = new BerByteArrayOutputStream(100, true);
        orig.encode(osMessage, true);

        final Assessment decoded = new Assessment();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testQualitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("aSsGAlICEwdjQHguY29tEwdhQHguY29tEwZzZWxsZXICAWQTCGRpc3RydXN0");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    public void testQualitativeAssessment() throws IOException {
        final Assessment orig = new Assessment();
        orig.tms = new SystemIdentity(new int[]{1, 1, 1});
        orig.source = new Entity("alice".getBytes());
        orig.target = new Entity("bob".getBytes());
        orig.service = new Service("seller".getBytes());
        orig.date = new BinaryTime(10);

        final QTM qv = new QTM(0);
        final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(3, true);
        qv.encode(osValue, true);
        orig.value = new BerAny(osValue.getArray());

        final BerByteArrayOutputStream osMessage = new BerByteArrayOutputStream(32, true);
        orig.encode(osMessage, true);

        final Assessment decoded = new Assessment();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()), true);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testAssessmentRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("ZgsCAQFkBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testAssessmentRequest() throws IOException {
        final AssessmentRequest ar = new AssessmentRequest(
                new BerInteger(0),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(ar, null, null, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testAssessmentResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Z1MCAQEwTmklBgIpARMHYkB4LmNvbRMHY0B4LmNvbRMGbGV0dGVyAgID6AIBBWklBgIpARMHYUB4LmNvbRMHYkB4LmNvbRMGcmVudGVyAgID6AIBBQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testAssessmentResponse() throws IOException {
        final AssessmentResponse ar = new AssessmentResponse();
        ar.rid = new BerInteger(1);
        ar.response = new AssessmentResponse.Response();
        ar.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Assessment a = new Assessment();
            a.tms = new SystemIdentity(new int[]{1, 1, 1});
            a.source = new Entity("alice".getBytes());
            a.target = new Entity("you@you.com".getBytes());
            a.service = new Service("seller".getBytes());
            a.date = new BinaryTime(10);

            final QTM qv = new QTM(0);
            final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(3, true);
            qv.encode(osValue, true);
            a.value = new BerAny(osValue.getArray());

            ar.response.seqOf.add(a);
        }

        final Message orig = new Message(null, ar, null, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testTrustRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("YgwCAhOIZAYKAQVCAVA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testTrustRequest() throws IOException {
        final TrustRequest tr = new TrustRequest(
                new BerInteger(0),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(null, null, tr, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testTrustResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y0wCAwERcDBFaCEGAikBEwdjQHguY29tEwVidXllcgICB9ATB25ldXRyYWxoIAYCKQETB2FAeC5jb20TBnNlbGxlcgICB9ATBXRydXN0");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testTrustResponse() throws IOException {
        final TrustResponse tr = new TrustResponse();
        tr.rid = new BerInteger(1);
        tr.response = new TrustResponse.Response();
        tr.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Trust t = new Trust();
            t.tms = new SystemIdentity(new int[]{1, 1, 1});
            t.target = new Entity("alice".getBytes());
            t.service = new Service("seller".getBytes());
            t.date = new BinaryTime(10);

            final QTM qv = new QTM(0);
            final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(3, true);
            qv.encode(osValue, true);
            t.value = new BerAny(osValue.getArray());

            tr.response.seqOf.add(t);
        }

        final Message orig = new Message(null, null, null, tr, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testFormatRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("QAA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testFormatRequest() throws IOException {
        final Message orig = new Message(null, null, null, null, new FormatRequest(), null, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(10, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    public void testFormatResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("YVkGAioDEytIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIGFzc2Vzc21lbnQgdmFsdWVzEyZIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIHRydXN0IHZhbHVlcw==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testFormatResponse() throws IOException {
        final FormatResponse fr = new FormatResponse();
        fr.assessment = new BerPrintableString("Assessment format".getBytes());
        fr.trust = new BerPrintableString("Trust format".getBytes());
        fr.tms = new SystemIdentity(new int[]{1, 2, 3});
        final Message orig = new Message(null, null, null, null, null, fr, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);
        assertEquals(orig.toString(), decoded.toString());
    }

    public void testFaultFromPython() throws IOException {
        final byte[] q = Utils.decode("ahoKAQATFXNvbWV0aGluZyB3ZW50IHdyb25nIQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testFault() throws IOException {
        final Fault f = new Fault(new BerEnum(0), new BerPrintableString("something went wrong!".getBytes()));
        final Message orig = new Message(null, null, null,
                null, null, null, f);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));
        assertEquals(orig.toString(), decoded.toString());
    }
}
