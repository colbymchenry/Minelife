package com.minelife.netty;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public class ModNetty extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {


        registerPacket(PacketSendNettyServer.Handler.class, PacketSendNettyServer.class, Side.CLIENT);
        MinecraftForge.EVENT_BUS.register(new NettyListener());
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.netty.ServerProxy.class;
    }

    public static MLConfig getConfig() {
        return ServerProxy.CONFIG;
    }

    public static ChatClient getNettyConnection() {
        return ServerProxy.CONNECTION;
    }

    public static void setNettyConnection(ChatClient connection) {
        ServerProxy.CONNECTION = connection;
    }

    public static boolean hasConnection() {
        return ModNetty.getNettyConnection() != null && ModNetty.getNettyConnection().getChannel() != null && ModNetty.getNettyConnection().getChannel().isActive();
    }
}
