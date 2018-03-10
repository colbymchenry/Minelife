package com.minelife.welfare;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.notification.ModNotifications;
import com.minelife.tracker.ModTracker;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WelfareListener {

    private static boolean paid = false;

    // TODO: Not working with new players
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        World world = MinecraftServer.getServer().worldServers[0];
        if(world.getWorldTime() >= 0 && world.getWorldTime() <= 1000) {
            if(!paid) {
                paid = true;
                try {
                    ResultSet result = Minelife.SQLITE.query("SELECT * FROM tracker");
                    while(result.next()) {
                        UUID playerUUID = UUID.fromString(result.getString("playerUUID"));
                        EntityPlayer player = PlayerHelper.getPlayer(playerUUID);
                        int payout = (ModTracker.getHoursPlayed(playerUUID) * 60) + 60;
                        MoneyHandler.depositATM(playerUUID, payout);
                        if(player != null) {
                            new WelfareNotification(playerUUID, payout).sendTo((EntityPlayerMP) player);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            paid = false;
        }
    }



}
