package com.minelife.util.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MLPacket implements IMessageHandler {

    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public synchronized void execute(IMessage message, MessageContext ctx) {}

    @Override
    public final synchronized IMessage onMessage(IMessage message, MessageContext ctx) {
        pool.submit(() -> {
            try {
                execute(message, ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }
}
