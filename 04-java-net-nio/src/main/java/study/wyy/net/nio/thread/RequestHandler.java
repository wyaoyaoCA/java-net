package study.wyy.net.nio.thread;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:35
 */
@Slf4j
public class RequestHandler implements Runnable {
    private final SocketChannel socketChannel;
    private Socket socket;

    public RequestHandler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            // 获得与socketChannel关联的Socket对象
            socket = socketChannel.socket();
            log.info("new client connection from {}:{} accept", socket.getInetAddress(), socket.getPort());

            // 获取输入流
            BufferedReader reader = getReader(socket);
            // 获取输出流
            PrintWriter writer = getWriter(socket);
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                log.info("accept message from client is {}", msg);

                // 返回响应给客户端
                writer.println("echo: " + msg);
                if (msg.contains("bye")) {
                    // 如果客户端发来的是bye，则退出当前会话
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socketChannel != null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
