package com.david;

import com.david.format.SL;
import com.david.messages.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.BerReal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.david.Utils.decode;
import static com.david.Utils.getQuery;
import static org.junit.Assert.assertArrayEquals;

public class MessagesTest extends TestCase {
    public MessagesTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MessagesTest.class);
    }

    public void testQuantitativeAssessmentFromPython() throws IOException {
        final byte[] q = decode("aSMGAikBEwdhQHguY29tEwdiQHguY29tEwVidXllcgIBZAIBAQ==");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    public void testQuantitativAssessment() throws IOException {
        final Assessment orig = new Assessment();
        orig.tms = new SystemIdentity(new int[]{1, 1, 1});
        orig.source = new Entity("me@me.com".getBytes());
        orig.target = new Entity("you@you.com".getBytes());
        orig.service = new Service("seller".getBytes());
        orig.date = new BinaryTime(10);
        final SL sl = new SL(new BerReal(0.1), new BerReal(0.2), new BerReal(0.7));
        sl.encodeAndSave(34);
        orig.value = new BerAny(sl.code);


        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos, true);
        orig.encodeAndSave(100);

        final Assessment decoded = new Assessment();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), true);

        decoded.encodeAndSave(100);

        assertArrayEquals(orig.code, decoded.code);
    }

    public void testQualitativeAssessmentFromPython() throws IOException {
        final byte[] q = decode("aSsGAlICEwdjQHguY29tEwdhQHguY29tEwZzZWxsZXICAWQTCGRpc3RydXN0");
        final Assessment m = new Assessment();
        m.decode(new ByteArrayInputStream(q), true);
    }

    public void testAssessmentRequestFromPython() throws IOException {
        final byte[] q = decode("ZgsCAQFkBgoBBUIBUA==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testAssessmentRequest() throws IOException {
        final AssessmentRequest ar = new AssessmentRequest(
                new BerInteger(0),
                getQuery("target = david@fri.si AND (service = seller OR service = letter)"));
        final Message orig = new Message(ar, null, null, null, null, null, null);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(100, true);
        orig.encode(baos, true);
        orig.encodeAndSave(100);

        final Message decoded = new Message();
        decoded.decode(new ByteArrayInputStream(baos.getArray()), null);

        decoded.encodeAndSave(100);

        assertArrayEquals(orig.code, decoded.code);
    }

    public void testAssessmentResponseFromPython() throws IOException {
        final byte[] q = decode("Z1MCAQEwTmklBgIpARMHYkB4LmNvbRMHY0B4LmNvbRMGbGV0dGVyAgID6AIBBWklBgIpARMHYUB4LmNvbRMHYkB4LmNvbRMGcmVudGVyAgID6AIBBQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testAssessmentResponse() throws IOException {
        final AssessmentResponse ar = new AssessmentResponse();
        ar.rid = new BerInteger(1);
        ar.response = new AssessmentResponse.Response();
        ar.response.seqOf = new ArrayList<>();
    }

    public void testTrustRequestFromPython() throws IOException {
        final byte[] q = decode("YgwCAhOIZAYKAQVCAVA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testTrustResponseFromPython() throws IOException {
        final byte[] q = decode("Y0wCAwERcDBFaCEGAikBEwdjQHguY29tEwVidXllcgICB9ATB25ldXRyYWxoIAYCKQETB2FAeC5jb20TBnNlbGxlcgICB9ATBXRydXN0");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testFormatRequestFromPython() throws IOException {
        final byte[] q = decode("QAA=");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testFormatResponseFromPython() throws IOException {
        final byte[] q = decode("YVkGAioDEytIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIGFzc2Vzc21lbnQgdmFsdWVzEyZIZXJlIGJlIGFuIEFTTi4xIHNwZWMgZm9yIHRydXN0IHZhbHVlcw==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }

    public void testErrorFromPython() throws IOException {
        final byte[] q = decode("ahoKAQATFXNvbWV0aGluZyB3ZW50IHdyb25nIQ==");
        final Message m = new Message();
        m.decode(new ByteArrayInputStream(q), null);
    }
}
