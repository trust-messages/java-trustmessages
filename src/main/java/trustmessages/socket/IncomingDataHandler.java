package trustmessages.socket;

import java.net.InetSocketAddress;

public interface IncomingDataHandler {
    void handle(TrustSocket socket, InetSocketAddress sender, byte[] data);
}
