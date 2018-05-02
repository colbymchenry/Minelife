package com.minelife.cape.server;

import com.google.common.collect.Maps;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.network.PacketUpdateCape;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Map;
import java.util.UUID;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        boolean on = event.player.getEntityData().hasKey("Cape") ? event.player.getEntityData().getBoolean("Cape") : false;
        Minelife.getNetwork().sendToAll(new PacketUpdateCapeStatus(event.player.getEntityId(), on));
        Minelife.getNetwork().sendToAll(new PacketUpdateCape(event.player.getUniqueID(), event.player.getEntityId(), ModCapes.itemCape.getPixels(event.player)));
    }

    private static Map<UUID, String> PIXELS_DEATHS = Maps.newHashMap();

    @SubscribeEvent
    public void deathEvent(LivingDeathEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if(ModCapes.itemCape.getPixels(player) != null) PIXELS_DEATHS.put(player.getUniqueID(), ModCapes.itemCape.getPixels(player));
        }
    }

    @SubscribeEvent
    public void spawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if(PIXELS_DEATHS.containsKey(event.player.getUniqueID())) {
            CommandCape.setCape((EntityPlayerMP) event.player, PIXELS_DEATHS.get(event.player.getUniqueID()));
            Minelife.getNetwork().sendToAll(new PacketUpdateCape(event.player.getUniqueID(), event.player.getEntityId(), PIXELS_DEATHS.get(event.player.getUniqueID())));
            Minelife.getNetwork().sendToAll(new PacketUpdateCapeStatus(event.player.getEntityId(), true));
            PIXELS_DEATHS.remove(event.player.getUniqueID());
        }
    }

}
