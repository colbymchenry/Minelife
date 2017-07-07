package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.SelectionController;
import com.minelife.realestate.Zone;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.util.List;
import java.util.logging.Level;

public class CommandClaim implements ICommand {

    @Override
    public String getCommandName()
    {
        return "claim";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "";
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        try {
            Vec3 pos1 = SelectionController.ServerSelector.pos1Map.get(((EntityPlayerMP) sender).getUniqueID());
            Vec3 pos2 = SelectionController.ServerSelector.pos2Map.get(((EntityPlayerMP) sender).getUniqueID());

            if(pos1 == null || pos2 == null) throw new CustomMessageException("Incomplete selection.");

            Region bounds = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));
            long total = (bounds.getArea() * Integer.parseInt("" + ModRealEstate.config.getOptions().get("PricePerBlock")));

            if(ModEconomy.getBalance(((EntityPlayerMP) sender).getUniqueID(), true) < total) throw new CustomMessageException("Insufficient funds.");

            Zone.createZone(sender.getEntityWorld(), pos1, pos2, ((EntityPlayerMP) sender).getUniqueID());

            ModEconomy.withdraw(((EntityPlayerMP) sender).getUniqueID(), total, true);

            ((EntityPlayerMP) sender).addChatComponentMessage(new ChatComponentText("$" + total + " withdrawn from your account."));
            ((EntityPlayerMP) sender).addChatComponentMessage(new ChatComponentText("Land claimed!"));
        }catch(Exception e) {
            if(e instanceof CustomMessageException) {
                sender.addChatMessage(new ChatComponentText(e.getMessage()));
            } else {
                e.printStackTrace();
                Minelife.getLogger().log(Level.SEVERE, "", e);
                sender.addChatMessage(new ChatComponentText(Minelife.default_error_message));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return Lists.newArrayList();
    }

    // TODO
    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
}
