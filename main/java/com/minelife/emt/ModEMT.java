package com.minelife.emt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.util.MLConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModEMT extends MLMod {

    public static Map<UUID, Long> PLAYERS_BEING_HEALED = Maps.newHashMap();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "emt"), EntityEMT.class, "emt", 10, Minelife.getInstance(), 77, 1, true, 0xFFFFFF, 0x4286f4);
        registerPacket(PacketReviving.Handler.class, PacketReviving.class, Side.CLIENT);
        registerPacket(PacketSendEMTStatus.Handler.class, PacketSendEMTStatus.class, Side.CLIENT);
        registerPacket(PacketRequestEMTStatus.Handler.class, PacketRequestEMTStatus.class, Side.SERVER);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandEMT());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.emt.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.emt.ServerProxy.class;
    }

    public static MLConfig getConfig() {
        return ServerProxy.config;
    }

    // TODO: Test spawns of EMT and Cops

    public static boolean isEMT(UUID playerID) {
        return playerID == null ? false : ServerProxy.config.getStringList("players") != null ? ServerProxy.config.getStringList("players").contains(playerID.toString()): null;
    }

    public static boolean isEMTClientCheck(UUID playerID) {
        return ClientProxy.EMT_SET.contains(playerID);
    }

    public static void setEMT(EntityPlayer player, boolean value) {
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

    public static boolean requestEMT(EntityPlayer player) {
        EntityEMT closest = null;
        for (Entity entity : player.getEntityWorld().loadedEntityList) {
            if(entity instanceof EntityEMT) {
                if(((EntityEMT) entity).getAttackTarget() == null) {
                    EntityEMT emt = (EntityEMT) entity;
                    if(closest == null) closest = emt;
                    if(emt.getDistance(player) < closest.getDistance(player)) closest = emt;
                }
            }
        }

        if(closest != null) {
            closest.setAttackTarget(player);
            closest.setRevivingPlayer(player);
            return true;
        }

        return false;
    }

}
