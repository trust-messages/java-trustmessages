package com.david;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
    final TrustSocket server;
    final SocketChannel socket;
    final byte[] data;

    public ServerDataEvent(TrustSocket socket, SocketChannel channel, byte[] data) {
        this.server = socket;
        this.socket = channel;
        this.data = data;
    }
}
