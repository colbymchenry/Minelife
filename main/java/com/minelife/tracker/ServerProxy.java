package com.minelife.tracker;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.util.DateHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    protected static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Tracker]", Minelife.getDirectory().getAbsolutePath(), "tracker");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS tracker (playerUUID TEXT, dateJoined TEXT)");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ResultSet result = DB.query("SELECT * FROM tracker WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
            if(!result.next()) {
                Calendar calendar = Calendar.getInstance();
                String dateJoined = DateHelper.dateToString(calendar.getTime());
                DB.query("INSERT INTO tracker (playerUUID, dateJoined) VALUES('" + event.player.getUniqueID().toString() + "', '" + dateJoined + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
