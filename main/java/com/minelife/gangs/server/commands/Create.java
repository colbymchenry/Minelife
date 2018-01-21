package com.minelife.gangs.server.commands;

import com.minelife.util.Color;
import com.minelife.util.FireworkBuilder;
import com.minelife.essentials.ModEssentials;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.ICommandHandler;
import com.minelife.util.SoundTrack;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Create implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("/g create <name>"));
            return;
        }

        String name = args[1];

        if (ModGangs.getGang(name) != null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "There is already a gang with that name."));
            return;
        }

        if (ModGangs.getPlayerGang(player.getUniqueID()) != null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are already in a gang."));
            return;
        }

        try {
            Gang g = new Gang(name, player.getUniqueID());
            ModGangs.cache_gangs.add(g);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Gang created!"));

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.GREEN.asRGB(), Color.YELLOW.asRGB()}, new int[]{Color.BLUE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.worldObj, player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.worldObj.spawnEntityInWorld(ent);

            SoundTrack soundTrack = new SoundTrack();

            soundTrack.addPart("minecraft:note.pling", 0L, 1, 1);
            soundTrack.addPart("minecraft:note.pling", 200L, 1, 1.2f);
            soundTrack.addPart("minecraft:note.pling", 200L, 1, 1.4f);
            soundTrack.addPart("minecraft:note.pling", 200L, 1, 1.6f);
            soundTrack.addPart("minecraft:note.pling", 200L, 1, 1.8f);
            soundTrack.addPart("minecraft:note.pling", 600L, 1, 2f);
            soundTrack.addPart("minecraft:note.pling", 100L, 1.5f, 2.5f);
            soundTrack.addPart("minecraft:note.pling", 100L, 1.5f, 2.5f);
            soundTrack.addPart("minecraft:note.bass", 200L, 2, 0.5f);
            soundTrack.addPart("minecraft:note.bass", 200L, 2, 0.2f);

            soundTrack.play(player);

            ModEssentials.sendTitle(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD.toString() + "Gang Created",
                    EnumChatFormatting.BLUE + "Type " + EnumChatFormatting.YELLOW + "/g help" + EnumChatFormatting.BLUE + " for more commands", 5, (EntityPlayerMP) player);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }

}
