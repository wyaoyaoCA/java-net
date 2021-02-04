package study.wyy.net.socket;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 14:03
 */
@Slf4j
public class SocketStatusTest {

    @Test
    public void test() throws IOException {
        Socket socket = new Socket();
        System.out.println("isClosed: " + socket.isClosed());
        System.out.println("isConnected: " +socket.isConnected());
        System.out.println("isBound: " +socket.isBound());
        SocketAddress remoteAddr =  new InetSocketAddress("www.baidu.com",80);
        socket.connect(remoteAddr);
        System.out.println("========连接远程主机之后================");
        System.out.println("isClosed: " + socket.isClosed());
        System.out.println("isConnected: " +socket.isConnected());
        System.out.println("isBound: " +socket.isBound());
        socket.close();
        System.out.println("========关闭之后=================");
        System.out.println("isClosed: " + socket.isClosed());
        System.out.println("isConnected: " +socket.isConnected());
        System.out.println("isBound: " +socket.isBound());
    }


    @Test(expected = SocketException.class)
    public void test2() throws IOException {
        Socket socket = new Socket();
        SocketAddress remoteAddr =  new InetSocketAddress("www.baidu.com",80);
        socket.connect(remoteAddr);
        socket.close();
        InputStream inputStream = socket.getInputStream();

    }
    @Test
    public void test3() throws IOException {
        Socket socket = new Socket();
        SocketAddress remoteAddr =  new InetSocketAddress("www.baidu.com",80);
        socket.connect(remoteAddr);
        socket.shutdownInput();
        InputStream inputStream = socket.getInputStream();

    }
}
