package study.wyy.net.quick;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/2 14:15
 */
@Slf4j
public class EchoClient {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        try {
            // 1 构建socket，指定服务端的ip和端口
            socket = new Socket("localhost", 9999);
            // 2 获取输入流，读取服务端的响应
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // 3 获取输出流，向服务端写数据
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            // 读取控制台输入
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = null;
            while ((msg = consoleReader.readLine()) != null) {
                // 向服务端写数据
                printWriter.println(msg);
                // 读取服务端的响应
                System.out.println(reader.readLine());
                if (msg.equalsIgnoreCase("exit")) {
                    // 跳出循环，不再读取数据
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
