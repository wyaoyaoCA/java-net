package study.wyy.net.nio.test;


import study.wyy.net.nio.server.MixEchoServer;
import study.wyy.net.nio.server.NoBlockEchoServer;

import java.io.IOException;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:54
 */
public class MixEchoServerTest {
    public static void main(String[] args) throws IOException {
        MixEchoServer server = new MixEchoServer(10010);
        // 等待客户端连接
        server.accept();
        server.service();
    }
}
