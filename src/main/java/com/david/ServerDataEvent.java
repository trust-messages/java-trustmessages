package com.david;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
    final TrustSocket socket;
    final SocketChannel channel;
    final byte[] data;

    public ServerDataEvent(TrustSocket socket, SocketChannel channel, byte[] data) {
        this.socket = socket;
        this.channel = channel;
        this.data = data;
    }
}
