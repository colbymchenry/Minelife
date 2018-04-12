package com.minelife;

import com.google.common.collect.Lists;
import com.minelife.cape.ModCapes;
import com.minelife.chestshop.ModChestShop;
import com.minelife.drugs.ModDrugs;
import com.minelife.economy.ModEconomy;
import com.minelife.essentials.ModEssentials;
import com.minelife.guns.ModGuns;
import com.minelife.jobs.ModJobs;
import com.minelife.minebay.ModMinebay;
import com.minelife.netty.ModNetty;
import com.minelife.notifications.ModNotifications;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.ModRealEstate;
import com.minelife.tracker.ModTracker;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.PacketPopup;
import com.minelife.util.client.PacketRequestName;
import com.minelife.util.client.PacketRequestUUID;
import com.minelife.util.server.PacketResponseName;
import com.minelife.util.server.PacketResponseUUID;
import com.minelife.welfare.ModWelfare;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;

@Mod(modid = Minelife.MOD_ID, name = Minelife.NAME, version = Minelife.VERSION, dependencies = "required-after:ic2;required-after:buildcraftcore;")
public class Minelife {

    public static final String MOD_ID = "minelife", VERSION = "3.0", NAME = "Minelife";

    @SidedProxy(clientSide = "com.minelife.ClientProxy", serverSide = "com.minelife.ServerProxy")
    private static MLProxy PROXY;

    @Mod.Instance
    private static Minelife INSTANCE;


    static {
        FluidRegistry.enableUniversalBucket();
    }


    private static SimpleNetworkWrapper NETWORK;
    private static List<MLMod> MODS = Lists.newArrayList();

    public Minelife() {
        MODS.add(new ModNetty());
        MODS.add(new ModPermission());
        MODS.add(new ModTracker());
        MODS.add(new ModWelfare());
        MODS.add(new ModNotifications());
        MODS.add(new ModEssentials());
        MODS.add(new ModEconomy());
        MODS.add(new ModChestShop());
        MODS.add(new ModRealEstate());
        MODS.add(new ModMinebay());
        MODS.add(new ModGuns());
        MODS.add(new ModDrugs());
        MODS.add(new ModCapes());
        MODS.add(new ModJobs());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = new SimpleNetworkWrapper(MOD_ID);

        MLMod.registerPacket(PacketPlaySound.Handler.class, PacketPlaySound.class, Side.CLIENT);
        MLMod.registerPacket(PacketRequestName.Handler.class, PacketRequestName.class, Side.SERVER);
        MLMod.registerPacket(PacketResponseName.Handler.class, PacketResponseName.class, Side.CLIENT);
        MLMod.registerPacket(PacketRequestUUID.Handler.class, PacketRequestUUID.class, Side.SERVER);
        MLMod.registerPacket(PacketResponseUUID.Handler.class, PacketResponseUUID.class, Side.CLIENT);
        MLMod.registerPacket(PacketPopup.Handler.class, PacketPopup.class, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(this);

        MODS.forEach(mod -> mod.preInit(event));
        try {
            PROXY.preInit(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MODS.forEach(mod -> mod.init(event));
        try {
            PROXY.init(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MinelifeGuiHandler());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MODS.forEach(mod -> mod.serverStarting(event));
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        MODS.forEach(mod -> mod.onLoadComplete(event));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MODS.forEach(mod -> mod.postInit(event));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event) {
        MODS.forEach(mod -> mod.textureHook(event));
    }

    @SubscribeEvent
    public void entityRegistration(final RegistryEvent.Register<EntityEntry> event) {
        MODS.forEach(mod -> mod.entityRegistration(event));
    }

    public static File getDirectory() {
        return new File(System.getProperty("user.dir") + File.separator + "config", MOD_ID);
    }

    public static MLProxy getProxy() {
        return PROXY;
    }

    public static Minelife getInstance() {
        return INSTANCE;
    }

    public static SimpleNetworkWrapper getNetwork() {
        return NETWORK;
    }

    public static List<MLMod> getModList() {
        return MODS;
    }
}
