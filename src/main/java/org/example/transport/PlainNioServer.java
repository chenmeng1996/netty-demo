package org.example.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 未使用netty的非阻塞版本
 *
 * @author Chen Meng
 */
public class PlainNioServer {
    public static void main(String[] args) {
    }

    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ssocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ssocket.bind(address);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT); // 服务端socketChannel注册到selector，监听accept事件
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
        for (;;) {
            try {
                selector.select(); // 阻塞，等待需要处理的事件
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys(); // 获取所有事件
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) { // 检查事件是否是一个新的已经就绪可以被接受的连接（accept事件）
                        ServerSocketChannel server = (ServerSocketChannel) key.channel(); // 该事件通知的channel（此处是服务端channel）
                        SocketChannel client = server.accept(); // 建立连接，获取客户端channel
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg); // 客户端注册到selecter
                        System.out.println("Accepted connection from "+ client);
                    }
                    if (key.isWritable()) { // 检查套接字是否已经准备好写数据（write事件）
                        SocketChannel client = (SocketChannel) key.channel(); // 该事件通知的channel（此处是客户端channel）
                        ByteBuffer buffer = (ByteBuffer) key.attachment(); // 该channel需要写入的数据
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) { // 数据写入已连接的客户端
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        // ignore
                    }
                }
            }
        }
    }
}
