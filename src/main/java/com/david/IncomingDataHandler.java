package com.david;

import java.net.InetSocketAddress;

public interface IncomingDataHandler {
    void handle(TrustSocket socket, InetSocketAddress sender, byte[] data);
}
