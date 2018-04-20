package com.minelife.gangs.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.essentials.ModEssentials;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.GangRole;
import com.minelife.gangs.network.PacketOpenGangGui;
import com.minelife.gangs.network.PacketRequestAlliance;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandGang extends CommandBase  {

    @Override
    public String getName() {
        return "gang";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("g");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sendMessage(sender, "/gang - To open gang GUI");
        sendMessage(sender, "/gang create <name> - To create a gang");
        sendMessage(sender, "/gang leave - To leave your gang");
        sendMessage(sender, "/gang disband - To destroy your gang");
        sendMessage(sender, "/gang accept|deny");
        sendMessage(sender, "/gang ally accept|deny");
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
                sendMessage(sender, TextFormatting.RED + "Name contains too many characters. Max 20.");
                return;
            }

            if(StringHelper.containsSpecialChar(args[1])) {
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
        } else if(args[0].equalsIgnoreCase("ally")) {
            if(args.length < 2) {
                getUsage(sender);
                return;
            }

            if(!playerGang.hasPermission(player.getUniqueID(), GangPermission.MANAGE_ALLIANCES)) {
                sendMessage(player, TextFormatting.RED + "You are not authorized to manage alliances.");
                return;
            }

            if(!PacketRequestAlliance.ALLY_REQUESTS.containsKey(playerGang)) {
                sendMessage(player, TextFormatting.RED + "You do not have any pending alliances.");
                return;
            }

            if(args[1].equalsIgnoreCase("accept")) {
                Set<Gang> allies = playerGang.getAlliances();
                allies.add(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang));
                playerGang.setAlliances(allies);
                allies = PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getAlliances();
                allies.add(playerGang);
                PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).setAlliances(allies);
                playerGang.writeToDatabase();
                PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).writeToDatabase();

                Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), player);
                ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Alliance Formed", TextFormatting.GOLD.toString() + "You formed an alliance with " + TextFormatting.RED + PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getName() + TextFormatting.GOLD + "!", 12, player);

                if(PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()) != null) {
                    Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()));
                    ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Alliance Formed", TextFormatting.GOLD.toString() + "You formed an alliance with " + TextFormatting.RED + playerGang.getName() + TextFormatting.GOLD + "!", 12, PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()));
                }

                PacketRequestAlliance.ALLY_REQUESTS.remove(playerGang);
            } else if(args[1].equalsIgnoreCase("deny")) {
                PacketRequestAlliance.ALLY_REQUESTS.remove(playerGang);
            } else {
                getUsage(sender);
            }
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
