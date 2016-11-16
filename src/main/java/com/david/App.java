package com.david;

import com.david.format.QTM;
import com.david.messages.*;
import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.types.BerAny;

import java.io.IOException;
import java.util.Base64;

public class App {
    public static void main(String[] args) throws IOException {
        final Trust trust = new Trust();
        trust.date = new BinaryTime(System.currentTimeMillis());
        trust.service = new Service("seller");
        trust.target = new Entity("david@fri.si");
        trust.tms = new SystemIdentity(1, 2, 3);

        final QTM v = new QTM();
        v.value = 0;

        v.encodeAndSave();

        trust.value = new BerAny(v.code);

        final BerByteArrayOutputStream baos = new BerByteArrayOutputStream(32, true);

        trust.encode(baos, true);

        final String base64 = Base64.getEncoder().encodeToString(baos.getArray());

        System.out.println(base64);
    }


}
