package com.minelife.economy.server;

import com.google.common.collect.Maps;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        /**
         * This creates the SQL table that will store all the player's balances
         */
        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36) NOT NULL, balanceBank LONG DEFAULT 0, balanceWallet LONG DEFAULT 0, pin VARCHAR(4) NOT NULL DEFAULT '')");

            ModEconomy.config = new SimpleConfig(new File(Minelife.getDirectory(), "economy_config.txt"));
            Map<String, Object> defaults = Maps.newHashMap();
            defaults.put("Message_Balance", EnumChatFormatting.GOLD + "Balance: " + EnumChatFormatting.RED + "$%b");
            defaults.put("Message_Set", EnumChatFormatting.GOLD + "%p's %w has been set to " + EnumChatFormatting.RED + "$%b");
            defaults.put("Message_Deposit", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " deposited into %p's %w.");
            defaults.put("Message_Withdraw", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " withdrawn from %p's %w.");
            ModEconomy.config.setDefaults(defaults);
        } catch (Exception e) {
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
