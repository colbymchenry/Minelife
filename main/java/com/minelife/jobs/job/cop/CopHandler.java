package com.minelife.jobs.job.cop;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.essentials.ModEssentials;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.police.ModPolice;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class CopHandler extends NPCHandler {

    public static final CopHandler INSTANCE = new CopHandler();

    private CopHandler() {
        super("cop");
    }

    // TODO: Finish player becoming a cop

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if (player.world.isRemote) return;

        if (!ModEMT.isEMT(player.getUniqueID())) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.COP), (EntityPlayerMP) player);
        } else {
            ModEMT.setEMT(player, false);
            player.sendMessage(new TextComponentString("You are no longer a cop!"));
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if (ModPolice.isCop(player.getUniqueID())) {
            CommandJob.sendMessage(player, EnumJob.FARMER, TextFormatting.RED + "You are already a cop.");
            return;
        }

        ModPolice.setCop(player, true);

        ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                new int[]{Color.BLUE.asRGB(), Color.NAVY.asRGB()}, new int[]{Color.WHITE.asRGB(), Color.AQUA.asRGB()}).getStack(1);

        EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
        player.getEntityWorld().spawnEntity(ent);
        EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
        player.getEntityWorld().spawnEntity(ent1);

        ModEssentials.sendTitle(null ,TextFormatting.RED + "Right click an unconscious player to revive them!", 6, (EntityPlayerMP) player);
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        return Lists.newArrayList();
    }

    @Override
    public void setupConfig() {

    }

}
