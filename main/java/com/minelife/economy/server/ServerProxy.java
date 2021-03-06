package com.minelife.economy.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.BillEvent;
import com.minelife.economy.ModEconomy;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.util.DateHelper;
import com.minelife.util.PlayerHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    private static long billCheckTime = 0L, atmReductionTime = 0L;

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Economy]", Minelife.getDirectory().getAbsolutePath(), "economy");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS bills (uuid VARCHAR(36), player VARCHAR(36), memo TEXT, amountDue INT, dueDate VARCHAR(36), tagCompound TEXT)");
        DB.query("CREATE TABLE IF NOT EXISTS atm (player VARCHAR(36), balance LONG)");
        DB.query("CREATE TABLE IF NOT EXISTS cashpiles (dimension INT, x INT, y INT, z INT)");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (billCheckTime == 0L) billCheckTime = System.currentTimeMillis() + (1000L * 10);
        if (atmReductionTime == 0L) atmReductionTime = System.currentTimeMillis() + (1000L * 60) * 20;

        try {
            if (System.currentTimeMillis() >= billCheckTime) {
                billCheckTime += System.currentTimeMillis() + (1000L * 10);

                ResultSet result = ModEconomy.getDatabase().query("SELECT * FROM bills WHERE duedate < '" + DateHelper.dateToString(Calendar.getInstance().getTime()) + "'");
                while (result.next()) {
                    if (!result.getString("uuid").isEmpty()) {
                        BillEvent billEvent = new BillEvent.LateEvent(new Bill(UUID.fromString(result.getString("uuid"))), null, result.getInt("amountDue"));
                        MinecraftForge.EVENT_BUS.post(billEvent);
                    }
                }
            }

            if (System.currentTimeMillis() >= atmReductionTime) {
                atmReductionTime += System.currentTimeMillis() + (1000L * 60) * 20;
                ResultSet result = ModEconomy.getDatabase().query("SELECT * FROM atm WHERE balance > 0");
                while (result.next()) {
                    ModEconomy.withdrawATM(UUID.fromString(result.getString("player")), (long) (result.getLong("balance") * 0.1));
                    Notification notification = new Notification(UUID.fromString(result.getString("player")), TextFormatting.DARK_RED + "10% was deducted from your ATM account.", NotificationType.EDGED, 5, 0xFFFFFF);
                    EntityPlayerMP player = PlayerHelper.getPlayer(UUID.fromString(result.getString("player")));
                    if(player != null) {
                        notification.sendTo(player, true, true, false);
                    } else {
                        notification.save();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
