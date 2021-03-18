package study.wyy.net.nio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author wyaoyao
 * @date 2021/3/18 8:50
 * 混合用阻塞模式与非阻塞模式
 */
@Slf4j
public class MixEchoServer {

    /**
     * 委托给Selector来负责接收连接就绪事件，读就绪事件，写就绪事件
     * 只要ServerSocketChannel以及SocketChannel向Selector注册了特定事件，
     * Selector就会监控这些事件是否发生。
     */
    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    private final ServerSocket serverSocket;

    private final int port;

    private final Charset charset = Charset.forName("UTF-8");

    private final Object LOCK = new Object();

    public MixEchoServer(int port) throws IOException {
        // 创建一个Selector对象
        this.selector = Selector.open();
        // 创建一个ServerSocketChannel对象
        serverSocketChannel = ServerSocketChannel.open();
        // 返回与ServerSocketChannel关联的ServerSocket对象，
        // 每个ServerSocketChannel对象都与一个ServerSocket对象关联
        serverSocket = serverSocketChannel.socket();
        // 使得在同一个主机上关闭了服务器，紧接着再启动服务器程序时，可以顺利绑定相同的端口
        serverSocket.setReuseAddress(true);
        // 负责接收客户连接的线程按照阻塞模式
        serverSocketChannel.configureBlocking(true);
        // 绑定本地端口
        this.port = port;
        serverSocketChannel.bind(new InetSocketAddress(port));
        log.info("the server has bind address is {}:{}", this.serverSocket.getInetAddress().getHostAddress(), this.port);
    }

    /**
     * 接收客户端连接
     */
    public void accept() throws IOException {
        // 开启一个线程处理
        new Thread(() -> {
            while (true) {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = serverSocketChannel.accept();
                    // 但是接收数据和响应结果要非阻塞
                    socketChannel.configureBlocking(false);
                    // 创建一个缓冲区，用于存放客户端发来的数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    // SocketChannel向selector注册读就绪事件和写就绪事件
                    // 并把byteBuffer作为附件注册进去，在读写事件发生的时候获取byteBuffer，进行数据读写
                    // 需要加锁，因为这个方法时操作的selector的all-keys集合
                    synchronized (LOCK) {
                        // 防止死锁：当获取已经捕获的事件的SelectionKey的selector.select()方法会阻塞
                        // 如果在调用register方法的时，正好阻塞了，register也就会阻塞在这
                        // 所以调用wakeup唤醒selector
                        selector.wakeup();
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, byteBuffer);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("accept client connection from {}:{} accept", socketChannel.socket().getInetAddress(), socketChannel.socket().getPort());
            }
        },
                "accept").start();

    }

    /**
     * 负责接收数据和响应结果
     *
     * @throws IOException
     */
    public void service() throws IOException {
        // 不断轮询
        while (true) {
            //  需要加锁，因为selector.select();这个方法时操作的selector的all-keys集合
            synchronized (LOCK) {
            }
            int select = selector.select();
            if (select == 0) {
                // 如果没有捕获到事件，那就继续轮询
                continue;
            }
            // 获取已经捕获到的事件的SelectionKey的集合
            Set<SelectionKey> readyKes = selector.selectedKeys();
            // 遍历readyKes，挨个处理
            Iterator<SelectionKey> iterator = readyKes.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    // 取出一个key
                    key = iterator.next();
                    iterator.remove();
                    // 判断事件类型
                    if (key.isReadable()) {
                        // 处理读就绪事件
                        handleReadable(key);
                    }
                    if (key.isWritable()) {
                        // 处理写就绪事件
                        handleWritable(key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (null != null) {
                        // 失效掉这个key, selector不再感兴趣这个SelectionKey感兴趣的事件
                        key.cancel();
                        // 关闭与这个key关联的socketChannel
                        key.channel().close();
                    }
                }
            }

        }
    }


    private void handleAcceptable(SelectionKey selectionKey) throws IOException {
        // 获取与SelectionKey关联的serverSocketChannel，就是通过serverSocketChannel来传输数据的
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        // 获取与客户端连接的SocketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        log.info("accept client connection from {}:{} accept", socketChannel.socket().getInetAddress(), socketChannel.socket().getPort());
        // 设置socketChannel为非阻塞
        socketChannel.configureBlocking(false);
        // 创建一个缓冲区，用于存放客户端发来的数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // SocketChannel向selector注册读就绪事件和写就绪事件
        // 并把byteBuffer作为附件注册进去，在读写事件发生的时候获取byteBuffer，进行数据读写
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, byteBuffer);
    }

    private void handleReadable(SelectionKey selectionKey) throws IOException {
        // 获取关联的的附件
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        // 获取与当前SelectionKey关联的SocketChannel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建ByteBuffer字节缓冲区，用于存放读取到的数据
        ByteBuffer readBuffer = ByteBuffer.allocate(32);
        socketChannel.read(readBuffer);
        // flip()：把极限设为位置，再把位置设为0
        readBuffer.flip();

        // 把buffer的极限设置为容量
        buffer.limit(buffer.capacity());
        // 把readBuffer中的数据拷贝到buffer中
        // 假定buffer的容量足够大，不会出现缓冲区溢出的情况
        buffer.put(readBuffer);
    }

    private void handleWritable(SelectionKey selectionKey) throws IOException {
        // 获取关联的的ByteBuffer
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        // 获取与当前SelectionKey关联的SocketChannel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // flip()：把极限设为位置，再把位置设为0
        buffer.flip();
        // 解码，将buffer中的字节转为字符串
        String data = decode(buffer);
        // 如果buffer中的数据不足用户一次请求（这里就指一行）,就返回
        if (data.indexOf("\r\n") == -1) {
            return;
        }
        // 截取一行数据
        String req = data.substring(0, data.indexOf("\n") + 1);
        // 将字符串进行编码
        ByteBuffer responseBuffer = encode("echo:" + req);
        // 输出responseBuffer中的所有字节
        while (responseBuffer.hasRemaining()) {
            socketChannel.write(responseBuffer);
        }
        ByteBuffer temp = encode(req);
        // 把buffer的位置设置为temp的极限
        buffer.position(temp.limit());
        // 删除buffer中已经处理的数据
        buffer.compact();
        if (req.contains("bye")) {
            // 如果客户端发来的是bye，则退出当前会话, 失效这个key
            selectionKey.cancel();
            socketChannel.close();
        }

    }

    private String decode(ByteBuffer byteBuffer) {
        CharBuffer decode = charset.decode(byteBuffer);
        return decode.toString();
    }

    private ByteBuffer encode(String content) {
        ByteBuffer encode = charset.encode(content);
        return encode;
    }
}
