package com.minelife.emt;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.police.ModPolice;
import com.minelife.util.MLConfig;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;
import java.util.UUID;

public class ServerProxy extends MLProxy {

    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        config = new MLConfig("emt");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHeal(PlayerInteractEvent event) {
        if(!ModEMT.isEMT(event.getEntityPlayer().getUniqueID())) return;

        PlayerHelper.TargetResult targetResult = PlayerHelper.getTarget(event.getEntityPlayer(), 5);
        Entity clickedEntity = targetResult.getEntity();

        if (!(clickedEntity instanceof EntityPlayer)) return;

        EntityPlayer playerClicked = (EntityPlayer) clickedEntity;

        if (!ModPolice.isUnconscious(playerClicked)) return;

        if (ModEMT.PLAYERS_BEING_HEALED.containsKey(playerClicked.getUniqueID())) {
            event.getEntityPlayer().sendMessage(new TextComponentString("That player is already being revived."));
            return;
        }

        ModEMT.PLAYERS_BEING_HEALED.put(playerClicked.getUniqueID(), System.currentTimeMillis() + (1000L * 21));
        Minelife.getNetwork().sendTo(new PacketReviving(), (EntityPlayerMP) event.getEntityPlayer());
        Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:emt_revive", 1, 1), new NetworkRegistry.TargetPoint(event.getEntityPlayer().dimension, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 10));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        List<UUID> toRemove = Lists.newArrayList();
        ModEMT.PLAYERS_BEING_HEALED.forEach((playerID, reviveTime) -> {
            if (System.currentTimeMillis() >= reviveTime) {
                toRemove.add(playerID);
                if (PlayerHelper.getPlayer(playerID) != null)
                    ModPolice.setUnconscious(PlayerHelper.getPlayer(playerID), false);
            }
        });

        toRemove.forEach(playerID -> ModEMT.PLAYERS_BEING_HEALED.remove(playerID));
    }
}
