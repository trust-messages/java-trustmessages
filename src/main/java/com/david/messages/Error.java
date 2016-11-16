/**
 * This class file was automatically generated by jASN1 v1.6.0 (http://www.openmuc.org)
 */

package com.david.messages;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerIdentifier;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.types.BerEnum;
import org.openmuc.jasn1.ber.types.string.BerPrintableString;

import java.io.IOException;
import java.io.InputStream;


public class Error {

    public static final BerIdentifier identifier = new BerIdentifier(BerIdentifier.APPLICATION_CLASS, BerIdentifier.CONSTRUCTED, 10);
    public byte[] code = null;
    public BerEnum value = null;
    public BerPrintableString message = null;
    protected BerIdentifier id;

    public Error() {
        id = identifier;
    }

    public Error(byte[] code) {
        id = identifier;
        this.code = code;
    }

    public Error(BerEnum value, BerPrintableString message) {
        id = identifier;
        this.value = value;
        this.message = message;
    }

    public int encode(BerByteArrayOutputStream os, boolean explicit) throws IOException {

        int codeLength;

        if (code != null) {
            codeLength = code.length;
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
        } else {
            codeLength = 0;
            codeLength += message.encode(os, true);

            codeLength += value.encode(os, true);

            codeLength += BerLength.encodeLength(os, codeLength);
        }

        if (explicit) {
            codeLength += id.encode(os);
        }

        return codeLength;

    }

    public int decode(InputStream is, boolean explicit) throws IOException {
        int codeLength = 0;
        int subCodeLength = 0;
        BerIdentifier berIdentifier = new BerIdentifier();

        if (explicit) {
            codeLength += id.decodeAndCheck(is);
        }

        BerLength length = new BerLength();
        codeLength += length.decode(is);

        int totalLength = length.val;
        codeLength += totalLength;

        subCodeLength += berIdentifier.decode(is);
        if (berIdentifier.equals(BerEnum.identifier)) {
            value = new BerEnum();
            subCodeLength += value.decode(is, false);
            subCodeLength += berIdentifier.decode(is);
        } else {
            throw new IOException("Identifier does not match the mandatory sequence element identifer.");
        }

        if (berIdentifier.equals(BerPrintableString.identifier)) {
            message = new BerPrintableString();
            subCodeLength += message.decode(is, false);
            if (subCodeLength == totalLength) {
                return codeLength;
            }
        }
        throw new IOException("Unexpected end of sequence, length tag: " + totalLength + ", actual sequence length: " + subCodeLength);


    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
        encode(os, false);
        code = os.getArray();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SEQUENCE{");
        sb.append("value: ").append(value);

        sb.append(", ");
        sb.append("message: ").append(message);

        sb.append("}");
        return sb.toString();
    }

}

