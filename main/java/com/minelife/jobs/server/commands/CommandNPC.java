package com.minelife.jobs.server.commands;

import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.EnumJob;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandNPC extends CommandBase {

    @Override
    public String getName() {
        return "npc";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/job npc <INT:type>\n/job npc delete";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(args.length != 2) {
            player.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if(!NumberConversions.isInt(args[1])) {
            if(args[1].equalsIgnoreCase("delete")) {
                PlayerHelper.TargetResult result = PlayerHelper.getTarget(player, 10);
                if(result.getEntity() == null) {
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Could not find NPC."));
                } else if (result.getEntity() instanceof EntityJobNPC){
                    player.getEntityWorld().removeEntity(result.getEntity());
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Entity removed."));
                }
                return;
            }
            player.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if(NumberConversions.toInt(args[1]) >= EnumJob.values().length || NumberConversions.toInt(args[1]) < 0) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Min: 0, Max: " + (EnumJob.values().length - 1)));
            return;
        }

        EntityJobNPC jobNPC = new EntityJobNPC(player.getEntityWorld(), NumberConversions.toInt(args[1]));
        jobNPC.setPosition(player.posX, player.posY, player.posZ);
        jobNPC.rotationYaw = player.rotationYaw;
        jobNPC.rotationPitch = player.rotationPitch;
        jobNPC.renderYawOffset = player.rotationYaw;
        jobNPC.prevRenderYawOffset = player.rotationYaw;
        player.getEntityWorld().spawnEntity(jobNPC);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if(!(sender instanceof EntityPlayerMP)) return false;
        return ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "jobs.npc");
    }
}