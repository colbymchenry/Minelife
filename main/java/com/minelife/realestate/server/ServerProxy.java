package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.SelectionController.ServerSelector;
import com.minelife.realestate.Zone;
import com.minelife.realestate.ZoneInfoController;
import com.minelife.realestate.sign.BlockForSaleSign;
import com.minelife.realestate.sign.ListenerForSaleSign;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        try {
            ModRealEstate.config = new SimpleConfig(new File(Minelife.getDirectory(), "realestate_config.txt"));
            ModRealEstate.config.addDefault("PricePerBlock", 2);
            ModRealEstate.config.setDefaults();
        } catch (Exception e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }

        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS RealEstate_Zones (" +
                    "region VARCHAR(36) NOT NULL, " +
                    "owner VARCHAR(36) NOT NULL, " +
                    "members TEXT NOT NULL DEFAULT '[]', " +
                    "publicPermissions TEXT NOT NULL DEFAULT '', " +
                    "intro TEXT NOT NULL DEFAULT '', " +
                    "outro TEXT NOT NULL DEFAULT '')");

            Zone.initZones();
        } catch (SQLException e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }

        MinecraftForge.EVENT_BUS.register(new ServerSelector());
        MinecraftForge.EVENT_BUS.register(new LandController());
        MinecraftForge.EVENT_BUS.register(new ListenerForSaleSign());
        FMLCommonHandler.instance().bus().register(new ZoneInfoController.PlayerTickListener());
    }

}
