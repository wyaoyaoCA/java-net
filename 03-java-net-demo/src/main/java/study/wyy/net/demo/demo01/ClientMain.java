package study.wyy.net.demo.demo01;

import java.io.IOException;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/5 9:31
 */
public class ClientMain {

    public static void main(String[] args) throws IOException {

        EchoClient client = new EchoClient("localhost", 9999);
        // 模拟同时发送10个请求
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(()->{
                try {
                    client.send("message ==> " + finalI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
