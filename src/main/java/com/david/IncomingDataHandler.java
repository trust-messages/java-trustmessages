package com.david;

import java.net.InetSocketAddress;

interface IncomingDataHandler {
    void handle(TrustSocket socket, InetSocketAddress sender, byte[] data);
}
