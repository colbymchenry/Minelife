package com.minelife.economy.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.BillEvent;
import com.minelife.economy.ModEconomy;
import com.minelife.util.DateHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.client.Minecraft;
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

    private static int tick;

    public static Database DB;

    // TODO: Need to check bills if they are late or not in a ServerTick, also call the events
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Economy]", Minelife.getDirectory().getAbsolutePath(), "economy");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS bills (uuid VARCHAR(36), player VARCHAR(36), memo TEXT, amountDue INT, dueDate VARCHAR(36), tagCompound TEXT)");
        DB.query("CREATE TABLE IF NOT EXISTS atm (player VARCHAR(36), balance LONG)");
        DB.query("CREATE TABLE IF NOT EXISTS cashpiles (dimension INT, x INT, y INT, z INT)");
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        tick++;

        if(tick >= 20 * 60) {
            tick = 0;
            try {
                ResultSet result = ModEconomy.getDatabase().query("SELECT * FROM bills WHERE duedate > '" + DateHelper.dateToString(Calendar.getInstance().getTime()) + "'");
                while(result.next()) {
                    BillEvent billEvent = new BillEvent.LateEvent(new Bill(UUID.fromString(result.getString("uuid"))), null);
                    MinecraftForge.EVENT_BUS.post(billEvent);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
