package study.wyy.net.socket.mail;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description
 * @date 2021/2/4 10:19
 */
public class MailSender {
    /***
     * smpt 服务器
     */
    private String smtpServer;

    /****
     *
     */
    private int port = 25;

    private String CLIENT_PREFIX = "client>";
    private String SERVER_PREFIX = "server>";

    public MailSender(String smtpServer, int port) {
        this.smtpServer = smtpServer;
        this.port = port;
    }

    public void send(Mail mail) {
        Socket socket = null;
        try {
            // 构建和邮箱服务器连接的socket
            socket = new Socket(this.smtpServer, this.port);
            BufferedReader bufferedReader = getReader(socket);
            PrintWriter printWriter = getWriter(socket);
            // 获取本地主机地址
            String localhost = InetAddress.getLocalHost().getHostName();
            // 测试HELO指令
            sendAndReceive("Hello " + localhost, bufferedReader, printWriter);
            // 测试MAIL FROM指令
            sendAndReceive("MAIL FROM: <" + mail.getFrom() + ">", bufferedReader, printWriter);
            // 测试RCPT TO指令
            sendAndReceive("RCPT TO <" + mail.getTo() + ">", bufferedReader, printWriter);
            // 测试DATA 指令
            sendAndReceive("DATA",bufferedReader, printWriter);
            // 发送内容
            sendAndReceive(mail.getData(),bufferedReader,printWriter);
            // . 告知邮件内容结束
            sendAndReceive(".",bufferedReader,printWriter);
            // 结束
            sendAndReceive("QUIT",bufferedReader,printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     *
     * @param command: smtp 协议指令
     * @param bufferedReader
     * @param printWriter
     */
    private void sendAndReceive(String command, BufferedReader bufferedReader, PrintWriter printWriter) throws IOException {
        // 发送指令，为了区分是客户端发送的，还是服务端响应的，输出的是的时候会拼接一个前缀
        if (command != null && command.length() > 0) {
            System.out.println(CLIENT_PREFIX + command);
            // 发送指令
            printWriter.write(command);
        }
        String response = null;
        if ((response = bufferedReader.readLine()) != null) {
            System.out.println(SERVER_PREFIX + response);
        }
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public static void main(String[] args) {
        Mail mail = new Mail(
                "www.wangyaoyao93@163.com",
                "www.wangyaoyao93@qq.com",
                "test send mail",
                "hello"
        );


        new MailSender("smtp.163.com", 25).send(mail);
    }
}
