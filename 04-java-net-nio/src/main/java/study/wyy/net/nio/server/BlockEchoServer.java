package study.wyy.net.nio.server;

import lombok.extern.slf4j.Slf4j;
import study.wyy.net.nio.thread.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:12
 * 阻塞式服务端示例
 */
@Slf4j
public class BlockEchoServer {

    /**
     * 服务端口号
     */
    private final int port;
    private final ServerSocketChannel serverSocketChannel;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    /**
     * 线程池中工作的线程数目
     */
    private final int POOL_MULTIPLE = 4;


    public BlockEchoServer(int port) throws IOException {
        this.port = port;
        executorService = Executors.newFixedThreadPool(
                // ava.lang.Runtime.availableProcessors() 方法: 返回可用处理器的Java虚拟机的数量。
                Runtime.getRuntime().availableProcessors() * POOL_MULTIPLE
        );
        // 打开通道
        this.serverSocketChannel = ServerSocketChannel.open();
        // 返回与ServerSocketChannel关联的ServerSocket对象，每个ServerSocketChannel对象都与一个ServerSocket对象关联
        serverSocket = serverSocketChannel.socket();
        // 使得在同一个主机上关闭了服务器，紧接着再启动服务器程序时，可以顺利绑定相同的端口
        serverSocket.setReuseAddress(true);
        // 与本地的端口绑定
        serverSocket.bind(new InetSocketAddress(port));
        log.info("the server has bind address is {}:{}", this.serverSocket.getInetAddress().getHostAddress(), this.port);
    }

    public void service() {
        while (true) {
            SocketChannel socketChannel;
            try {
                // 等待接收客户端连接，一旦有客户端连接，就会返会与当前客户端连接的SocketChannel的对象
                socketChannel = serverSocketChannel.accept();
                // 开启一个线程去处理当前客户端连接
                 executorService.submit(new RequestHandler(socketChannel));
                // 这里不开启线程
                // new RequestHandler(socketChannel).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
