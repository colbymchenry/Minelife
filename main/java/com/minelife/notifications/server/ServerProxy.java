package com.minelife.notifications.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Minebay]", Minelife.getDirectory().getAbsolutePath(), "minebay");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS notifications (player VARCHAR(36), message TEXT, icon TEXT, type INT)");
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

}
