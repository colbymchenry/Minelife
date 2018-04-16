package com.minelife.essentials;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.network.PacketTitleMsg;
import com.minelife.essentials.server.ServerProxy;
import com.minelife.essentials.server.commands.*;
import com.minelife.util.MLConfig;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;

public class ModEssentials extends MLMod {

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
            event.registerServerCommand(new SetHome());
            event.registerServerCommand(new Home());
            event.registerServerCommand(new SetSpawn());
            event.registerServerCommand(new Spawn());
            event.registerServerCommand(new TeleportAccept());
            event.registerServerCommand(new TeleportAsk());
            event.registerServerCommand(new TeleportAskHere());
            event.registerServerCommand(new TeleportDeny());
            event.registerServerCommand(new Whisper());
            event.registerServerCommand(new Reply());
            event.registerServerCommand(new TempBan());
            event.registerServerCommand(new Heal());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.SERVER)
    public static void sendTitle(String title, String subTitle, int duration, EntityPlayerMP player) {
        Minelife.getNetwork().sendTo(new PacketTitleMsg(title == null || title.isEmpty() ? " " : title, subTitle == null || subTitle.isEmpty() ? " " : subTitle, duration), player);
    }

    public static MLConfig getConfig() {
        return ServerProxy.CONFIG;
    }

    public static Database getDB() {
        return ServerProxy.DB;
    }
}
