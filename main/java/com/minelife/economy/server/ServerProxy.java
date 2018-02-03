package com.minelife.economy.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.economy.packet.PacketBalanceResult;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws SQLException, IOException, InvalidConfigurationException {

        /**
         * This creates the SQL table that will store all the player's balances
         */
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS economy (player VARCHAR(36) NOT NULL, amount INT)");
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS Economy_Bills (uuid VARCHAR(36) NOT NULL, dueDate VARCHAR(36) NOT NULL, days INT, amount INT, amountDue INT, player VARCHAR(36) NOT NULL, memo TEXT, autoPay BOOLEAN, handlers TEXT, tagCompound TEXT)");

        ModEconomy.config = new MLConfig("economy");
        ModEconomy.config.addDefault("messages.balance", EnumChatFormatting.GOLD + "Balance: " + EnumChatFormatting.RED + "$%b");
        ModEconomy.config.addDefault("messages.set", EnumChatFormatting.GOLD + "%p's %w has been set to " + EnumChatFormatting.RED + "$%b");
        ModEconomy.config.addDefault("messages.deposit", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " deposited into %p's %w.");
        ModEconomy.config.addDefault("messages.withdraw", EnumChatFormatting.RED + "$%b" + EnumChatFormatting.GOLD + " withdrawn from %p's %w.");
        ModEconomy.config.save();

        FMLCommonHandler.instance().bus().register(this);
        FMLCommonHandler.instance().bus().register(new Billing.TickHandler());
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM economy WHERE player='" + event.player.getUniqueID().toString() + "'");
            if (!result.next())
                Minelife.SQLITE.query("INSERT INTO economy (player, amount) VALUES ('" + event.player.getUniqueID().toString() + "', '0')");

            Minelife.NETWORK.sendTo(new PacketBalanceResult(MoneyHandler.getBalanceATM(event.player.getUniqueID())), (EntityPlayerMP) event.player);
        } catch (Exception e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {

    }

    @SubscribeEvent
    public void onPick(PlayerEvent.ItemPickupEvent event) {

    }

}
