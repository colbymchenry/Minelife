package com.minelife.netty.handlers;

import com.minelife.netty.NettyProcess;

public abstract class NettyHandler {

    public abstract void handle(NettyProcess process);

}
