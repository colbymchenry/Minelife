package com.minelife;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.drug.ModDrugs;
import com.minelife.economy.ModEconomy;
import com.minelife.gun.ModGun;
import com.minelife.notification.ModNotifications;
import com.minelife.permission.ModPermission;
import com.minelife.police.ModPolice;
import com.minelife.realestate.ModRealEstate;
import com.minelife.region.ModRegion;
import com.minelife.util.PacketPlaySound;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mod(modid=Minelife.MOD_ID, name=Minelife.NAME, version=Minelife.VERSION, dependencies="after:BuildCraft|Transport;after:BuildCraft|Energy;after:IC2;after:WorldEdit")
public class Minelife {

    public static final String MOD_ID = "minelife", VERSION = "2017.1", NAME = "Minelife";

    @SidedProxy(clientSide = "com.minelife.ClientProxy", serverSide = "com.minelife.ServerProxy")
    public static CommonProxy PROXY;

    @Mod.Instance
    public static Minelife instance;

    public static SimpleNetworkWrapper NETWORK;

    public static Database SQLITE;

    public static final List<AbstractMod> MODS = Lists.newArrayList();

    public static String default_error_message = EnumChatFormatting.RED + "Sorry, something went wrong. Notify a staff member.";

    public Minelife() {
        MODS.add(new ModPermission());
        MODS.add(new ModNotifications());
        MODS.add(new ModEconomy());
        MODS.add(new ModPolice());
        MODS.add(new ModGun());
        MODS.add(new ModRegion());
        MODS.add(new ModDrugs());
        MODS.add(new ModRealEstate());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = new SimpleNetworkWrapper(MOD_ID);
        MLBlocks.init();
        MLItems.init();
        MinecraftForge.EVENT_BUS.register(this);
        AbstractMod.registerPacket(PacketPlaySound.Handler.class, PacketPlaySound.class, Side.CLIENT);
        MODS.forEach(mod -> mod.preInit(event));
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MODS.forEach(mod -> mod.init(event));
        PROXY.init(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MinelifeGuiHandler());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        MODS.forEach(mod -> mod.serverStarting(event));
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event){
        MODS.forEach(mod -> mod.onLoadComplete(event));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        MODS.forEach(mod -> mod.postInit(event));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event) {
        MODS.forEach(mod -> mod.textureHook(event));
    }

    public static File getConfigDirectory() {
        return new File(System.getProperty("user.dir") + File.separator + "config", MOD_ID);
    }

    public static Logger getLogger() {
        return Logger.getLogger("Minecraft");
    }

    public static void log(Exception e) {
        e.printStackTrace();
        getLogger().log(Level.SEVERE, "", e);
    }

    public static void handle_exception(Exception e, EntityPlayer player) {
        if(e instanceof CustomMessageException) {
            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
        } else {
            Minelife.log(e);
            player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
        }
    }

}
