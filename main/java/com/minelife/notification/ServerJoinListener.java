package com.minelife.notification;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ServerJoinListener {

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE player='" + event.player.getUniqueID().toString() + "'");
            while(result.next()) {
                try {
                    Class<? extends AbstractNotification> clazz = (Class<? extends AbstractNotification>) Class.forName(result.getString("clazz"));
                    AbstractNotification notification = clazz.newInstance();
                    notification.uniqueID = UUID.fromString(result.getString("uuid"));
                    notification.readFromDB();
                    notification.sendTo((EntityPlayerMP) event.player);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
            Minelife.SQLITE.query("DELETE FROM notifications WHERE player='" + event.player.getUniqueID().toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
