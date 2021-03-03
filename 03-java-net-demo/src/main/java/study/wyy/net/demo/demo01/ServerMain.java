package study.wyy.net.demo.demo01;

import java.io.IOException;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/5 9:31
 */
public class ServerMain {

    public static void main(String[] args) throws IOException {
        // 启动服务端
        //EchoServer echoServer = new EchoServer(9999);
        // 构建线程池
        SimpleThreadPool simpleThreadPool = new SimpleThreadPool();
        EchoServer echoServer = new EchoServer(9999,simpleThreadPool);
        echoServer.start();
    }
}
