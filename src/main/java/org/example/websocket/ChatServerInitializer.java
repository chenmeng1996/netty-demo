package org.example.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Chen Meng
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    /**
     * 当Channel注册进它的EventLoop时，为该Channel的Pipeline设置Handler
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec()) // http server的通用逻辑
                .addLast(new ChunkedWriteHandler()) // 发送大内存数据的通用逻辑
                .addLast(new HttpObjectAggregator(64 * 1024)) // 将接收到的chunked数据合并成一个数据
                .addLast(new HttpRequestHandler("/ws")) // 处理http请求，返回index.html
                .addLast(new WebSocketServerProtocolHandler("/ws")) // 处理websocket的通用逻辑
                .addLast(new TextWebSocketFrameHandler(group)); // websocket的业务逻辑
    }
}
