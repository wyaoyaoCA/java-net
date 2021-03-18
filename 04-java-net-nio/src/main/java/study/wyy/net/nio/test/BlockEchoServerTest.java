package study.wyy.net.nio.test;

import study.wyy.net.nio.server.BlockEchoServer;

import java.io.IOException;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:54
 */
public class BlockEchoServerTest {
    public static void main(String[] args) throws IOException {
        BlockEchoServer server = new BlockEchoServer(10010);
        // 等待客户端连接
        server.service();
    }
}
