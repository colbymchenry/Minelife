package com.minelife.jobs.job.fisherman;

import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.job.lumberjack.LumberjackHandler;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.ItemHelper;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FishermanListener {

    @SubscribeEvent
    public void onFish(ItemFishedEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();

        if (player == null) return;

        if (!FishermanHandler.INSTANCE.isProfession(player)) return;

        if (event.isCanceled()) return;

        if(FishermanHandler.INSTANCE.doDoubleDrop(player)) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
            event.getDrops().forEach(stack -> stack.setCount(stack.getCount() * 2));
        }

        int level = FishermanHandler.INSTANCE.getLevel(player.getUniqueID());
        int xp = 0;
        for (ItemStack itemStack : event.getDrops()) {
            SellingOption s = FishermanHandler.INSTANCE.getSellingOptions().stream().filter(sellingOption -> ItemHelper.areStacksIdentical(itemStack, sellingOption.getStack())).findFirst().orElse(null);
            if(s != null) xp += s.getPrice();
        }

        FishermanHandler.INSTANCE.addXP(player.getUniqueID(), xp);
        CommandJob.sendMessage(player, EnumJob.FISHERMAN, "+" + xp);

        if (FishermanHandler.INSTANCE.getLevel(player) > level) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:level_up", 0.2F, 1), player);

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.WHITE.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.NAVY.asRGB(), Color.AQUA.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + LumberjackHandler.INSTANCE.getLevel(player), 5, player);
        }
    }

}
