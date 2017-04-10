package com.minelife.economy.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.sql.SQLException;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        /**
         * This creates the SQL table that will store all the player's balances
         */
        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36) NOT NULL, balanceBank LONG DEFAULT 0, balanceWallet LONG DEFAULT 0, pin VARCHAR(4) NOT NULL DEFAULT '')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(ModEconomy.playerExists(event.player.getUniqueID())) return;

        try {
            Minelife.SQLITE.query("INSERT INTO players (uuid) VALUES ('" + event.player.getUniqueID().toString() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
//        if(event.world.getBlock(event.x, event.y, event.z) == BlockATM.INSTANCE || event.world.getBlock(event.x, event.y, event.z) == BlockATMTop.INSTANCE) {
//            event.setCanceled(true);
//
//            try {
//                Minelife.NETWORK.sendTo(new PacketOpenATM(ModEconomy.getPin(event.entityPlayer.getUniqueID()).isEmpty()), (EntityPlayerMP) event.entityPlayer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

}
