package study.wyy.net.socket.timeout;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 17:14
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost",8000);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello world".getBytes());
        TimeUnit.MINUTES.sleep(1);
        log.info("client 醒来了");
        socket.close();
    }
}
