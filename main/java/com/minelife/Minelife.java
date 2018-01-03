package com.minelife;

import com.google.common.collect.Lists;
import com.minelife.drug.ModDrugs;
import com.minelife.economy.ModEconomy;
import com.minelife.gun.ModGun;
import com.minelife.minebay.ModMinebay;
import com.minelife.notification.ModNotifications;
import com.minelife.permission.ModPermission;
import com.minelife.police.ModPolice;
import com.minelife.realestate.ModRealEstate;
import com.minelife.shop.ModShop;
import com.minelife.tracker.ModTracker;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.PacketPopupMessage;
import com.minelife.util.client.PacketRequestName;
import com.minelife.util.client.PacketRequestUUID;
import com.minelife.util.server.PacketResponseName;
import com.minelife.util.server.PacketResponseUUID;
import com.minelife.util.server.PacketUpdatePlayerInventory;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mod(modid=Minelife.MOD_ID, name=Minelife.NAME, version=Minelife.VERSION, dependencies="required-after:BuildCraft|Transport;required-after:BuildCraft|Energy;required-after:BuildCraft|Core;required-after:BuildCraft|Factory;required-after:IC2;after:WorldEdit")
public class Minelife {

    private static Side side;

    public static final String MOD_ID = "minelife", VERSION = "2017.1", NAME = "Minelife";

    @SidedProxy(clientSide = "com.minelife.ClientProxy", serverSide = "com.minelife.ServerProxy")
    public static MLProxy PROXY;

    @Mod.Instance
    public static Minelife instance;

    public static SimpleNetworkWrapper NETWORK;

    public static Database SQLITE;

    public static final List<MLMod> MODS = Lists.newArrayList();

    public static String default_error_message = EnumChatFormatting.RED + "Sorry, something went wrong. Notify a staff member.";

    public Minelife() {
        MODS.add(new ModPermission());
        MODS.add(new ModTracker());
        MODS.add(new ModNotifications());
        MODS.add(new ModEconomy());
        MODS.add(new ModPolice());
        MODS.add(new ModGun());
        MODS.add(new ModDrugs());
        MODS.add(new ModRealEstate());
        MODS.add(new ModMinebay());
        MODS.add(new ModShop());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        side = event.getSide();
        NETWORK = new SimpleNetworkWrapper(MOD_ID);
        MLBlocks.init();
        MLItems.init();
        MinecraftForge.EVENT_BUS.register(this);
        MLMod.registerPacket(PacketPlaySound.Handler.class, PacketPlaySound.class, Side.CLIENT);
        MLMod.registerPacket(PacketUpdatePlayerInventory.Handler.class, PacketUpdatePlayerInventory.class, Side.CLIENT);
        MLMod.registerPacket(PacketPopupMessage.Handler.class, PacketPopupMessage.class, Side.CLIENT);
        MLMod.registerPacket(PacketRequestName.Handler.class, PacketRequestName.class, Side.SERVER);
        MLMod.registerPacket(PacketResponseName.Handler.class, PacketResponseName.class, Side.CLIENT);
        MLMod.registerPacket(PacketRequestUUID.Handler.class, PacketRequestUUID.class, Side.SERVER);
        MLMod.registerPacket(PacketResponseUUID.Handler.class, PacketResponseUUID.class, Side.CLIENT);
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

    public static void handle_exception(Exception e, ICommandSender player) {
        if(player instanceof EntityPlayer) {
            ((EntityPlayer) player).closeScreen();
        }
        if(e instanceof CustomMessageException) {
            player.addChatMessage(new ChatComponentText(e.getMessage()));
        } else {
            Minelife.log(e);
            player.addChatMessage(new ChatComponentText(Minelife.default_error_message));
        }
    }

    public static MLMod getModInstance(Class<? extends MLMod> modClass) {
        return MODS.stream().filter(m -> m.getClass().equals(modClass)).findFirst().orElse(null);
    }

    public static Side getSide() {
        return side;
    }

}
