package com.minelife.minebay.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws SQLException
    {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS item_listings (uuid VARCHAR(36), seller VARCHAR(36), price INT, title TEXT, description TEXT, item_stack TEXT, damage INT, stack_size INT, date_published TEXT)");
        Minelife.SQLITE.query("DELETE FROM item_listings WHERE date_published < '" + ItemListing.df.format(Calendar.getInstance().getTime()) + "'");
    }
}
