package study.wyy.net.demo.demo01;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/5 9:25
 */
@Slf4j
public class EchoServer {
    private String ip;
    private int port;
    private ServerSocket serverSocket;
    private SimpleThreadPool simpleThreadPool;

    public EchoServer(int port,SimpleThreadPool simpleThreadPool) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.ip = serverSocket.getInetAddress().getHostAddress();
        this.simpleThreadPool =simpleThreadPool;
        log.info("the server has bind address is {}:{}", this.ip, this.port);
    }

    public void start(){
        Socket client;
        while (true){
            try {
                // 主线程负责接收客户端请求，等待客户端连接，返回socket对象
                client = serverSocket.accept();
                // 开启一个线程处理该客户端的连接
                //new Thread(new RequestHandler(client)).start();
                //new RequestHandler(client).handle();
                // 使用线程池
                simpleThreadPool.submit(new RequestHandler(client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
