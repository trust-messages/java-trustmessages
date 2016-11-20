package com.david;

import java.nio.channels.SocketChannel;

public class TrustSocketRequest {
    final TrustSocket trustSocket;
    final SocketChannel socketChannel;
    final byte[] data;

    public TrustSocketRequest(TrustSocket trustSocket, SocketChannel socketChannel, byte[] data) {
        this.trustSocket = trustSocket;
        this.socketChannel = socketChannel;
        this.data = data;
    }
}
