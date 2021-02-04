package study.wyy.net.socket;

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
 * @date 2021/2/3 16:22
 */
public class SocketOption {

    @Test
    public void test() throws SocketException {
        Socket socket = new Socket();
        // 获取
        boolean tcpNoDelay = socket.getTcpNoDelay();
        System.out.println("tcpNoDelay的默认值：" + tcpNoDelay);
        // 设置
        socket.setTcpNoDelay(true);
    }



    @Test
    public void test1() throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(1000);
        // 必须在获取输入流之前设置
        InputStream inputStream = socket.getInputStream();

    }
}
