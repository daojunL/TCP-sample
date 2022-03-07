package TCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Liang_Daojun
 * @date 2022/1/22 9:57 PM
 */
// BIO (Blocking-IO, 一个连接会有一个线程来处理 )
public class TcpServer {
    public static final char END_CHAR = '#';
    public static final String SERVICE_IP = "127.0.0.1";
    public static final int SERVICE_PORT = 10101;

    public static void main(String[] args) throws IOException {
        TcpServer tcpServer = new TcpServer();
        tcpServer.startService(SERVICE_IP, 10101);
    }

    public void startService(String serverIP, int serverPort) throws IOException {
        InetAddress serverAddress = InetAddress.getByName(serverIP);
        ServerSocket service = new ServerSocket(serverPort, 10, serverAddress);

        // 需要一直响应Client传来的请求
        while (true) {
            StringBuilder sb = new StringBuilder();
            // Listens for a connection to be made to this socket and accepts
            //     * it. The method blocks until a connection is made.
            // 阻塞操作，一旦连接成功会返回一个新的Socket.
            Socket connect = service.accept();
            // 输入流
            InputStream in = connect.getInputStream();
            for (int c = in.read(); c!= END_CHAR; c = in.read()) {
                if (c == -1) break;
                sb.append((char) c);
            }

            String response = "Hello, server has received this msg: " + sb.toString() + END_CHAR;
            // 输出流
            OutputStream out = connect.getOutputStream();
            out.write(response.getBytes());
        }
    }
}
