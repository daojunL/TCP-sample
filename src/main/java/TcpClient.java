import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @author Liang_Daojun
 * @date 2022/1/22 10:21 PM
 */
public class TcpClient {

    public static void main(String[] args) throws IOException {
        TcpClient tcpClient = new TcpClient();
        SimpleDateFormat format = new SimpleDateFormat("hh-MM-ss");
        Scanner scanner = new Scanner(System.in);
        // 需要一直发起对server的请求
        while (true) {
            String msg = scanner.nextLine();
            if ("#".equals(msg)) break;
            System.out.println("send time: " + format.format(new Date()));
            System.out.println(tcpClient.sendAndReceive(TcpServer.SERVICE_IP, TcpServer.SERVICE_PORT, msg));
            System.out.println("send time2: " + format.format(new Date()));
        }
    }

    public String sendAndReceive(String ip, int port, String msg) throws IOException {
        msg = msg + TcpServer.END_CHAR;
        StringBuilder sb = new StringBuilder();
        Socket client = new Socket(ip, port);
        // 输出流，作为client一方，要send数据
        OutputStream out = client.getOutputStream();
        out.write(msg.getBytes());
        InputStream in = client.getInputStream();
        for (int c = in.read(); c!=TcpServer.END_CHAR; c = in.read()) {
            if (c == -1) break;
            sb.append((char) c);
        }
        return sb.toString();
    }
}
