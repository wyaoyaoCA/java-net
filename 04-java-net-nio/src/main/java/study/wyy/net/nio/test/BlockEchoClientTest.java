package study.wyy.net.nio.test;

import study.wyy.net.nio.client.BlockEchoClient;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author wyaoyao
 * @date 2021/3/17 18:26
 */
public class BlockEchoClientTest {
    public static void main(String[] args) throws IOException {
        Arrays.asList(1,2,3).stream().forEach(i->{
            new Thread(()->{
                BlockEchoClient client = null;
                try {
                    client = new BlockEchoClient("localhost", 10010);
                    client.send("hello! from " + Thread.currentThread().getName());
                    client.send("你好! from " + Thread.currentThread().getName());
                    client.send("bye! from " +  Thread.currentThread().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(client != null){
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"client" +i).start();

        });
    }
}
