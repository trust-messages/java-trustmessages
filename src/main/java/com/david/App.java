package com.david;

import org.openmuc.jasn1.ber.BerIdentifier;

public class App {
    public static void main(String[] args) {
        final Trust trust = new Trust();
        trust.date = new BinaryTime(System.currentTimeMillis());
        trust.id = new BerIdentifier();
        trust.service = new Service("seller".getBytes());
        trust.target = new Entity("david@fri.si".getBytes());

        System.out.println(trust);
    }
}
