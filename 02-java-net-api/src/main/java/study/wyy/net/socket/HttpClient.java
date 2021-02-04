package study.wyy.net.socket;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author wyaoyao
 * @description: 模拟访问请求百度百度首页
 * @date 2021/2/3 10:32
 */
public class HttpClient {
    final String host = "www.baidu.com";
    //final String host = "www.javathinker.net";
    final int port = 80;
    final Socket socket;


    public HttpClient() throws IOException {
        this.socket = new Socket(host,port);
    }

    public void sendRequest() throws IOException {
        try{
            // 换号符
            String br  = "\r\n";
            // 构造请求：
            StringBuffer sb = new StringBuffer("GET " + "/ " + "HTTP/1.1" + br);
            sb.append("Host: " + this.host + br);
            // Accept-Language: zh-CN,zh;q=0.9
            sb.append("Accept-Language: " + "zh-CN,zh;q=0.9" + br);
            // 设置浏览器支持的编码，这里不要设置，否则拿到数据还得解码
            //sb.append("Accept-Encoding: " + "gzip, deflate" + br);
            sb.append("Accept: " + "text/html" + br);
            sb.append("Connection: " + "keep-alive" + br + br);
            // 发送请求
            OutputStream outputStream = this.socket.getOutputStream();
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();

            // 获取响应结果
            InputStream inputStream = this.socket.getInputStream();
            byte[] buff = new byte[1024];
            ByteArrayOutputStream buffWriter = new ByteArrayOutputStream();
            int len =-1;
            while ((len = inputStream.read(buff)) != -1){
                // 将读取到的数据写入
                buffWriter.write(buff,0,len);
            }
            byte[] bytes = buffWriter.toByteArray();
            // 将响应结果写到控制台，也可以写到文件
            System.out.println(new String(bytes));
        }catch (Exception e){

        }finally {
            this.socket.close();
        }

    }

    public static void main(String[] args) throws IOException {
        HttpClient httpClient = new HttpClient();
        httpClient.sendRequest();
    }

}
