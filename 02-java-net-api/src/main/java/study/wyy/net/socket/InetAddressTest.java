package study.wyy.net.socket;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Queue;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 8:24
 */
public class InetAddressTest {

    @Test
    public void testGetByName() throws IOException {
        // 获取ipv4地址
        InetAddress ipv4 = InetAddress.getByName("127.0.0.1");
        // 获取ipv6地址
        InetAddress ipv6 = InetAddress.getByName("1030::C9B4:FF12:48AA:1A2B");
        //也可以通过主机名获取
        InetAddress byName = InetAddress.getByName("www.baidu.com");
    }

    /***
     * 测试获取host name
     * @throws IOException
     */
    @Test
    public void testGetHostName() throws IOException {

        //也可以通过主机名获取
        InetAddress baidu = InetAddress.getByName("www.baidu.com");
        String canonicalHostName = baidu.getCanonicalHostName();
        System.out.println(canonicalHostName);
        String hostName = baidu.getHostName();
        System.out.println(hostName);
        String hostAddress = baidu.getHostAddress();
        System.out.println(hostAddress);
        String hostName1 = InetAddress.getLocalHost().getHostName();
        System.out.println(hostName1);
    }

    @Test
    public void testReachable() throws IOException {
        InetAddress baidu = InetAddress.getByName("www.baidu.com");
        // 参数表示超时时间，单位毫秒
        boolean reachable = baidu.isReachable(1000);
        System.out.println(reachable);
    }

    @Test
    public void test()  {
        try {

            String BLACK_HOLE = "sbl.spamhaus.org";
            String ip = "108.33.56.27";
            String query = BLACK_HOLE;
            // 反转这个ip地址的字节
            for (byte b : ip.getBytes()) {
                int unsignedByte = b < 0 ? b + 256 : b;
                query = unsignedByte + "." + query;
            }
            // query: 108.33.56.27.sbl.spamhaus.org
            InetAddress.getByName(query);
        } catch (UnknownHostException e) {
            System.out.println("不是一个垃圾邮件发送者");
        }
    }
}
