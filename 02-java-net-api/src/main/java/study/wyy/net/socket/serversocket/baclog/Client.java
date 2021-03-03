package study.wyy.net.socket.serversocket.baclog;

import java.io.IOException;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/4 14:46
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        final int port = 9999;
        final String host = "localhost";
        final int len = 100;
        // 尝试建立100次连接
        Socket[] sockets = new Socket[len];
        for (int i = 0; i < len; i++) {
            sockets[i] = new Socket(host, port);
            System.out.println("第" + (i + 1) + "次数" + "连接成功");
        }

        // 关闭连接
        for (int i = 0; i < len; i++) {
            sockets[i].close();
        }
    }
}
