package com.minelife.economy;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.util.UUID;

public class ATMPenaltyListener {

    private static boolean paid;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        World world = MinecraftServer.getServer().worldServers[0];
        if(world.getWorldTime() >= 0 && world.getWorldTime() <= 1000) {
            if(!paid) {
                paid = true;
                try {
                    ResultSet result = Minelife.SQLITE.query("SELECT * FROM economy");
                    while(result.next()) {
                        UUID playerUUID = UUID.fromString(result.getString("player"));
                        int balance = MoneyHandler.getBalanceATM(playerUUID);
                        int newBalance = (int) (balance - (balance * ModEconomy.config.getDouble("atm_penalty")));
                        MoneyHandler.setATM(playerUUID, newBalance < 0 ? 0 : newBalance);
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
