package study.wyy.net.socket.serversocket.baclog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/4 14:46
 */
public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 设置最大连接数为3
        ServerSocket serverSocket = new ServerSocket(9999, 3);
        System.out.println("服务启动");
        while (true) {
            Socket socket = null;
            try {
                // 获取客户端连接， 就是从连接请求队列中获取一个连接
                socket = serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(5000);
                System.out.println("new connection from" + socket.getInetAddress() + ":" + socket.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
