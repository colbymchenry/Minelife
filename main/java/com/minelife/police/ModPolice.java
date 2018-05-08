package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.PacketSendEMTStatus;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.network.PacketUnconscious;
import com.minelife.police.server.CommandCop;
import com.minelife.police.server.CommandPrison;
import com.minelife.police.server.ServerProxy;
import com.minelife.util.MLConfig;
import com.minelife.util.PacketPlaySound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.UUID;

public class ModPolice extends MLMod {

    public static ItemHandcuff itemHandcuff;
    public static ItemHandcuffKey itemHandcuffKey;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "cop"), EntityCop.class, "cop", 7, Minelife.getInstance(), 77, 1, true, 0xFFFFFF, 0x4286f4);
        registerPacket(PacketUnconscious.Handler.class, PacketUnconscious.class, Side.CLIENT);
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
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPrison());
        event.registerServerCommand(new CommandCop());
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
        List<String> emts = ServerProxy.config.contains("players") ? ServerProxy.config.getStringList("players") : Lists.newArrayList();
        if(value) {
            if(!emts.contains(player.getUniqueID().toString())) emts.add(player.getUniqueID().toString());
            Minelife.getNetwork().sendToAll(new PacketSendEMTStatus(player.getUniqueID(), true));
        } else {
            emts.remove(player.getUniqueID().toString());
            Minelife.getNetwork().sendToAll(new PacketSendEMTStatus(player.getUniqueID(), false));
        }
        ServerProxy.config.set("players", emts);
        ServerProxy.config.save();
    }

    public static boolean requestOfficer(EntityPlayer player) {
        EntityCop closest = null;
        for (Entity entity : player.getEntityWorld().loadedEntityList) {
            if(entity instanceof EntityCop) {
                if(((EntityCop) entity).getChasingPlayer() == null) {
                    EntityCop cop = (EntityCop) entity;
                    if(closest == null) closest = cop;
                    if(cop.getDistance(player) < closest.getDistance(player)) closest = cop;
                }
            }
        }

        if(closest != null) {
            closest.setAttackTarget(player);
            closest.setChasingPlayer(player);
            return true;
        }

        return false;
    }

}
