package com.minelife.netty;

import com.minelife.netty.handlers.NettyHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Map<Channel, NettyProcess> processes = new HashMap<>();
    public static final Map<Integer, NettyHandler> handlers = new HashMap<>();

    static {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel incoming = ctx.channel();
        if(s.startsWith("START:")) {
            processes.put(incoming, new NettyProcess(Integer.parseInt(s.split("\\:")[1])));
            return;
        }
        if(s.startsWith("END:") && processes.containsKey(incoming)) {
            processes.get(incoming).finish();
            int processID = Integer.parseInt(s.split("\\:")[1]);
            handlers.get(processID).handle(processes.get(incoming));
            return;
        }
        if(processes.containsKey(incoming))processes.get(incoming).add(s);
    }
}
