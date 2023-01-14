package top.opensrc.core.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8083));
        sc.write(Charset.defaultCharset().encode("12345678\n901234567899aaadasfbasfdsaf\n"));
        System.in.read();
    }
}
