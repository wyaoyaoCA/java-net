package study.wyy.net.socket;

import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 9:35
 */
public class NetworkInterfaceTest {

    @Test
    public void testBuild() throws SocketException, UnknownHostException {
        NetworkInterface eth0 = NetworkInterface.getByName("eth0");
        System.out.println(eth0);
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(addr);
        System.out.println(byInetAddress);
    }

    @Test
    public void testGetNetworkInterfaces() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()){
            System.out.println(networkInterfaces.nextElement());
        }
    }


}
