package com.minelife;

import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.gun.ModGun;
import com.minelife.police.ModPolice;
import com.minelife.region.ModRegion;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import lib.PatPeter.SQLibrary.Database;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

@Mod(modid=Minelife.MOD_ID, name=Minelife.NAME, version=Minelife.VERSION, dependencies="after:BuildCraft|Transport;after:IC2;after:WorldEdit")
public class Minelife {

    public static final String MOD_ID = "minelife", VERSION = "2017.1", NAME = "Minelife";

    @SidedProxy(clientSide = "com.minelife.ClientProxy", serverSide = "com.minelife.ServerProxy")
    public static CommonProxy PROXY;

    public static SimpleNetworkWrapper NETWORK;

    public static Database SQLITE;

    public static final List<SubMod> MODS = Lists.newArrayList();

    public Minelife() {
        MODS.add(new ModEconomy());
        MODS.add(new ModPolice());
        MODS.add(new ModGun());
        MODS.add(new ModRegion());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = new SimpleNetworkWrapper(MOD_ID);

        SubMod.registerPacket(PacketPlaySound.Handler.class, PacketPlaySound.class, Side.CLIENT);
        MODS.stream().forEach(mod -> mod.preInit(event));
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MODS.stream().forEach(mod -> mod.init(event));
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MODS.stream().forEach(mod -> mod.serverStarting(event));
    }

    public static final File getDirectory() {
        return new File(System.getProperty("user.dir"), MOD_ID);
    }

    public static final Logger getLogger() {
        return Logger.getLogger("Minecraft");
    }

}
