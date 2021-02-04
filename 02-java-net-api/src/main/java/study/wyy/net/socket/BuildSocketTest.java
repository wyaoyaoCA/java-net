package study.wyy.net.socket;


import org.junit.Test;

import java.io.IOException;
import java.net.*;
;

/**
 * @author wyaoyao
 * @description: 构造Socket对象
 * @date 2021/2/2 15:50
 */
public class BuildSocketTest {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        // 指定远端服务的IP地址和端口号
        SocketAddress remoteAddr = new InetSocketAddress("www.baidu.com",80);
        // 设置超时时间为60000毫秒 1分钟
        socket.connect(remoteAddr,60000);
    }
    @Test
    public void testSetTimeout() throws IOException {
        Integer.valueOf(2);
        Socket socket = new Socket();
        // 指定远端服务的IP地址和端口号
        SocketAddress remoteAddr = new InetSocketAddress("localhost",9999);
        // 设置超时时间为60000毫秒 1分钟
        socket.connect(remoteAddr,60000);
    }

    @Test
    public void testRemoteAddr() throws IOException {
        // 1 无参构造
        Socket socket = new Socket();
        // 指定远端服务的IP地址和端口号
        SocketAddress remoteAddr = new InetSocketAddress("localhost",9999);
        // 设置超时时间为
        socket.connect(remoteAddr);

        // 通过有参构造
        Socket socket1 = new Socket("localhost", 9999);
        Socket socket2 = new Socket(InetAddress.getByName("localhost"),9999);
    }

    @Test
    public void test() throws IOException {
        // 代理服务器地址
        String proxyAddr = "localhost";
        // 代理服务器端口
        int proxyPort = 9999;
        // 创建代理对象
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyAddr, proxyPort));
        // 连接到远程服务
        Socket socket = new Socket(proxy);
        socket.connect(new InetSocketAddress("www.baidu.com",80));
        boolean b = socket.isConnected() && !socket.isClosed();
    }


}
