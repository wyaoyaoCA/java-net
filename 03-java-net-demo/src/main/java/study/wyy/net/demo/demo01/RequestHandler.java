package study.wyy.net.demo.demo01;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;


/**
 * @author wyaoyao
 * @description 客户端请求处理
 * @date 2021/2/5 11:10
 */
@Slf4j
public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        handle();
    }

    public void handle() {
        log.info("new client connection from {}:{} accept", socket.getInetAddress(), socket.getPort());
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = getReader(socket);
            // 获取输出流
            writer = getWriter(socket);
            // 读取客户端发来的数据
            String msg = reader.readLine();
            // 处理请求数据并响应
            log.info("get request content is {}", msg);
            // 模拟本次请求的处理时间
            Thread.sleep(3000);
            String res = "echo: " + msg;
            writer.println(res);
            log.info("send response success; response: {}", res);
        } catch (Exception e) {
            log.error("handle request error cause by {}", e.getMessage(), e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                log.error("close stream error", e);
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
