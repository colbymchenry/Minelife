package com.minelife.notifications.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.notifications.PacketNotification;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Minebay]", Minelife.getDirectory().getAbsolutePath(), "minebay");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS notifications (player VARCHAR(36), message TEXT, icon TEXT, type INT, duration INT, bgColor INT, txtColor INT)");
        MinecraftForge.EVENT_BUS.register(this);
    }

    // TODO: Send important notification of current count of notifications
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ResultSet result = DB.query("SELECT * FROM notifications WHERE player='" + event.player.getUniqueID().toString() + "'");
            while(result.next()) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        Notification notification = new Notification(event.getPlayer().getUniqueID(), TextFormatting.DARK_GRAY + "Here is a sample test of a", NotificationType.BLACK, 5, Color.WHITE.getRGB());
        Minelife.getNetwork().sendTo(new PacketNotification(notification, true, true), (EntityPlayerMP) event.getPlayer());
    }

}
