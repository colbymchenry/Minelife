package com.minelife.jobs.server;

import com.minelife.jobs.EntityJobNPC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandJob extends CommandBase {

    @Override
    public String getCommandName() {
        return "job";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(!(sender instanceof EntityPlayer)) return;

        EntityPlayerMP player = (EntityPlayerMP) sender;

        EntityJobNPC jobNPC = new EntityJobNPC(player.worldObj);
        jobNPC.setPosition(player.posX, player.posY, player.posZ);

        player.worldObj.spawnEntityInWorld(jobNPC);
    }
}
