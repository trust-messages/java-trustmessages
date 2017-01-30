/**
 * This class file was automatically generated by jASN1 v1.7.0 (http://www.openmuc.org)
 */

package trustmessages.asn;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerTag;

import java.io.IOException;
import java.io.InputStream;


public class Message {

    public byte[] code = null;
    public AssessmentRequest assessmentRequest = null;
    public AssessmentResponse assessmentResponse = null;
    public TrustRequest trustRequest = null;
    public TrustResponse trustResponse = null;
    public FormatRequest formatRequest = null;
    public FormatResponse formatResponse = null;
    public Fault fault = null;

    public Message() {
    }

    public Message(byte[] code) {
        this.code = code;
    }

    public Message(AssessmentRequest assessmentRequest, AssessmentResponse assessmentResponse, TrustRequest trustRequest, TrustResponse trustResponse, FormatRequest formatRequest, FormatResponse formatResponse, Fault fault) {
        this.assessmentRequest = assessmentRequest;
        this.assessmentResponse = assessmentResponse;
        this.trustRequest = trustRequest;
        this.trustResponse = trustResponse;
        this.formatRequest = formatRequest;
        this.formatResponse = formatResponse;
        this.fault = fault;
    }

    public int encode(BerByteArrayOutputStream os) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
            return code.length;
        }

        int codeLength = 0;
        if (fault != null) {
            codeLength += fault.encode(os, true);
            return codeLength;
        }

        if (formatResponse != null) {
            codeLength += formatResponse.encode(os, true);
            return codeLength;
        }

        if (formatRequest != null) {
            codeLength += formatRequest.encode(os, true);
            return codeLength;
        }

        if (trustResponse != null) {
            codeLength += trustResponse.encode(os, true);
            return codeLength;
        }

        if (trustRequest != null) {
            codeLength += trustRequest.encode(os, true);
            return codeLength;
        }

        if (assessmentResponse != null) {
            codeLength += assessmentResponse.encode(os, true);
            return codeLength;
        }

        if (assessmentRequest != null) {
            codeLength += assessmentRequest.encode(os, true);
            return codeLength;
        }

        throw new IOException("Error encoding BerChoice: No item in choice was selected.");
    }

    public int decode(InputStream is) throws IOException {
        return decode(is, null);
    }

    public int decode(InputStream is, BerTag berTag) throws IOException {

        int codeLength = 0;
        BerTag passedTag = berTag;

        if (berTag == null) {
            berTag = new BerTag();
            codeLength += berTag.decode(is);
        }

        if (berTag.equals(AssessmentRequest.tag)) {
            assessmentRequest = new AssessmentRequest();
            codeLength += assessmentRequest.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(AssessmentResponse.tag)) {
            assessmentResponse = new AssessmentResponse();
            codeLength += assessmentResponse.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(TrustRequest.tag)) {
            trustRequest = new TrustRequest();
            codeLength += trustRequest.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(TrustResponse.tag)) {
            trustResponse = new TrustResponse();
            codeLength += trustResponse.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(FormatRequest.tag)) {
            formatRequest = new FormatRequest();
            codeLength += formatRequest.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(FormatResponse.tag)) {
            formatResponse = new FormatResponse();
            codeLength += formatResponse.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(Fault.tag)) {
            fault = new Fault();
            codeLength += fault.decode(is, false);
            return codeLength;
        }

        if (passedTag != null) {
            return 0;
        }

        throw new IOException("Error decoding BerChoice: Tag matched to no item.");
    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
        encode(os);
        code = os.getArray();
    }

    public String toString() {
        if (assessmentRequest != null) {
            return "CHOICE{assessmentRequest: " + assessmentRequest + "}";
        }

        if (assessmentResponse != null) {
            return "CHOICE{assessmentResponse: " + assessmentResponse + "}";
        }

        if (trustRequest != null) {
            return "CHOICE{trustRequest: " + trustRequest + "}";
        }

        if (trustResponse != null) {
            return "CHOICE{trustResponse: " + trustResponse + "}";
        }

        if (formatRequest != null) {
            return "CHOICE{formatRequest: " + formatRequest + "}";
        }

        if (formatResponse != null) {
            return "CHOICE{formatResponse: " + formatResponse + "}";
        }

        if (fault != null) {
            return "CHOICE{fault: " + fault + "}";
        }

        return "unknown";
    }

}

