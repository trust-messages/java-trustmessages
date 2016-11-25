package com.david;

import java.net.InetSocketAddress;

interface IncomingDataHandler {
    void handle(TrustSocket socket, InetSocketAddress remoteAddress, byte[] data);
}
