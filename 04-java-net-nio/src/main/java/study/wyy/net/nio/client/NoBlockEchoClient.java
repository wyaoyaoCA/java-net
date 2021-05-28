package study.wyy.net.nio.client;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wyaoyao
 * @date 2021/3/17 17:59
 */
@Slf4j
public class NoBlockEchoClient {

    private final SocketChannel socketChannel;
    private final String serverHost;
    private final int serverPort;

    private final Charset charset = Charset.forName("UTF-8");

    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
    /**
     * 委托给Selector来负责接收连接就绪事件，读就绪事件，写就绪事件
     */
    private final Selector selector;

    private final Object LOCK = new Object();

    private Thread sendThread =  new Thread(() -> {
        try {
            send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    public NoBlockEchoClient(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.socketChannel = SocketChannel.open();

        // 连接服务器
        SocketAddress remote = new InetSocketAddress(serverHost, serverPort);
        socketChannel.connect(remote);
        // 设置socketChannel为非阻塞模式
        socketChannel.configureBlocking(false);
        log.info("connect echo server success");
        // 创建一个Selector对象
        this.selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        sendThread.start();
    }

    public void submit(String message) throws IOException {
        // 编码，在用户提交的数据上追加一个结束符号，这里就以换行符吧
        log.info("submit :{}",message);
        ByteBuffer encode = encode(message + "\r\n");
        // 将要发送的内容添加到sendBuffer中
        synchronized (LOCK) {
            sendBuffer.put(encode);
        }
    }

    public void send() throws IOException {
        while (true) {
            //  需要加锁，因为selector.select();这个方法时操作的selector的all-keys集合
            int select = selector.select();
            if (select == 0) {
                continue;
            }
            // 获取已经捕获到的事件的SelectionKey的数量
            // 数量大于0，捕获到事件发生 取出捕获的事件
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            // 遍历事件，进行处理
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        // 读取服务响应结果
                        receive(key);
                    }
                    if (key.isWritable()) {
                        // 发送数据给服务端
                        sendMessage(key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (key != null) {
                        key.cancel();
                        key.channel().close();
                    }
                }
            }
        }
    }

    /**
     * 读取服务端数据
     *
     * @param key
     */
    private void receive(SelectionKey key) throws IOException {
        // 获取SelectionKey关联的SocketChannel
        SocketChannel channel = (SocketChannel) key.channel();
        // 服务端的数据读取到responseBuffer中，如果responseBuffer中的数据有一行数据
        // 就输出这一行数据，并删除这部分数据
        channel.read(responseBuffer);
        // 把极限设置为位置，位置设置为0
        responseBuffer.flip();
        // 解码
        String response = decode(responseBuffer);
        if (response.indexOf("\n") == -1) {
            // 不足一行，就返回，等待下次
            return;
        }
        // 截取出一行数据
        String line = response.substring(0, response.indexOf("\n") + 1);
        log.info("get response success; response is {}", line);
//        if(line.contains("bye")){
//            key.channel();
//            socketChannel.close();
//            log.info("关闭与服务器的连接");
//            selector.close();
//            System.exit(0);
//        }
        ByteBuffer temp = encode(line);
        responseBuffer.position(temp.limit());
        // 删除已经写入的数据
        responseBuffer.compact();
    }

    /**
     * 从sendBuffer缓冲中获取数据，向服务端发送数据
     *
     * @param key
     */
    private void sendMessage(SelectionKey key) throws IOException {
        // 获取SelectionKey关联的SocketChannel
        SocketChannel channel = (SocketChannel) key.channel();
        synchronized (LOCK) {
            // 把极限设置为位置，位置设置为0
            sendBuffer.flip();
            // 发送sendBuffer中的数据
            String decode = decode(sendBuffer);
            if(decode.length() !=0){
                channel.write(sendBuffer);
            }
            // 删除已经发送的数据
            sendBuffer.compact();
        }
    }


    private ByteBuffer encode(String content) {
        ByteBuffer encode = charset.encode(content);
        return encode;
    }

    /**
     * 解码
     *
     * @param byteBuffer
     * @return
     */
    private String decode(ByteBuffer byteBuffer) {
        CharBuffer decode = charset.decode(byteBuffer);
        return decode.toString();
    }

}
