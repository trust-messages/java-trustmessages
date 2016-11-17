/**
 * This class file was automatically generated by jASN1 v1.6.0 (http://www.openmuc.org)
 */

package com.david.messages;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerIdentifier;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.types.BerInteger;

import java.io.IOException;
import java.io.InputStream;


public class TrustRequest {

    public static final BerIdentifier identifier = new BerIdentifier(BerIdentifier.APPLICATION_CLASS, BerIdentifier.CONSTRUCTED, 2);
    protected BerIdentifier id;

    public byte[] code = null;
    public BerInteger rid = null;

    public Query query = null;

    public TrustRequest() {
        id = identifier;
    }

    public TrustRequest(byte[] code) {
        id = identifier;
        this.code = code;
    }

    public TrustRequest(BerInteger rid, Query query) {
        id = identifier;
        this.rid = rid;
        this.query = query;
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
            codeLength += query.encode(os, true);

            codeLength += rid.encode(os, true);

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
        if (berIdentifier.equals(BerInteger.identifier)) {
            rid = new BerInteger();
            subCodeLength += rid.decode(is, false);
            subCodeLength += berIdentifier.decode(is);
        } else {
            throw new IOException("Identifier does not match the mandatory sequence element identifer.");
        }

        query = new Query();
        subCodeLength += query.decode(is, berIdentifier);
        if (subCodeLength == totalLength) {
            return codeLength;
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
        sb.append("rid: ").append(rid);

        sb.append(", ");
        sb.append("query: ").append(query);

        sb.append("}");
        return sb.toString();
    }

}

