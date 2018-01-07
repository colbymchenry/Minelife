package com.minelife.util.client.netty;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

public class NettyPlayerListener {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Minelife.NETWORK.sendTo(new PacketSendNettyServer(Minelife.config.getString("netty_ip"), Minelife.config.getInt("netty_port")), (EntityPlayerMP) event.player);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minelife.NETTY_CONNECTION.shutdown();
    }

}
