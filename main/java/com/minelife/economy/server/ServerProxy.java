package com.minelife.economy.server;

import com.google.common.collect.Maps;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.packet.PacketBalanceResult;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {

        /**
         * This creates the SQL table that will store all the player's balances
         */
        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(36) NOT NULL, balanceBank LONG DEFAULT 0, balanceWallet LONG DEFAULT 0, pin VARCHAR(4) NOT NULL DEFAULT '')");
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS Economy_Bills (uuid VARCHAR(36) NOT NULL, dueDate VARCHAR(36) NOT NULL, days INT, amount LONG, amountDue LONG, player VARCHAR(36) NOT NULL, memo TEXT, autoPay BOOLEAN)");

            ModEconomy.config = new SimpleConfig(new File(Minelife.getDirectory(), "economy_config.txt"));
            ModEconomy.config.addDefault("Message_Balance", EnumChatFormatting.GOLD + "Balance: " + EnumChatFormatting.RED + "$%b");
            ModEconomy.config.addDefault("Message_Set", EnumChatFormatting.GOLD + "%p's %w has been set to " + EnumChatFormatting.RED + "$%b");
            ModEconomy.config.addDefault("Message_Deposit", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " deposited into %p's %w.");
            ModEconomy.config.addDefault("Message_Withdraw", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " withdrawn from %p's %w.");
            ModEconomy.config.setDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(new Billing.TickHandler());
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        try {
            if (!ModEconomy.playerExists(event.player.getUniqueID()))
                Minelife.SQLITE.query("INSERT INTO players (uuid) VALUES ('" + event.player.getUniqueID().toString() + "')");

            Minelife.NETWORK.sendTo(new PacketBalanceResult(ModEconomy.getBalance(event.player.getUniqueID(), false), ModEconomy.getBalance(event.player.getUniqueID(), true)), (EntityPlayerMP) event.player);
        } catch (Exception e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

}
