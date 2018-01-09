package com.minelife.essentials;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.network.PacketTitleMsg;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

public class ModEssentials extends MLMod {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketTitleMsg.Handler.class, PacketTitleMsg.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.essentials.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.essentials.client.ClientProxy.class;
    }

    @SideOnly(Side.SERVER)
    public static void sendTitle(String title, String subTitle, int duration, EntityPlayerMP player) {
        Minelife.NETWORK.sendTo(new PacketTitleMsg(title == null || title.isEmpty() ? " " : title, subTitle == null || subTitle.isEmpty() ? " " : subTitle, duration), player);
    }
}
