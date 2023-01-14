package top.opensrc.core.client;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class WriteClient {

    @SneakyThrows(Exception.class)
    public static void main(String[] args) {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost",8000));

        int count=0;
        while (true){
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            count+=sc.read(buffer);
            System.out.println(count);
            buffer.clear();

        }
    }
}
