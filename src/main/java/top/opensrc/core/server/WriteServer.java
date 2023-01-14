package top.opensrc.core.server;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * nio网络编程写数据服务
 * @author surgar.tian
 *
 *
 */
public class WriteServer {

    @SneakyThrows(Exception.class)
    public static void main(String[] args) {

        /* 1. 开启socket服务通道 */
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        /* 2. 关联selector */
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        /* 3. 注册端口  */
        ssc.bind(new InetSocketAddress(8000));


        /* 4. 服务器正式开始运行, 轮询读取事件的内容 */
        while (true){

            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iter = selectionKeys.iterator();

            while (iter.hasNext()){

                SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    while (buffer.hasRemaining()) {
                        int write = sc.write(buffer);
                        System.out.println(write);
                    }

                }

            }
        }



    }


}
