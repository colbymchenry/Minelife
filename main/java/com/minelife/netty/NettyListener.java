package com.minelife.netty;

import com.minelife.Minelife;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NettyListener {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Minelife.getNetwork().sendTo(new PacketSendNettyServer(ModNetty.getConfig().getString("netty_ip"), ModNetty.getConfig().getInt("netty_port")), (EntityPlayerMP) event.player);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ModNetty.getNettyConnection().shutdown();
    }

}
