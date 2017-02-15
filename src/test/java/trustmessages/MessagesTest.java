package trustmessages;

import org.junit.Ignore;
import org.junit.Test;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;
import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerReal;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;
import trustmessages.asn.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MessagesTest {

    @Test
    public void decimal() throws IOException {
        final BerReal orig = new BerReal(0.7);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100,
                true);
        orig.encode(baos);

        final BerReal decoded = new BerReal();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.value, decoded.value, 0.001);
    }

    @Test
    public void quantitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("aR8TB2FAeC5jb20TB2JAeC5jb20TBWJ1eWVyAgFkAgEB");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    @Test
    public void QTMFromPython() throws IOException {
        final byte[] q = Utils.decode("CgEE");
        final QTM m = new QTM();
        m.decode(new ByteArrayInputStream(q), true);
        assertEquals(m.value, BigInteger.valueOf(4L));
    }

    @Test
    public void QTMAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("aSATB2FAeC5jb20TB2JAeC5jb20TBnJlbnRlcgIBZAoBBA==");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);

        final QTM qtm = new QTM();
        qtm.decode(new ByteArrayInputStream(m.value.value));
        assertEquals(qtm.value, BigInteger.valueOf(4L));
    }

    @Test
    public void SL() throws IOException {
        final SL orig = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final SL decoded = new SL();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
        assertEquals(orig.b.value, decoded.b.value, 0.001);
        assertEquals(orig.d.value, decoded.d.value, 0.001);
        assertEquals(orig.u.value, decoded.u.value, 0.001);
    }

    @Test
    @Ignore
    public void SLFromPython() throws IOException {
        final byte[] q = Utils.decode("MBUJBQMxRS0xCQUDMkUtMQkFAzdFLTE=");
        final SL m = new SL();
        m.decode(new ByteArrayInputStream(q), true);
    }

    @Test
    public void QTM() throws IOException {
        final QTM orig = new QTM(1);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(3, true);
        orig.encode(baos, true);

        final QTM decoded = new QTM();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), true);

        assertEquals(orig.value, decoded.value);
    }

    @Test
    public void quantitativeAssessment() throws IOException {
        final Assessment orig = new Assessment();
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

    @Test
    public void qualitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("aScTB2NAeC5jb20TB2FAeC5jb20TBnNlbGxlcgIBZBMIZGlzdHJ1c3Q");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    @Test
    public void qualitativeAssessment() throws IOException {
        final Assessment orig = new Assessment();
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

    @Test
    public void assessmentRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("ZgsCAQFkBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void assessmentRequest() throws IOException {
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

    @Test
    public void assessmentResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Z1UTBGViYXkGAikBAgEBMEZpIRMHYkB4LmNvbRMHY0B4LmNvbRMGbGV0dGVyAgID6AIBBWkhEwdhQHguY29tEwdiQHguY29tEwZyZW50ZXICAgPoAgEF");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void assessmentResponse() throws IOException {
        final AssessmentResponse ar = new AssessmentResponse();
        ar.provider = new Entity("sometms".getBytes());
        ar.format = new Format(new int[]{1, 1, 1});
        ar.rid = new BerInteger(1);
        ar.response = new AssessmentResponse.Response();
        ar.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Assessment a = new Assessment();
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

    @Test
    public void trustRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("YgwCAhOIZAYKAQVCAVA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void trustRequest() throws IOException {
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

    @Test
    public void trustResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y04TBGViYXkGAikBAgMBEXAwPWgdEwdjQHguY29tEwVidXllcgICB9ATB25ldXRyYWxoHBMHYUB4LmNvbRMGc2VsbGVyAgIH0BMFdHJ1c3Q=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void trustResponse() throws IOException {
        final TrustResponse tr = new TrustResponse();
        tr.provider = new Entity("sometms".getBytes());
        tr.format = new Format(new int[]{1, 1, 1});
        tr.rid = new BerInteger(1);
        tr.response = new TrustResponse.Response();
        tr.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Trust t = new Trust();
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

    @Test
    public void formatRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("QAA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void formatRequest() throws IOException {
        final Message orig = new Message(null, null, null, null, new FormatRequest(), null, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(10, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void formatResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("YVkGAioDEytIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIGFzc2Vzc21lbnQgdmFsdWVzEyZIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIHRydXN0IHZhbHVlcw==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void formatResponse() throws IOException {
        final FormatResponse fr = new FormatResponse();
        fr.assessment = new BerPrintableString("Assessment format".getBytes());
        fr.trust = new BerPrintableString("Trust format".getBytes());
        fr.format = new Format(new int[]{1, 2, 3});
        final Message orig = new Message(null, null, null, null, null, fr, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);
        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void faultFromPython() throws IOException {
        final byte[] q = Utils.decode("ahoKAQATFXNvbWV0aGluZyB3ZW50IHdyb25nIQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void fault() throws IOException {
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
