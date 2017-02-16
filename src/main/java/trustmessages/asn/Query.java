/**
 * This class file was automatically generated by jASN1 v1.7.0 (http://www.openmuc.org)
 */

package trustmessages.asn;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerTag;

import java.io.IOException;
import java.io.InputStream;


public class Query {

    public byte[] code = null;
    public Comparison cmp = null;
    public Logical log = null;

    public Query() {
    }

    public Query(byte[] code) {
        this.code = code;
    }

    public Query(Comparison cmp, Logical log) {
        this.cmp = cmp;
        this.log = log;
    }

    public int encode(BerByteArrayOutputStream os) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
            return code.length;
        }

        int codeLength = 0;
        if (log != null) {
            codeLength += log.encode(os, true);
            return codeLength;
        }

        if (cmp != null) {
            codeLength += cmp.encode(os, true);
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

        if (berTag.equals(Comparison.tag)) {
            cmp = new Comparison();
            codeLength += cmp.decode(is, false);
            return codeLength;
        }

        if (berTag.equals(Logical.tag)) {
            log = new Logical();
            codeLength += log.decode(is, false);
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
        if (cmp != null) {
            return "CHOICE{cmp: " + cmp + "}";
        }

        if (log != null) {
            return "CHOICE{log: " + log + "}";
        }

        return "unknown";
    }

}
