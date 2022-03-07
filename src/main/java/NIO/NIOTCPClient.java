package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author Liang_Daojun
 * @date 2022/3/6 9:02 PM
 */
public class NIOTCPClient {

    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));
            socketChannel.write(ByteBuffer.wrap("hello, world!".getBytes()));
            readBytes(socketChannel);
        } catch (IOException e) {

        }
    }

    private static void readBytes(SocketChannel socketChannel) throws IOException {
        int bufferSize = 100;
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        int len = socketChannel.read(byteBuffer);
        while (true) {
            byte[] bytes = new byte[len];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            System.out.println(bytes.length + ":read:" + Arrays.toString(bytes));
            byteBuffer.clear();
            if (len < bufferSize) {
                break;
            }
            len = socketChannel.read(byteBuffer);
        }
    }
}
