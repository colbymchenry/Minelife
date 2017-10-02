package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ServerProxy extends CommonProxy {

    public Set<Estate> loadedEstates = new TreeSet<>();

    public MLConfig config;
    public File estatesDir = new File(Minelife.getConfigDirectory(), "estates");

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        config = new MLConfig("realestate");
        config.addDefault("selection_tool", Item.getIdFromItem(Items.golden_hoe));
        config.save();

        estatesDir.mkdir();
    }

}
