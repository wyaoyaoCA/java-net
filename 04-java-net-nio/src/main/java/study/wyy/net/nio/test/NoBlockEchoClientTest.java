package study.wyy.net.nio.test;

import study.wyy.net.nio.client.BlockEchoClient;
import study.wyy.net.nio.client.NoBlockEchoClient;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author wyaoyao
 * @date 2021/3/17 18:26
 */
public class NoBlockEchoClientTest {
    public static void main(String[] args) throws IOException {
        NoBlockEchoClient client = null;
        try {
            client = new NoBlockEchoClient("localhost", 10010);
            client.submit("hello! from " + Thread.currentThread().getName());
            client.submit("你好! from " + Thread.currentThread().getName());
            client.submit("bye! from " + Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
