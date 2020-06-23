package trustmessages;

import org.junit.Ignore;
import org.junit.Test;
import org.openmuc.jasn1.ber.ReverseByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerOctetString;
import org.openmuc.jasn1.ber.types.BerReal;
import org.openmuc.jasn1.ber.types.string.BerIA5String;
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
        ar.format = new Format(new int[]{1, 1, 1});
        ar.rid = new BerInteger(1);
        ar.type = new BerEnum(1L);
        ar.response = new DataResponse.Response();
        ar.response.seqOf = new SLDb().getAssessments(Utils.getQuery("source = bob AND target = alice"));

        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, ar, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);
//         System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
    }

    @Test
    public void QTM() throws IOException {
        final QTM orig = new QTM(1);

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(3, true);
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
        final byte[] q = Utils.decode("ZCIWB2JAeC5jb20WB2NAeC5jb20WBnNlbGxlcgIBZAQDCgEE");
        final Rating m = new Rating();
        m.decode(new ByteArrayInputStream(q), true);

        final QTM qtm = new QTM();
        qtm.decode(new ByteArrayInputStream(m.value.value));
        assertEquals(qtm.value, BigInteger.valueOf(4L));
    }

    @Test
    public void QTMDataResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("Y1YCAQEGAikBCgEBMEpkIxYHY0B4LmNvbRYHYUB4LmNvbRYGbGV0dGVyAgID6AQDCgEEZ" +
                "CMWB2JAeC5jb20WB2NAeC5jb20WBnJlbnRlcgICA+gEAwoBBA==");
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
        final byte[] q = Utils.decode("ZCIWB2JAeC5jb20WB2NAeC5jb20WBnNlbGxlcgIBZAQDCgEE");
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
        final ReverseByteArrayOutputStream osValue = new ReverseByteArrayOutputStream(3, true);
        qv.encode(osValue, true);
        orig.value = new BerOctetString(osValue.getArray());

        final ReverseByteArrayOutputStream osMessage = new ReverseByteArrayOutputStream(32, true);
        orig.encode(osMessage, true);

        final Rating decoded = new Rating();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()), true);

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void quantitativeAssessmentFromPython() throws IOException {
        final byte[] q = Utils.decode("ZDUWB2NAeC5jb20WB2FAeC5jb20WBWJ1eWVyA" +
                "gFkBBcwFQkFAzFFLTEJBQMyRS0xCQUDN0UtMQ==");
        final Rating m = new Rating();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void SL() throws IOException {
        final SL orig = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
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
        final ReverseByteArrayOutputStream osValue = new ReverseByteArrayOutputStream(32, true);
        sl.encode(osValue);

        orig.value = new BerOctetString(osValue.getArray());

        final ReverseByteArrayOutputStream osMessage = new ReverseByteArrayOutputStream(100, true);
        orig.encode(osMessage, true);

        final Rating decoded = new Rating();
        decoded.decode(new ByteArrayInputStream(osMessage.getArray()));

        assertEquals(orig.toString(), decoded.toString());
        // System.out.println(Base64.getEncoder().encodeToString(osMessage.getArray()));
    }

    @Test
    public void assessmentRequest() throws IOException {
        final DataRequest ar = new DataRequest(
                new BerInteger(0),
                new BerEnum(1L),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(ar, null, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void assessmentResponseQTM() throws IOException {
        final DataResponse ar = new DataResponse();
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
            final ReverseByteArrayOutputStream osValue = new ReverseByteArrayOutputStream(3, true);
            qv.encode(osValue, true);
            a.value = new BerOctetString(osValue.getArray());

            ar.response.seqOf.add(a);
        }

        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, ar, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void assessmentResponseSL() throws IOException {
        final DataResponse ar = new DataResponse();
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
            final ReverseByteArrayOutputStream osValue = new ReverseByteArrayOutputStream(32, true);
            sl.encode(osValue);

            a.value = new BerOctetString(osValue.getArray());
            ar.response.seqOf.add(a);
        }

        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, ar, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
        // System.out.println(Base64.getEncoder().encodeToString(baos.getArray()));
    }

    @Test
    public void trustRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("MCMCAQEWBmNhbGxlchYGY2FsbGVlYg4CAQEKAQBlBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void trustRequest() throws IOException {
        final DataRequest tr = new DataRequest(
                new BerInteger(0),
                new BerEnum(0L),
                Utils.getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(tr, null, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void trustResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("MGsCAQEWBmNhbGxlchYGY2FsbGVlY1YCAQEGAikBCgEAMEpkIxYHYkB4LmNvbRYHY0B4LmNvbR" +
                "YGbGV0dGVyAgID6AQDAgEFZCMWB2FAeC5jb20WB2JAeC5jb20WBnJlbnRlcgICA+gEAwIBBQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void trustResponse() throws IOException {
        final DataResponse tr = new DataResponse();
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
            final ReverseByteArrayOutputStream osValue = new ReverseByteArrayOutputStream(3, true);
            qv.encode(osValue, true);
            t.value = new BerOctetString(osValue.getArray());

            tr.response.seqOf.add(t);
        }

        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, tr, null, null, null));

        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void formatRequestFromPython() throws IOException {
        final byte[] q = Utils.decode("MBYCAQEWBmNhbGxlchYGY2FsbGVlQAFk");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void formatRequest() throws IOException {
        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, null, new FormatRequest(10L), null, null));
        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(10, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));

        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void formatResponseFromPython() throws IOException {
        final byte[] q = Utils.decode("MHUCAQEWBmNhbGxlchYGY2FsbGVlYWACAWQGAioDFitIZXJlIGJlIGFuIEFTTi4xIHNwZWMg" +
                "Zm9yIGFzc2Vzc21lbnQgdmFsdWVzBgIqAxYmSGVyZSBiZSBhbiBBU04uMSBzcGVjIGZvciB0cnVzdCB2YWx1ZXM=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void formatResponse() throws IOException {
        final FormatResponse fr = new FormatResponse();
        fr.assessmentId = new Format(new int[]{1, 2, 3});
        fr.assessmentDef = new BerIA5String("Assessment format".getBytes());
        fr.trustDef = new BerIA5String("Trust format".getBytes());
        fr.trustId = new Format(new int[]{1, 2, 3});
        fr.rid = new BerInteger(10L);
        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, null, null, fr, null));
        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));
        assertEquals(orig.toString(), decoded.toString());
    }

    @Test
    public void faultFromPython() throws IOException {
        final byte[] q = Utils.decode("MC8CAQEWBmNhbGxlchYGY2FsbGVlZxoCAQoWFXNvbWV0aGluZyB3ZW50IHdyb25nIQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q));
    }

    @Test
    public void fault() throws IOException {
        final Fault f = new Fault(
                new BerInteger(10),
                new BerIA5String("something went wrong!".getBytes()));
        final Message orig = new Message(
                new BerInteger(1L),
                new Entity("caller".getBytes()), new Entity("callee".getBytes()),
                new Message.Payload(null, null, null, null, f));
        final ReverseByteArrayOutputStream baos = new ReverseByteArrayOutputStream(100, true);
        orig.encode(baos);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()));
        assertEquals(orig.toString(), decoded.toString());
    }
}
