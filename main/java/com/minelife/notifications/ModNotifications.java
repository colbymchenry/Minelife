package com.minelife.notifications;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.util.DateHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

public class ModNotifications extends MLMod {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketNotification.Handler.class, PacketNotification.class, Side.CLIENT);

        DB = new SQLite(Logger.getLogger("Minecraft"), "[Notifications]", Minelife.getDirectory().getAbsolutePath(), "notifications");
        DB.open();
        try {
            DB.query("CREATE TABLE IF NOT EXISTS notifications (player VARCHAR(36), message TEXT, icon TEXT, type INT, duration INT, bgColor INT, datepublished TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.notifications.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.notifications.server.ServerProxy.class;
    }

    public static Database getDatabase() {
        return DB;
    }
}
