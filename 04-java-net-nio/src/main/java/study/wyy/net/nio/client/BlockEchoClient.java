package study.wyy.net.nio.client;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:59
 */
@Slf4j
public class BlockEchoClient {

    private final SocketChannel socketChannel;
    private final String serverHost;
    private final int serverPort;

    public BlockEchoClient(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.socketChannel = SocketChannel.open();
        // 连接服务器
        SocketAddress remote = new InetSocketAddress(serverHost, serverPort);
        socketChannel.connect(remote);
        log.info("connect echo server success");
    }

    public void send(String message) {
        try {
            BufferedReader reader = getReader(socketChannel.socket());
            PrintWriter writer = getWriter(socketChannel.socket());
            // 发送数据
            writer.println(message);
            log.info("send request success; content is {}", message);
            // 读取服务端的响应
            String s1 = reader.readLine();
            log.info("get response success; response is {}", s1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        if(socketChannel != null){
            socketChannel.close();
        }
    }

    public BufferedReader getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }
}
