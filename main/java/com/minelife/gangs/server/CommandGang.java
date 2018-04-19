package com.minelife.gangs.server;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.essentials.ModEssentials;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangRole;
import com.minelife.gangs.network.PacketOpenGangGui;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandGang extends CommandBase  {

    @Override
    public String getName() {
        return "gang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sendMessage(sender, "/gang - To open gang GUI");
        sendMessage(sender, "/gang create <name> - To create a gang");
        sendMessage(sender, "/gang leave - To leave your gang");
        sendMessage(sender, "/gang disband - To destroy your gang");
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

        if(args.length == 0) {
            if(playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            Minelife.getNetwork().sendTo(new PacketOpenGangGui(playerGang), player);
            return;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(args.length != 2) {
                getUsage(sender);
                return;
            }

            if(playerGang != null) {
                sendMessage(sender, TextFormatting.RED + "You already belong to a gang.");
                return;
            }

            if(args[1].length() > 20) {
                sendMessage(sender, TextFormatting.RED + "Name contains too many characters.");
                return;
            }

            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(args[1]);

            if(m.find()) {
                sendMessage(sender, TextFormatting.RED + "Name cannot contain special characters.");
                return;
            }

            if(Gang.getGang(args[1]) != null) {
                sendMessage(sender, TextFormatting.RED + "A gang with that name already exists.");
                return;
            }

            Gang.createGang(args[1], player.getUniqueID());
            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.GREEN.asRGB(), Color.YELLOW.asRGB()}, new int[]{Color.BLUE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), player);
            ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Gang Created", TextFormatting.GOLD.toString() + "Type " + TextFormatting.RED + "/g help" + TextFormatting.GOLD + " for more commands", 12, player);
        } else if (args[0].equalsIgnoreCase("leave")) {
            if(playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if(playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "Owner cannot leave a gang. Please transfer ownership or disband the gang.");
                return;
            }

            Map<UUID, GangRole> members = playerGang.getMembers();
            members.remove(player.getUniqueID());
            playerGang.setMembers(members);
            playerGang.writeToDatabase();
            sendMessage(sender, "You have successfully left your gang.");
        } else if(args[0].equalsIgnoreCase("disband")) {
            if(playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if(!playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "You must be the owner to disband the gang.");
                return;
            }

            playerGang.disband();
            sendMessage(sender, "Gang disbanded.");
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    public static void sendMessage(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + "[Gangs] " + TextFormatting.GOLD + msg));
    }

}
