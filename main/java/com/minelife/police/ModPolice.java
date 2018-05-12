package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.drugs.XRayEffect;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.cop.EntityCop;
import com.minelife.police.network.PacketCheckUnconscious;
import com.minelife.police.network.PacketWriteup;
import com.minelife.police.network.PacketOpenWriteupGUI;
import com.minelife.police.network.PacketUnconscious;
import com.minelife.police.server.*;
import com.minelife.util.MLConfig;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.UUID;

public class ModPolice extends MLMod {

    public static ItemHandcuff itemHandcuff;
    public static ItemHandcuffKey itemHandcuffKey;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ForgeRegistries.POTIONS.register(ArrestedEffect.INSTANCE);
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "cop"), EntityCop.class, "cop", 7, Minelife.getInstance(), 77, 1, true, 0xFFFFFF, 0x4286f4);
        registerPacket(PacketUnconscious.Handler.class, PacketUnconscious.class, Side.CLIENT);
        registerPacket(PacketSendCopStatus.Handler.class, PacketSendCopStatus.class, Side.CLIENT);
        registerPacket(PacketRequestCopStatus.Handler.class, PacketRequestCopStatus.class, Side.SERVER);
        registerPacket(PacketOpenSignupGui.Handler.class, PacketOpenSignupGui.class, Side.CLIENT);
        registerPacket(PacketWriteup.Handler.class, PacketWriteup.class, Side.SERVER);
        registerPacket(PacketOpenWriteupGUI.Handler.class, PacketOpenWriteupGUI.class, Side.CLIENT);
        registerPacket(PacketCheckUnconscious.Handler.class, PacketCheckUnconscious.class, Side.SERVER);
        registerItem(itemHandcuff = new ItemHandcuff());
        registerItem(itemHandcuffKey = new ItemHandcuffKey());
        itemHandcuff.registerRecipe();
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler getGuiHandler() {
        return new GuiHandler();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPrison());
        event.registerServerCommand(new CommandCop());
        event.registerServerCommand(new CommandRespawn());
        event.registerServerCommand(new CommandJail());
        event.registerServerCommand(new CommandWriteup());
        event.registerServerCommand(new CommandDrop());
    }

    public static Database getDatabase() {
        return ServerProxy.database;
    }

    public static MLConfig getConfig() {
        return ServerProxy.config;
    }

    public static void setUnconscious(EntityPlayer entity, boolean value, boolean playSound) {
        Minelife.getNetwork().sendToAll(new PacketUnconscious(entity.getEntityId(), value));

        if(value) {
            entity.getEntityData().setLong("UnconsciousTime", System.currentTimeMillis() + (60000L * 3));
            ItemHandcuff.setHandcuffed(entity, true, playSound);
        } else {
            entity.getEntityData().removeTag("UnconsciousTime");
            entity.getEntityData().removeTag("Tazed");
            ItemHandcuff.setHandcuffed(entity, false, false);
        }
    }

    public static boolean isUnconscious(EntityPlayer player) {
        return player.getEntityData().hasKey("UnconsciousTime") && player.getEntityData().getLong("UnconsciousTime") > System.currentTimeMillis();
    }

    public static boolean isCop(UUID playerID) {
        return ServerProxy.config.getStringList("players") != null ? ServerProxy.config.getStringList("players").contains(playerID.toString()): null;
    }

    public static boolean isCopClientCheck(UUID playerID) {
        return ClientProxy.POLICE_SET.contains(playerID);
    }

    public static void setCop(EntityPlayer player, boolean value) {
        List<String> cops = ServerProxy.config.contains("players") ? ServerProxy.config.getStringList("players") : Lists.newArrayList();
        if(value) {
            if(!cops.contains(player.getUniqueID().toString())) cops.add(player.getUniqueID().toString());
            Minelife.getNetwork().sendToAll(new PacketSendCopStatus(player.getUniqueID(), true));
        } else {
            cops.remove(player.getUniqueID().toString());
            Minelife.getNetwork().sendToAll(new PacketSendCopStatus(player.getUniqueID(), false));
        }
        ServerProxy.config.set("players", cops);
        ServerProxy.config.save();
    }

    public static boolean requestOfficer(EntityPlayer player) {
//        EntityCop closest = null;
//        for (Entity entity : player.getEntityWorld().loadedEntityList) {
//            if(entity instanceof EntityCop) {
//                if(((EntityCop) entity).getChasingPlayer() == null) {
//                    EntityCop cop = (EntityCop) entity;
//                    if(closest == null) closest = cop;
//                    if(cop.getDistance(player) < closest.getDistance(player)) closest = cop;
//                }
//            }
//        }
//
//        if(closest != null) {
//            closest.setAttackTarget(player);
//            closest.setChasingPlayer(player);
//            return true;
//        }

        return false;
    }

}
