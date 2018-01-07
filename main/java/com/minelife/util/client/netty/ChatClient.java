package com.minelife.util.client.netty;


import com.minelife.Minelife;
import com.minelife.util.MLConfig;
import cpw.mods.fml.client.FMLClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

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
            Bootstrap bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChatClientInitializer());
            System.out.println("CONNECTED TO REMOTE NETTY SERVER!");
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            group.shutdownGracefully();
        }

    }

    public Channel getChannel() {
        return channel;
    }

    public void shutdown() {
        group.shutdownGracefully();
    }


}
