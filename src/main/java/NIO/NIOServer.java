package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Liang_Daojun
 * @description: NIO 实现TCP通信
 * @date 2022/3/6 7:30 PM
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        final Selector serverSelector = Selector.open();
        final Selector clientSelector = Selector.open();
        // serverSelector轮询是否有新连接
        new Thread(()-> {
             try {
                 ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                 listenerChannel.socket().bind(new InetSocketAddress(8000));
                 listenerChannel.configureBlocking(false);
                 listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
                 while (true) {
                     // 检测是否有新连接，1代表阻塞的时间为1ms
                     if (serverSelector.select(1) > 0) {
                         Set<SelectionKey> set = serverSelector.selectedKeys();
                         Iterator<SelectionKey> keyIterator = set.iterator();
                         while (keyIterator.hasNext()) {
                             SelectionKey key = keyIterator.next();
                             if (key.isAcceptable()) {
                                 try {
                                     SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                     clientChannel.configureBlocking(false);
                                     clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                 } finally {
                                     keyIterator.remove();
                                 }
                             }
                         }
                     }
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
        }).start();
        // clientSelector轮询连接是否有数据可读
        new Thread(() -> {
            try {
                while (true) {
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isReadable()) {
                                try {
                                  SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    byteBuffer.putChar('1');
                                    clientChannel.read(byteBuffer);
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {

            }
        }).start();
    }

}
