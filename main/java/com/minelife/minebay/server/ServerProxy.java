package com.minelife.minebay.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.util.DateHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Calendar;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        super.preInit(event);
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Minebay]", Minelife.getDirectory().getAbsolutePath(), "minebay");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS items (uuid VARCHAR(36), seller VARCHAR(36), price INT, title TEXT, description TEXT, itemstack TEXT, meta INT, stacksize INT, datepublished TEXT, storage INT)");

        // TODO: Add notification to player that the listing will be deleted in a day and they will lose their items.
        DB.query("DELETE FROM items WHERE datepublished < '" + DateHelper.dateToString(Calendar.getInstance().getTime()) + "'");
    }
}
