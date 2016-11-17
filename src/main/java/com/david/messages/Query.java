/**
 * This class file was automatically generated by jASN1 v1.6.0 (http://www.openmuc.org)
 */

package com.david.messages;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerIdentifier;
import org.openmuc.jasn1.ber.BerLength;

import java.io.IOException;
import java.io.InputStream;


public class Query {

    public static final BerIdentifier identifier = new BerIdentifier(BerIdentifier.APPLICATION_CLASS,
            BerIdentifier.CONSTRUCTED, 4);
    protected BerIdentifier id;

    public byte[] code = null;
    public Comparison cmp = null;

    public Logical log = null;

    public Query() {
        this.id = identifier;
    }

    public Query(byte[] code) {
        this.id = identifier;
        this.code = code;
    }

    public Query(Comparison cmp, Logical log) {
        this.id = identifier;
        this.cmp = cmp;
        this.log = log;
    }

    public int encode(BerByteArrayOutputStream os, boolean explicit) throws IOException {
        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
            return code.length;

        }
        int codeLength = 0;
        if (log != null) {
            codeLength += log.encode(os, true);
            codeLength += BerLength.encodeLength(os, codeLength);
            codeLength += id.encode(os);
            return codeLength;

        }

        if (cmp != null) {
            codeLength += cmp.encode(os, true);
            codeLength += BerLength.encodeLength(os, codeLength);
            codeLength += id.encode(os);
            return codeLength;

        }

        throw new IOException("Error encoding BerChoice: No item in choice was selected.");
    }

    public int decode(InputStream is, BerIdentifier berIdentifier) throws IOException {
        int codeLength = 0;
        BerIdentifier passedIdentifier = berIdentifier;

        if (berIdentifier == null) {
            berIdentifier = new BerIdentifier();
            codeLength += berIdentifier.decode(is);
        }

        BerLength length = new BerLength();
        if (berIdentifier.equals(Comparison.identifier)) {
            cmp = new Comparison();
            codeLength += cmp.decode(is, false);
            return codeLength;
        }

        if (berIdentifier.equals(Logical.identifier)) {
            log = new Logical();
            codeLength += log.decode(is, false);
            return codeLength;
        }

        if (passedIdentifier != null) {
            return 0;
        }
        throw new IOException("Error decoding BerChoice: Identifier matched to no item.");
    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
        encode(os, false);
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

