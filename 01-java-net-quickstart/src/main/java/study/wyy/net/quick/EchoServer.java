package study.wyy.net.quick;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/2 13:52
 */
@Slf4j
public class EchoServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        while (true){
            Socket socket = null;
            try {
                // 1 创建ServerSocket， 并指定端口号为9999
                serverSocket = new ServerSocket(9999);
                // 2 等待客户端连接
                socket = serverSocket.accept();
                log.info("client connection accepted client ip is {} and port is {}",
                        socket.getInetAddress().getHostAddress(), socket.getPort());
                // 获取输入流，读取客户端数据
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // 获取输出流，向客户端写数据
                // true 表示每写入一行，printWriter的缓存就自动溢出，把数据写到目的地
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                String msg = null;
                while ((msg = bufferedReader.readLine()) != null) {
                    log.info("accept msg is {}", msg);
                    // 向客户端响应结果
                    printWriter.println("echo: " + msg);
                    if (msg.equalsIgnoreCase("exit")) {
                        // 退出循环
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                serverSocket.close();
                socket.close();
            }
        }
    }

}
