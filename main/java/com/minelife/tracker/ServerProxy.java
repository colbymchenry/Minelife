package com.minelife.tracker;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerProxy extends MLProxy {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS tracker (playerUUID TEXT, dateJoined TEXT)");
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
           ResultSet result = Minelife.SQLITE.query("SELECT * FROM tracker WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
           if(!result.next()) {
               Calendar calendar = Calendar.getInstance();
               String dateJoined = df.format(calendar.getTime());
               Minelife.SQLITE.query("INSERT INTO tracker (playerUUID, dateJoined) VALUES('" + event.player.getUniqueID().toString() + "', '" + dateJoined + "')");
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
