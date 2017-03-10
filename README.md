# Java Trust Messages

## Intro

This is an example Java application built using `trust messages`.

## Usage

To generate a single runnable jar, run the following command:
```
mvn clean compile assembly:single
```

Then run the jar with:
```
java -jar target/jasn1-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Generate ASN.1 messages

The messages can be generated with [OpenMUC jasn1-compiler.](https://www.openmuc.org/asn1/download)
Once installed, run it against the messages file:

* Core messages: `./jasn1-compiler -l -o "generated" -p "trustmessages.asn" -f ~/Development/python/py-trustmessages/messages.asn`
* Sample trust formats: `./jasn1-compiler -p "trustmessages.asn" -f ~/Development/python/py-trustmessages/formats.asn`