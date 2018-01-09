package com.minelife.essentials;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.network.PacketTitleMsg;
import com.minelife.essentials.server.commands.*;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.entity.player.EntityPlayerMP;

import java.sql.SQLException;

public class ModEssentials extends MLMod {

    public static MLConfig config;
    public static Database db;

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

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        try {
            event.registerServerCommand(new Ban());
            event.registerServerCommand(new UnBan());
            event.registerServerCommand(new Broadcast());
            event.registerServerCommand(new Warp());
            event.registerServerCommand(new DelWarp());
            event.registerServerCommand(new SetWarp());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.SERVER)
    public static void sendTitle(String title, String subTitle, int duration, EntityPlayerMP player) {
        Minelife.NETWORK.sendTo(new PacketTitleMsg(title == null || title.isEmpty() ? " " : title, subTitle == null || subTitle.isEmpty() ? " " : subTitle, duration), player);
    }
}
