package com.minelife.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChatClient {

    private Channel channel;
    private final String host;
    private final int port;
    private  EventLoopGroup group;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new NettyClientInitializer());
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Channel getChannel() {
        return channel;
    }

    public void shutdown() {
        if(group != null) group.shutdownGracefully();
    }

}
