package com.minelife.gangs.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.GangRole;
import com.minelife.gangs.network.PacketAddMember;
import com.minelife.gangs.network.PacketOpenGangGui;
import com.minelife.gangs.network.PacketRequestAlliance;
import com.minelife.gangs.network.PacketSendGangMembers;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.util.NumberConversions;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import scala.tools.nsc.doc.model.Object;

import javax.xml.soap.Text;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandGang extends CommandBase {

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
        sendMessage(sender, "/gang sethome|delhome|home");
        sendMessage(sender, "/gang setbank|delbank");
        sendMessage(sender, "/gang deposit <amount>");
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

        if (args.length == 0) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            Minelife.getNetwork().sendTo(new PacketOpenGangGui(playerGang), player);
            return;
        }

        Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 2) {
                getUsage(sender);
                return;
            }

            if (playerGang != null) {
                sendMessage(sender, TextFormatting.RED + "You already belong to a gang.");
                return;
            }

            if (args[1].length() > 20) {
                sendMessage(sender, TextFormatting.RED + "Name contains too many characters. Max 20.");
                return;
            }

            if (StringHelper.containsSpecialChar(args[1])) {
                sendMessage(sender, TextFormatting.RED + "Name cannot contain special characters.");
                return;
            }

            if (Gang.getGang(args[1]) != null) {
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
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "Owner cannot leave a gang. Please transfer ownership or disband the gang.");
                return;
            }

            Map<UUID, GangRole> members = playerGang.getMembers();
            members.remove(player.getUniqueID());
            playerGang.setMembers(members);
            playerGang.writeToDatabase();
            sendMessage(sender, "You have successfully left your gang.");
        } else if (args[0].equalsIgnoreCase("disband")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "You must be the owner to disband the gang.");
                return;
            }

            playerGang.disband();
            sendMessage(sender, "Gang disbanded.");
        } else if (args[0].equalsIgnoreCase("ally")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (args.length < 2) {
                getUsage(sender);
                return;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.MANAGE_ALLIANCES)) {
                sendMessage(player, TextFormatting.RED + "You are not authorized to manage alliances.");
                return;
            }

            if (!PacketRequestAlliance.ALLY_REQUESTS.containsKey(playerGang)) {
                sendMessage(player, TextFormatting.RED + "You do not have any pending alliances.");
                return;
            }

            if (args[1].equalsIgnoreCase("accept")) {
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

                if (PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()) != null) {
                    Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()));
                    ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Alliance Formed", TextFormatting.GOLD.toString() + "You formed an alliance with " + TextFormatting.RED + playerGang.getName() + TextFormatting.GOLD + "!", 12, PlayerHelper.getPlayer(PacketRequestAlliance.ALLY_REQUESTS.get(playerGang).getOwner()));
                }

                PacketRequestAlliance.ALLY_REQUESTS.remove(playerGang);
            } else if (args[1].equalsIgnoreCase("deny")) {
                PacketRequestAlliance.ALLY_REQUESTS.remove(playerGang);
            } else {
                getUsage(sender);
            }
        } else if (args[0].equalsIgnoreCase("sethome")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.SETHOME_DELHOME)) {
                sendMessage(sender, TextFormatting.RED + "You do not have permission to do that in your gang.");
                return;
            }

            if (estate == null) {
                sendMessage(sender, TextFormatting.RED + "The gang home must be set within an estate.");
                return;
            }

            if (!estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.BREAK)
                    && !estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.PLACE)) {
                sendMessage(sender, TextFormatting.RED + "You can only set the home in estates you are authorized to build in.");
                return;
            }

            playerGang.setHome(new Location(player.dimension, player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch));
            playerGang.writeToDatabase();
            sendMessage(sender, "Gang home set!");
        } else if (args[0].equalsIgnoreCase("delhome")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.SETHOME_DELHOME)) {
                sendMessage(sender, TextFormatting.RED + "You do not have permission to do that in your gang.");
                return;
            }

            playerGang.setHome(null);
            playerGang.writeToDatabase();
            sendMessage(sender, "Gang home deleted!");
        } else if (args[0].equalsIgnoreCase("home")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (playerGang.getHome() == null) {
                sendMessage(sender, TextFormatting.RED + "Gang home not set.");
                return;
            }

            Location home = playerGang.getHome();

            Estate estateAtHome = ModRealEstate.getEstateAt(home.getEntityWorld(), new BlockPos(home.getX(), home.getY(), home.getZ()));
            if (estateAtHome != null && !estateAtHome.getPlayerPermissions(playerGang.getOwner()).contains(PlayerPermission.BREAK)
                    && !estateAtHome.getPlayerPermissions(playerGang.getOwner()).contains(PlayerPermission.PLACE)) {
                sendMessage(sender, TextFormatting.RED + "The gang owner does not have build rights where the gang home is. Teleportation not permitted.");
                return;
            }

            TeleportHandler.teleport(player, home);
        } else if (args[0].equalsIgnoreCase("setbank")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "Only the gang owner can set the bank.");
                return;
            }

            if (estate == null) {
                sendMessage(sender, TextFormatting.RED + "The gang bank must be within an estate.");
                return;
            }

            if (!estate.getPlayerPermissions(playerGang.getOwner()).contains(PlayerPermission.BREAK)
                    && !estate.getPlayerPermissions(playerGang.getOwner()).contains(PlayerPermission.PLACE)) {
                sendMessage(sender, TextFormatting.RED + "You must have building rights in the estate your bank is set.");
                return;
            }

            playerGang.setBank(estate);
            playerGang.writeToDatabase();
            sendMessage(sender, "Gang bank set!");
        } else if (args[0].equalsIgnoreCase("delbank")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.getOwner().equals(player.getUniqueID())) {
                sendMessage(sender, TextFormatting.RED + "Only the gang owner can delete the bank.");
                return;
            }

            if (playerGang.getBank() == null) {
                sendMessage(sender, TextFormatting.RED + "Gang does not have a bank set.");
                return;
            }

            if (!Objects.equals(estate, playerGang.getBank())) {
                sendMessage(sender, TextFormatting.RED + "You must be within the bank estate to delete it.");
                return;
            }

            playerGang.setBank(null);
            playerGang.writeToDatabase();
            sendMessage(sender, "Gang bank deleted!");
        } else if (args[0].equalsIgnoreCase("deposit")) {
            if (playerGang == null) {
                sendMessage(sender, "You do not belong to a gang. Type " + TextFormatting.RED + "/gang create <name>" + TextFormatting.GOLD + " to create a gang.");
                return;
            }

            if (!playerGang.hasPermission(player.getUniqueID(), GangPermission.DEPOSIT_MONEY)) {
                sendMessage(sender, TextFormatting.RED + "You do not have permission to deposit money in the gang.");
                return;
            }

            if (playerGang.getBank() == null) {
                sendMessage(sender, TextFormatting.RED + "Your gang does not have a bank.");
                return;
            }

            if (!NumberConversions.isInt(args[1])) {
                sendMessage(sender, TextFormatting.RED + "Amount must be an integer.");
                return;
            }

            if (ModEconomy.getBalanceInventory(player) < NumberConversions.toInt(args[1])) {
                sendMessage(sender, TextFormatting.RED + "You do not have that much within your inventory.");
                return;
            }

            ModEconomy.withdrawInventory(player, NumberConversions.toInt(args[1]));
            int didNotFit = ModEconomy.depositEstate(playerGang.getBank(), NumberConversions.toInt(args[1]));
            if (didNotFit > 0)
                ModEconomy.depositATM(playerGang.getOwner(), didNotFit, true);

            Map<UUID, Long> rep = playerGang.getRep();
            long newRep = rep.containsKey(player.getUniqueID()) ? rep.get(player.getUniqueID()) + NumberConversions.toInt(args[1]) : NumberConversions.toInt(args[1]);
            rep.put(player.getUniqueID(), newRep);
            playerGang.setRep(rep);
            sendMessage(player, "+" + NumberConversions.toInt(args[1]) + " Rep");
            Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
        } else if (args[0].equalsIgnoreCase("accept")) {
            if (playerGang != null) {
                sendMessage(player, TextFormatting.RED + "You already belong to a gang.");
                return;
            }

            if (!PacketAddMember.GANG_INVITES.containsKey(player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You do not have any gang invites.");
                return;
            }

            Gang gang = PacketAddMember.GANG_INVITES.get(player.getUniqueID());
            Map<UUID, GangRole> members = gang.getMembers();
            members.put(player.getUniqueID(), GangRole.TEENIE);
            gang.setMembers(members);
            gang.writeToDatabase();
            PacketAddMember.GANG_INVITES.remove(player.getUniqueID());

            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:gang_created", 0.1F, 1F), player);
            ModEssentials.sendTitle(TextFormatting.RED.toString() + TextFormatting.BOLD.toString() + "Joined Gang", TextFormatting.GOLD.toString() + "You joined gang " + TextFormatting.RED + gang.getName() + TextFormatting.GOLD + "!", 12, player);

            gang.getMembers().keySet().forEach(playerID -> {
                if (PlayerHelper.getPlayer(playerID) != null) {
                    PacketSendGangMembers.sendMembers(gang, PlayerHelper.getPlayer(playerID));
                }
            });

            PacketSendGangMembers.sendMembers(gang, player);
        } else if (args[0].equalsIgnoreCase("deny")) {
            if (!PacketAddMember.GANG_INVITES.containsKey(player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You do not have any gang invites.");
                return;
            }

            PacketAddMember.GANG_INVITES.remove(player.getUniqueID());
        } else {
            getUsage(sender);
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
