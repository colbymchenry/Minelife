package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.EstateHandler;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Arrays;

public class ServerProxy extends CommonProxy {

    public static MLConfig config;
    public File estatesDir = new File(Minelife.getConfigDirectory(), "estates");

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        config = new MLConfig("realestate");
        config.addDefault("selection_tool", Item.getIdFromItem(Items.golden_hoe));
        config.addDefault("messages.estate_create", "Estate created!");
        config.addDefault("black-listed-blocks", Arrays.asList(64, 69, 96, 77, 143));
        config.save();

        estatesDir.mkdir();
        EstateHandler.reloadEstates();

        MinecraftForge.EVENT_BUS.register(new EstateListener());
        FMLCommonHandler.instance().bus().register(new EstateListener());
    }

}
