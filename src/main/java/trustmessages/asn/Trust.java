/**
 * This class file was automatically generated by jASN1 v1.7.0 (http://www.openmuc.org)
 */

package trustmessages.asn;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.BerTag;
import org.openmuc.jasn1.ber.types.BerAny;

import java.io.IOException;
import java.io.InputStream;


public class Trust {

    public static final BerTag tag = new BerTag(BerTag.APPLICATION_CLASS, BerTag.CONSTRUCTED, 8);

    public byte[] code = null;
    public SystemIdentity tms = null;
    public Entity target = null;
    public Service service = null;
    public BinaryTime date = null;
    public BerAny value = null;

    public Trust() {
    }

    public Trust(byte[] code) {
        this.code = code;
    }

    public Trust(SystemIdentity tms, Entity target, Service service, BinaryTime date, BerAny value) {
        this.tms = tms;
        this.target = target;
        this.service = service;
        this.date = date;
        this.value = value;
    }

    public int encode(BerByteArrayOutputStream os) throws IOException {
        return encode(os, true);
    }

    public int encode(BerByteArrayOutputStream os, boolean withTag) throws IOException {

        if (code != null) {
            for (int i = code.length - 1; i >= 0; i--) {
                os.write(code[i]);
            }
            if (withTag) {
                return tag.encode(os) + code.length;
            }
            return code.length;
        }

        int codeLength = 0;
        codeLength += value.encode(os);

        codeLength += date.encode(os, true);

        codeLength += service.encode(os, true);

        codeLength += target.encode(os, true);

        codeLength += tms.encode(os, true);

        codeLength += BerLength.encodeLength(os, codeLength);

        if (withTag) {
            codeLength += tag.encode(os);
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
        if (berTag.equals(SystemIdentity.tag)) {
            tms = new SystemIdentity();
            subCodeLength += tms.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(Entity.tag)) {
            target = new Entity();
            subCodeLength += target.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(Service.tag)) {
            service = new Service();
            subCodeLength += service.decode(is, false);
            subCodeLength += berTag.decode(is);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        if (berTag.equals(BinaryTime.tag)) {
            date = new BinaryTime();
            subCodeLength += date.decode(is, false);
        } else {
            throw new IOException("Tag does not match the mandatory sequence element tag.");
        }

        value = new BerAny();
        subCodeLength += value.decode(is, totalLength - subCodeLength);
        return codeLength;

    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
        BerByteArrayOutputStream os = new BerByteArrayOutputStream(encodingSizeGuess);
        encode(os, false);
        code = os.getArray();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SEQUENCE{");
        sb.append("tms: ").append(tms);

        sb.append(", ");
        sb.append("target: ").append(target);

        sb.append(", ");
        sb.append("service: ").append(service);

        sb.append(", ");
        sb.append("date: ").append(date);

        sb.append(", ");
        sb.append("value: ").append(value);

        sb.append("}");
        return sb.toString();
    }

}

