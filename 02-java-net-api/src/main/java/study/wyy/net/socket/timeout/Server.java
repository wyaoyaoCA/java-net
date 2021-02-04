package study.wyy.net.socket.timeout;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 17:18
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        Socket socket = serverSocket.accept();
        // 设置超时时间
        socket.setSoTimeout(20000);
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = -1;
        do {
            try {
                len = inputStream.read(bytes);
                if (len != -1) {
                    // 将数据放到缓冲里
                    buffer.write(bytes);
                }
            } catch (SocketTimeoutException e) {
                log.info("读取超时");
            }
        } while (len != -1);
        // 输出到控制台
        log.info(new String(buffer.toByteArray()));

    }
}
