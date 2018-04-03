package com.minelife.realestate.server;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.Estate;
import com.minelife.util.MLConfig;
import com.minelife.util.NBTHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static MLConfig CONFIG;
    public static Set<Estate> ESTATES = Sets.newTreeSet();
    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[RealEstate]", Minelife.getDirectory().getAbsolutePath(), "realestate");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS estates (uuid VARCHAR(36), tagCompound TEXT)");
        Bill.createTable(DB, "bills");
        loadEstates();

        CONFIG = new MLConfig("realestate");

        Blocks.IRON_BLOCK.setResistance(55);
        Blocks.DIAMOND_BLOCK.setResistance(100);
        Blocks.OBSIDIAN.setResistance(80);
        Blocks.STONEBRICK.setResistance(40);

        MinecraftForge.EVENT_BUS.register(new SelectionListener());
        MinecraftForge.EVENT_BUS.register(new EstateListener());
    }

    private void loadEstates() throws SQLException {
        ESTATES.clear();
        ResultSet result = DB.query("SELECT * FROM estates");
        while(result.next())
            ESTATES.add(new Estate(UUID.fromString(result.getString("uuid")), NBTHelper.fromString(result.getString("tagCompound"))));
    }
}
