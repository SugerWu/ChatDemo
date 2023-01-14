package top.opensrc.core.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static top.opensrc.core.utils.ByteBufferUtil.debugAll;
import static top.opensrc.core.utils.ByteBufferUtil.debugRead;

/**
 * @author surgar.tian
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {

        log.debug("1");
        /* 1. 创建selector 可以管理多个channel */
        Selector selector = Selector.open();

        final ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        /* 2. 建立selector和channel 的联系 */
        SelectionKey sscKey = ssc.register(selector, 0, null);
        /* 2.1 只关注ACCEPT事件 */
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8083));

        while (true) {
            /* 3. selector 的 select方法获取当前的事件 */
            selector.select();
            /* 4. 处理事件 包含所有事件 */
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                log.debug("{}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}",sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer attachment = (ByteBuffer) key.attachment();
                        int read = channel.read(attachment);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(attachment);
                            if(attachment.position()==attachment.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(attachment.capacity() << 1);
                                attachment.flip();
                                newBuffer.put(attachment);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }

                }

            }


        }

    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i)=='\n'){
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
