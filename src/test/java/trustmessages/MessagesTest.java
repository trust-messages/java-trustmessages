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
import trustmessages.tms.SLDb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MessagesTest {

    @Test
    public void QTMAssessmentB64PrintQuery() throws IOException {
        final DataResponse ar = new DataResponse();
        ar.provider = new Entity("sometms".getBytes());
        ar.format = new Format(new int[]{1, 1, 1});
        ar.rid = new BerInteger(1);
        ar.type = new BerEnum(1L);
        ar.response = new DataResponse.Response();
        ar.response.seqOf = new SLDb().getAssessments(Utils.getQuery("source = bob AND target = alice"));

        final Message orig = new Message(null, ar, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);
        // System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
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
    public void QTMFromPython() throws IOException {
        final byte[] q = Utils.decode("CgEE");
        final QTM m = new QTM();
        m.decode(new ByteArrayInputStream(q), true);
        assertEquals(m.value, BigInteger.valueOf(4L));
    }

    @Test
    public void QTMAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("ZCATB2JAeC5jb20TB2NAeC5jb20TBnNlbGxlcgIBZAoBBA==");
        final Rating m = new Rating();
        m.decode(new ByteArrayInputStream(q), true);

        final QTM qtm = new QTM();
        qtm.decode(new ByteArrayInputStream(m.value.value));
        assertEquals(qtm.value, BigInteger.valueOf(4L));
    }

    @Test
    public void QTMDataResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y1gTBGViYXkGAikBCgEBAgEBMEZkIRMHYUB4LmNvbRMHYkB4LmN" +
                "vbRMGbGV0dGVyAgID6AoBBGQhEwdjQHguY29tEwdhQHguY29tEwZyZW50ZXICAgPoCgEE");
        final DataResponse m = new DataResponse();
        m.decode(new ByteArrayInputStream(q), true);

        for (Rating a : m.response.seqOf) {
            assertEquals(a.date.value, BigInteger.valueOf(1000L));

            final QTM qtm = new QTM();
            qtm.decode(new ByteArrayInputStream(a.value.value));
            assertEquals(qtm.value, BigInteger.valueOf(4L));
        }
    }

    @Test
    public void qualitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("ZB8TB2FAeC5jb20TB2JAeC5jb20TBWJ1eWVyAgFkAgEB");
        final Rating m = new Rating();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void QTMAssessment() throws IOException {
        final Rating orig = new Rating();
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

        final Rating decoded = new Rating();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()), true);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void quantitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("ZB8TB2FAeC5jb20TB2JAeC5jb20TBWJ1eWVyAgFkAgEB");
        final Rating m = new Rating();
        m.decode(new ByteArrayInputStream(q));
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
        // System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
    }

    @Test
    @Ignore
    public void SLFromPython() throws IOException {
        final byte[] q = Utils.decode("MBUJBQMxRS0xCQUDMkUtMQkFAzdFLTE=");
        final SL m = new SL();
        m.decode(new ByteArrayInputStream(q), true);
    }

    @Test
    public void SLData() throws IOException {
        final Rating orig = new Rating();
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

        final Rating decoded = new Rating();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()));

        assertEquals(orig.toString(), decoded.toString());
        // System.out.println(Base64.getEncoder().encodeToString(osMessage.getArray()));
    }

    @Test
    public void assessmentRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("Yg4CAQEKAQBlBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void assessmentRequest() throws IOException {
        final DataRequest ar = new DataRequest(
                new BerInteger(0),
                new BerEnum(1L),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(ar, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void assessmentResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y1gTBGViYXkGAikBCgEAAgEBMEZkIRMHYkB4LmNvbRMHY0B4LmNvbR" +
                "MGbGV0dGVyAgID6AIBBWQhEwdhQHguY29tEwdiQHguY29tEwZyZW50ZXICAgPoAgEF");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void assessmentResponseQTM() throws IOException {
        final DataResponse ar = new DataResponse();
        ar.provider = new Entity("sometms".getBytes());
        ar.format = new Format(new int[]{1, 1, 1});
        ar.type = new BerEnum(1L);
        ar.rid = new BerInteger(1);
        ar.response = new DataResponse.Response();
        ar.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Rating a = new Rating();
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

        final Message orig = new Message(null, ar, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void assessmentResponseSL() throws IOException {
        final DataResponse ar = new DataResponse();
        ar.provider = new Entity("sometms".getBytes());
        ar.format = new Format(new int[]{1, 1, 1});
        ar.rid = new BerInteger(1);
        ar.type = new BerEnum(1L);
        ar.response = new DataResponse.Response();
        ar.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Rating a = new Rating();
            a.source = new Entity("alice".getBytes());
            a.target = new Entity("you@you.com".getBytes());
            a.service = new Service("seller".getBytes());
            a.date = new BinaryTime(10);

            final SL sl = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));
            final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(32, true);
            sl.encode(osValue);

            a.value = new BerAny(osValue.getArray());
            ar.response.seqOf.add(a);
        }

        final Message orig = new Message(null, ar, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
        // System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
    }

    @Test
    public void trustRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("Yg4CAQEKAQBlBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void trustRequest() throws IOException {
        final DataRequest tr = new DataRequest(
                new BerInteger(0),
                new BerEnum(0L),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(tr, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void trustResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y1gTBGViYXkGAikBCgEAAgEBMEZkI" +
                "RMHYkB4LmNvbRMHY0B4LmNvbRMGbGV0dGVyAgID6AIBBWQhEwdhQHgu" +
                "Y29tEwdiQHguY29tEwZyZW50ZXICAgPoAgEF");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void trustResponse() throws IOException {
        final DataResponse tr = new DataResponse();
        tr.provider = new Entity("sometms".getBytes());
        tr.format = new Format(new int[]{1, 1, 1});
        tr.rid = new BerInteger(1);
        tr.type = new BerEnum(0L);
        tr.response = new DataResponse.Response();
        tr.response.seqOf = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            final Rating t = new Rating();
            t.source = new Entity("bob".getBytes());
            t.target = new Entity("alice".getBytes());
            t.service = new Service("seller".getBytes());
            t.date = new BinaryTime(10);

            final QTM qv = new QTM(0);
            final BerByteArrayOutputStream osValue = new BerByteArrayOutputStream(3, true);
            qv.encode(osValue, true);
            t.value = new BerAny(osValue.getArray());

            tr.response.seqOf.add(t);
        }

        final Message orig = new Message(null, tr, null, null, null);

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
        final Message orig = new Message(null, null, new FormatRequest(), null, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(10, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void formatResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("YVkGAioDEytIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIGFzc2Vzc21lbnQgdmFsdWVzEyZ" +
                "IZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIHRydXN0IHZhbHVlcw==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void formatResponse() throws IOException {
        final FormatResponse fr = new FormatResponse();
        fr.assessment = new BerPrintableString("Assessment format".getBytes());
        fr.trust = new BerPrintableString("Trust format".getBytes());
        fr.format = new Format(new int[]{1, 2, 3});
        final Message orig = new Message(null, null, null, fr, null);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);
        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void faultFromPython() throws IOException {
        final byte[] q = Utils.decode("ZxoKAQATFXNvbWV0aGluZyB3ZW50IHdyb25nIQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    @Test
    public void fault() throws IOException {
        final Fault f = new Fault(new BerEnum(0), new BerPrintableString("something went wrong!".getBytes()));
        final Message orig = new Message(null, null, null, null, f);
        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));
        assertEquals(orig.toString(), decoded.toString());
    }
}
