/**
 * This class file was automatically generated by jASN1 v1.10.0 (http://www.openmuc.org)
 */

package trustmessages.asn;

import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.BerTag;
import org.openmuc.jasn1.ber.ReverseByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.BerType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;


public class Expression implements BerType, Serializable {

    public static final BerTag tag = new BerTag(BerTag.APPLICATION_CLASS, BerTag.CONSTRUCTED, 6);
    private static final long serialVersionUID = 1L;
    public byte[] code = null;
    public BerEnum operator = null;
    public Query left = null;
    public Query right = null;

    public Expression() {
    }

    public Expression(byte[] code) {
        this.code = code;
    }

    public Expression(BerEnum operator, Query left, Query right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public int encode(OutputStream reverseOS) throws IOException {
        return encode(reverseOS, true);
    }

    public int encode(OutputStream reverseOS, boolean withTag) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                reverseOS.write(code[i]);
            }
            if (withTag) {
                return tag.encode(reverseOS) + code.length;
            }
            return code.length;
        }

        int codeLength = 0;
        codeLength += right.encode(reverseOS);

        codeLength += left.encode(reverseOS);

        codeLength += operator.encode(reverseOS, true);

        codeLength += BerLength.encodeLength(reverseOS, codeLength);

        if (withTag) {
            codeLength += tag.encode(reverseOS);
        }

        return codeLength;

    }

    public int decode(InputStream is) throws IOException {
        return decode(is, true);
    }

    public int decode(InputStream is, boolean withTag) throws IOException {
        int codeLength = 0;
        int subCodeLength = 0;
        BerTag berTag = new BerTag();

        if (withTag) {
            codeLength += tag.decodeAndCheck(is);
        }

        BerLength length = new BerLength();
        codeLength += length.decode(is);

        int totalLength = length.val;
        codeLength += totalLength;

        subCodeLength += berTag.decode(is);
        if (berTag.equals(BerEnum.tag)) {
            operator = new BerEnum();
            subCodeLength += operator.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        left = new Query();
        subCodeLength += left.decode(is, berTag);
        subCodeLength += berTag.decode(is);

        right = new Query();
        subCodeLength += right.decode(is, berTag);
        if (subCodeLength == totalLength) {
            return codeLength;
        }
        throw new IOException("Unexpected end of sequence, length tag: " + totalLength + ", actual sequence length: " + subCodeLength);


    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        ReverseByteArrayOutputStream reverseOS = new ReverseByteArrayOutputStream(encodingSizeGuess);
        encode(reverseOS, false);
        code = reverseOS.getArray();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendAsString(sb, 0);
        return sb.toString();
    }

    public void appendAsString(StringBuilder sb, int indentLevel) {

        sb.append("{");
        sb.append("\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (operator != null) {
            sb.append("operator: ").append(operator);
        } else {
            sb.append("operator: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (left != null) {
            sb.append("left: ");
            left.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("left: <empty-required-field>");
        }

        sb.append(",\n");
        for (int i = 0; i < indentLevel + 1; i++) {
            sb.append("\t");
        }
        if (right != null) {
            sb.append("right: ");
            right.appendAsString(sb, indentLevel + 1);
        } else {
            sb.append("right: <empty-required-field>");
        }

        sb.append("\n");
        for (int i = 0; i < indentLevel; i++) {
            sb.append("\t");
        }
        sb.append("}");
    }

}

