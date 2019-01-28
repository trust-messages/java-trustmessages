# Java Trust Messages

An example Java application that uses `trust messages` ASN.1 schema. 

## Overview

The application is a simple command-line tool with netcat-like behavior: once you run the program, you are given an input prompt where you can connect to other such programs and query them for trust and reputation information. If another system connects to your program and sends a request, you are shown the incoming query on the standard output. The requests are replied automatically.

## Building

To build this program you require [Maven.](https://maven.apache.org) For instance, to build a single runnable jar, run the following command:
```
mvn clean compile assembly:single
```

## Running

Once you have created the runnable JAR, run it with:
```
java -jar target/java-trustmessages-1.0-SNAPSHOT-jar-with-dependencies.jar <port> <qtm|sl> <trs-name> 
```
The arguments have to following semantics:

* `port` represents the network port on which this program listens for incoming requests,
* `qtm` or `sl` represent two examples of trust and reputation systems (a qualitative trust model or subjective logic),
* `trs-name` the identity of the system you want to connect.

Alternatively, you can run the program without creating a runnable JAR with the following command.
```
mvn exec:java -Dexec.mainClass="trustmessages.Node" -Dexec.args="<port> <qtm|sl> <trs-name>"
```

The arguments `<port> <qtm|sl> <trs-name>` have the same meaning as above. An example run:

```
$ mvn exec:java -Dexec.mainClass="trustmessages.Node" -Dexec.args="5000 qtm system1"
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building Java Trust Messages 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ java-trustmessages ---
11:46:06.204 [trustmessages.Node.main()] INFO  trustmessages.socket.TrustSocket - [ServerSocket[addr=/0:0:0:0:0:0:0:0,localport=5000]] Binding 
11:46:06.210 [pool-1-thread-1] INFO  trustmessages.tms.TrustService - Running Trust Service with QTMDb
```

## Usage

Once the program is run, you can issue commands.

Every command has the form of `IP PORT COMMAND` where you specify the `IP` address and the `PORT` number of the system to which you want to send the `COMMAND`.

### Connecting to other systems

To connect to another instance of such program that runs on `localhost` on port `6000`, issue the following.

```
127.0.0.1 6000 connect
11:47:03.913 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Connected
```

To connect to multiple systems, issue multiple connect statements.

### Requesting `Rating` definitions

To obtain the definitions of the `Rating`’s `value` component, issue the `freq` command.

```
127.0.0.1 6000 freq
11:58:10.749 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Sent (8B)
11:58:10.754 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Read (280B)
11:58:10.757 [pool-1-thread-1] INFO  trustmessages.tms.TrustService - [format-response] SEQUENCE{rid: 100, assessmentId: 1.1.1, assessmentDef: ValueFormat DEFINITIONS ::= BEGIN ValueFormat ::= ENUMERATED { very-bad (0), bad (1), neutral (2), good (3), very-good (4)} END, trustId: 1.1.1, trustDef: ValueFormat DEFINITIONS ::= BEGIN ValueFormat ::= ENUMERATED { very-bad (0), bad (1), neutral (2), good (3), very-good (4)} END}
```

### Requesting trust and assessments

To obtain assessments or trust values from a remote system, issue the `areq` or `treq` command (assessment-request or trust-request) and then specify the `query`.

When specifying the query, keep keep in mind that both example trust and reputation systems contain: 

* four different entities (`alice`, `bob`, `charlie`, `david`, `eve`), 
* four different services (`seller`, `buyer`, `renter`, `letter`),
* all `date` values are between `0` and `79`.

Also, remember to capitalize logical operators `AND` and `OR`.

```
127.0.0.1 6000 areq source = alice AND target  = bob AND date > 1
12:04:05.998 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Sent (56B)
12:04:06.011 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Read (87B)
12:04:06.012 [pool-1-thread-1] INFO  trustmessages.tms.TrustService - [data-response] (87B): 1 / (system2) [SEQUENCE{source: alice, target: bob, service: letter, date: 2, value: 0A0102}, SEQUENCE{source: alice, target: bob, service: renter, date: 3, value: 0A0103}]
```

### Responding to incoming requests

Incoming requests are responded automatically. Here is an example output that is shown when a request is received and immediately responded. (No user input is required; the system only prints out debugging information.)

An example output for `FormatRequest`.

```
12:08:54.436 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Read (18B)
12:08:54.437 [pool-1-thread-1] INFO  trustmessages.tms.TrustService - [format-request] (18B)
12:08:54.439 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Sent (285B)
```

An example output for `TrustRequest`.

```
12:10:33.909 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Read (54B)
12:10:33.910 [pool-1-thread-1] INFO  trustmessages.tms.TrustService - [data-request] (54B): 0 / CHOICE{exp: SEQUENCE{operator: 0, left: CHOICE{exp: SEQUENCE{operator: 0, left: CHOICE{con: SEQUENCE{operator: 0, value: CHOICE{source: alice}}}, right: CHOICE{con: SEQUENCE{operator: 0, value: CHOICE{target: bob}}}}}, right: CHOICE{con: SEQUENCE{operator: 4, value: CHOICE{date: 1}}}}}
12:10:33.936 [pool-1-thread-2] INFO  trustmessages.socket.TrustSocket - [/127.0.0.1:6000] Sent (142B)
```

### Benchmarking 

To run the benchmarking tests, run the following:

* Encoding test `mvn exec:java -Dexec.mainClass="trustmessages.Measurement" -Dexec.args="encode"` 
* Decoding test `mvn exec:java -Dexec.mainClass="trustmessages.Measurement" -Dexec.args="decode"`
