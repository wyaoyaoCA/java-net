package study.wyy.net.demo.demo01;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/5 16:02
 */
@Slf4j
public class EchoClient {

    private final String serverHost;
    private final int serverPort;

    public EchoClient(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * 发送请求
     */
    public void send(String message) throws IOException {
        // 构建socket连接
        Socket socket = new Socket(serverHost, serverPort);
        BufferedReader reader = null;
        PrintWriter printWriter = null;
        try {
            // 2 获取输入流，读取服务端的响应
            InputStream inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // 3 获取输出流，向服务端写数据
            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);
            // 向服务端写数据
            printWriter.println(message);
            log.info("send request success; content is {}", message);
            // 读取服务端的响应
            String s1 = reader.readLine();
            log.info("get response success; response is {}", message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
