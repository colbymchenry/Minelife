package com.minelife.jobs.server.commands;

import com.minelife.jobs.EntityJobNPC;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandNPC extends CommandBase {

    @Override
    public String getCommandName() {
        return "npc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/job npc <INT:type>\n/job npc delete";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(args.length != 2) {
            player.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        if(!NumberConversions.isInt(args[1])) {
            if(args[1].equalsIgnoreCase("delete")) {
                PlayerHelper.TargetResult result = PlayerHelper.getTarget(player, 10);
                if(result.getEntity() == null) {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find NPC."));
                } else if (result.getEntity() instanceof EntityJobNPC){
                    player.worldObj.removeEntity(result.getEntity());
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Entity removed."));
                }
                return;
            }
            player.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityJobNPC jobNPC = new EntityJobNPC(player.worldObj, NumberConversions.toInt(args[1]));
        jobNPC.setPosition(player.posX, player.posY, player.posZ);
        player.worldObj.spawnEntityInWorld(jobNPC);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if(!(sender instanceof EntityPlayerMP)) return false;
        return ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "jobs.npc");
    }
}
