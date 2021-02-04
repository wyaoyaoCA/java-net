package study.wyy.net.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/3 14:39
 */
public class Sender {
    private String host;

    private int port;

    private final Socket socket;

    private static int stopWay = 1;

    private final int NATURAL_STOP=1; // 正常关闭
    private final int SUDDEN_STOP=2;  // 突然终止程序
    private final int SOCKET_STOP=3;  // 关闭socket 在结束程序
    private final int OUTPUT_STOP=4;  // 关闭输出流，再结束程序

    public Sender() throws IOException {
        this.host = "localhost";
        this.port = 8000;
        this.socket = new Socket(host,port);
    }



    public void send() throws IOException, InterruptedException {

        PrintWriter writer = this.getWriter();
        for (int i = 0; i < 20; i++) {
            String msg = "hello_" + i;
            // 发送数据
            System.out.println("send: " + msg);
            writer.println(msg);
            TimeUnit.MILLISECONDS.sleep(500);
            if (i == 2){
               if(stopWay == SUDDEN_STOP){
                   // 突然终止程序
                   System.out.println("突然终止程序");
                   System.exit(0);
               }else if (stopWay == SOCKET_STOP){
                   System.out.println("关闭socket，并终止程序");
                   socket.close();
                   break;
               }else if(stopWay == OUTPUT_STOP){
                   System.out.println("关闭输出流，并终止程序");
                   socket.shutdownOutput();
                   break;
               }
            }
        }
        if(stopWay == NATURAL_STOP){
            socket.close();
        }
    }

    public PrintWriter getWriter() throws IOException {
        OutputStream outputStream = this.socket.getOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream),true);
        return printWriter;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length>0)stopWay=Integer.parseInt(args[0]);
        new Sender().send();
    }
}
