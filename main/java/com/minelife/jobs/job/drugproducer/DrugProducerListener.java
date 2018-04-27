package com.minelife.jobs.job.drugproducer;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.farmer.FarmerHandler;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Map;
import java.util.UUID;

public class DrugProducerListener {

    static Map<UUID, Long> lastLevelUpSong = Maps.newHashMap();

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (!DrugProducerHandler.INSTANCE.isProfession(player)) return;

        if (event.crafting.getItem() != ModDrugs.itemJoint) return;

        if (DrugProducerHandler.INSTANCE.doDoubleJoint(player)) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
            event.player.dropItem(new ItemStack(ModDrugs.itemJoint), false).setPickupDelay(0);
        }

        int level = DrugProducerHandler.INSTANCE.getLevel(player);
        CommandJob.sendMessage(player, EnumJob.DRUG_PRODUCER, "+" + 100);
        DrugProducerHandler.INSTANCE.addXP(player.getUniqueID(), 100);

        if (DrugProducerHandler.INSTANCE.getLevel(player) > level
                && (!lastLevelUpSong.containsKey(player.getUniqueID()) || lastLevelUpSong.get(player.getUniqueID()) < System.currentTimeMillis())) {
            lastLevelUpSong.put(player.getUniqueID(), System.currentTimeMillis() + 10000L);
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:drug_level_up", 1, 1), player);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + DrugProducerHandler.INSTANCE.getLevel(player), 10, player);
        }
    }

}
