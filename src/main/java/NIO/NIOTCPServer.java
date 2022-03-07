package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Liang_Daojun
 * @date 2022/3/6 8:42 PM
 */
public class NIOTCPServer {
    public static void main(String[] args) {
        int threadNumber = 5;
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);
            System.out.println("Server is waiting for connection...");
            ExecutorService newFixedTheadPool = Executors.newFixedThreadPool(threadNumber);
            final ServerSocketChannel channel = serverSocketChannel;
            AtomicInteger count = new AtomicInteger(0);
            for (int i = 0; i < threadNumber; i++) {
                NioChannelHandler handler = new NioChannelHandler();
                newFixedTheadPool.submit(new NioServerChannelAccepter(channel, count, handler));
            }

            while (true) {
                int i = count.get();
                System.out.println("Client number is " + i);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {

                }
            }
        } catch (Exception e) {
            if (serverSocketChannel != null) {
                System.out.println("server shutdown now...");
                try {
                    serverSocketChannel.close();
                } catch (Exception e2) {

                }
            }
        }
    }

    private static class NioServerChannelAccepter implements Runnable {

        private final ServerSocketChannel channel;
        private final AtomicInteger clientCount;
        private final NioChannelHandler handler;

        public NioServerChannelAccepter(ServerSocketChannel channel, AtomicInteger clientCount, NioChannelHandler handler) {
            this.channel = channel;
            this.clientCount = clientCount;
            this.handler = handler;
        }

        @Override
        public void run() {
            SocketChannel accept = null;
            while (true) {
                try {
                    if (accept == null) {
                        try {
                            System.out.println(Thread.currentThread().getName() + "waits to connect");
                            Thread.sleep(2000L);
                        } catch (Exception e) {

                        }
                        accept = channel.accept();
                        if (accept != null) {
                            clientCount.incrementAndGet();
                        }
                    } else {
                        handler.handler(accept);
                    }
                } catch (IOException e) {
                    if (accept != null) {
                        clientCount.decrementAndGet();
                        try {
                            accept.close();
                        } catch (IOException ex) {

                        }
                        accept = null;
                    }
                }
            }
        }
    }

    private static class NioChannelHandler {
        public void handler(SocketChannel accept) throws IOException {
            SocketAddress remoteAddress = accept.getRemoteAddress();
            System.out.println("server received a connection: " + remoteAddress);
            readBytes(accept);
            byte[] bytes = "hello world!".getBytes();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            int write = accept.write(byteBuffer);
            System.out.println("write bytes to client: " + write);
            byteBuffer.clear();
        }

        private static void readBytes(SocketChannel accept) throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            int len = accept.read(byteBuffer);
            while (true) {
                byte[] bytes = new byte[len];
                byteBuffer.flip();
                byteBuffer.get(bytes);
                System.out.println(bytes.length + ":read:" + Arrays.toString(bytes));
                byteBuffer.clear();
                if (len < 100) {
                    break;
                }
                len = accept.read(byteBuffer);
            }
        }

    }
}
