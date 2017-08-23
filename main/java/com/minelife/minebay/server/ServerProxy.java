package com.minelife.minebay.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws SQLException
    {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS item_listings (uuid VARCHAR(36), seller VARCHAR(36), price DOUBLE, title TEXT, description TEXT, item_stack TEXT, damage INT, stack_size INT, date_published TEXT)");
        Minelife.SQLITE.query("DELETE FROM item_listings WHERE date_published < '" + ItemListing.df.format(Calendar.getInstance().getTime()) + "'");
    }
}
