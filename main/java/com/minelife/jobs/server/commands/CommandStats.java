package com.minelife.jobs.server.commands;

import com.minelife.jobs.EnumJob;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;

public class CommandStats extends CommandBase {

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/job stats";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) sender;

        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "------[" + TextFormatting.RED + "Stats" + TextFormatting.GOLD + "]------"));
        for (EnumJob enumJob : EnumJob.values()) {
            if (enumJob.getHandler() != null && enumJob.getHandler().isProfession(player)) {
                int lvl = enumJob.getHandler().getLevel(player);
                double barsToFill = ((enumJob.getHandler().getXP(player.getUniqueID()) - enumJob.getHandler().getXpNeeded(lvl)) / (enumJob.getHandler().getXpNeeded(lvl + 1) - enumJob.getHandler().getXpNeeded(lvl))) * 100;
                String progress = "&7[";
                for (int i = 1; i <= 100; i++) progress += i <= barsToFill ? "&a|" : "&c|";
                progress += "&7]";

                player.sendMessage(new TextComponentString(
                        StringHelper.ParseFormatting("&6" + WordUtils.capitalizeFully(enumJob.name().replace("_", "")), '&')));
                player.sendMessage(new TextComponentString("    " + StringHelper.ParseFormatting(progress, '&')));
                player.sendMessage(new TextComponentString(
                        StringHelper.ParseFormatting("    &6Level: &c" + enumJob.getHandler().getLevel(player), '&')));
                player.sendMessage(new TextComponentString(
                        StringHelper.ParseFormatting("    &6XP Needed: &c" + NumberConversions.format((long) enumJob.getHandler().getXpNeeded(enumJob.getHandler().getLevel(player) + 1) - enumJob.getHandler().getXP(player.getUniqueID())), '&')));
            }
        }
    }

}
