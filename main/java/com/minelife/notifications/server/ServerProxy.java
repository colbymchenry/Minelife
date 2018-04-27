package com.minelife.notifications.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.TeleportHandler;
import com.minelife.notifications.ModNotifications;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.notifications.PacketNotification;
import com.minelife.util.DateHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class ServerProxy extends MLProxy {

    static boolean turnedOffFireSpread = false;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
         MinecraftForge.EVENT_BUS.register(this);

        ModNotifications.getDatabase().query("DELETE FROM notifications WHERE datepublished < '" + DateHelper.dateToString(Calendar.getInstance().getTime()) + "'");
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            if(!turnedOffFireSpread) {
                MinecraftServer server = FMLServerHandler.instance().getServer();
                server.getCommandManager().executeCommand(server, "/gamerule doFireTick false");
                turnedOffFireSpread = true;
            }

            ResultSet result = ModNotifications.getDatabase().query("SELECT * FROM notifications WHERE player='" + event.player.getUniqueID().toString() + "'");
            int total = 0;
            while (result.next()) {
                total++;
                Notification notification = new Notification(result);
                Minelife.getNetwork().sendTo(new PacketNotification(notification, false, false, true), (EntityPlayerMP) event.player);
            }

            if (total > 0) {
                Notification notification = new Notification(event.player.getUniqueID(), TextFormatting.YELLOW + "New Notifications!" + TextFormatting.WHITE + "\nYou have " + total + " notifications.", new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/important_notification_icon.png"), NotificationType.EDGED, 10, 0x082C4C);
                Minelife.getNetwork().sendTo(new PacketNotification(notification, true, true, false), (EntityPlayerMP) event.player);
            }

            ModNotifications.getDatabase().query("DELETE FROM notifications WHERE player='" + event.player.getUniqueID().toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
