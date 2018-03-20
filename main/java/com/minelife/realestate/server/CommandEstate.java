package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.network.PacketCreateGui;
import com.minelife.realestate.network.PacketModifyGui;
import com.minelife.util.client.PacketPopup;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CommandEstate extends CommandBase {

    @Override
    public String getName() {
        return "estate";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("e");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e create"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e delete"));
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;

        if (args.length != 1) {
            getUsage(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        Estate estateAtPos = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        Set<PlayerPermission> basePermissions = getPlayerPermissions(player.getUniqueID());
        Set<EstateProperty> baseProperties = getEstateProperties(player.getUniqueID());

        if (args[0].equalsIgnoreCase("create")) {
            if (creationCheck(player, true, false))
                Minelife.getNetwork().sendTo(new PacketCreateGui(basePermissions, baseProperties), player);
        } else if (args[0].equalsIgnoreCase("delete")) {
            if(estateAtPos == null) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "There is no estate at your location."));
                return;
            }

            if(!Objects.equals(estateAtPos.getOwnerID(),player.getUniqueID())) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "You are not the owner of this estate."));
                return;
            }

            try {
                estateAtPos.delete();
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.GOLD + "Estate deleted."));
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "An error occurred while attempting to delete the estate."));
            }
        } else if(args[0].equalsIgnoreCase("modify")) {
            if(estateAtPos == null) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "There is no estate at your location."));
                return;
            }

            if(!Objects.equals(estateAtPos.getOwnerID(), player.getUniqueID()) && !Objects.equals(estateAtPos.getRenterID(), player.getUniqueID())) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "You are not authorized to modify this estate."));
                return;
            }

            Minelife.getNetwork().sendTo(new PacketModifyGui(estateAtPos, basePermissions, baseProperties), player);
        }
    }

    public static Set<PlayerPermission> getPlayerPermissions(UUID playerID) {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        for (PlayerPermission playerPermission : PlayerPermission.values()) {
            if (ModPermission.hasPermission(playerID, "estate." + playerPermission.name().toLowerCase())) {
                permissions.add(playerPermission);
            }
        }
        return permissions;
    }

    public static Set<EstateProperty> getEstateProperties(UUID playerID) {
        Set<EstateProperty> properties = Sets.newTreeSet();
        for (EstateProperty estateProperty : EstateProperty.values()) {
            if (ModPermission.hasPermission(playerID, "estate." + estateProperty.name().toLowerCase())) {
                properties.add(estateProperty);
            }
        }
        return properties;
    }

    // TODO: need to make sure can't create estate that encapsulates an estate they do not own
    public static boolean creationCheck(EntityPlayerMP player, boolean sendMessages, boolean sendPopups) {
        if (!SelectionListener.hasFullSelection(player)) {
            if (sendMessages)
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Please make a full selection first with a golden hoe."));
            else if (sendPopups)
                PacketPopup.sendPopup("Invalid selection.", player);
            return false;
        }

        BlockPos min = SelectionListener.getMinimum(player), max = SelectionListener.getMaximum(player);
        int width = Math.abs(max.getX() - min.getX());
        int length = Math.abs(max.getZ() - min.getZ());
        int height = Math.abs(max.getY() - min.getY());

        if ((width * length * height) < 125) {
            if (sendMessages)
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Selection is not big enough. Must be at least 5x5x5."));
            else if (sendPopups)
                PacketPopup.sendPopup("Selection is not big enough. Must be at least 5x5x5.", player);
            return false;
        }

        Estate parentEstate = null;

        for (Estate estate : ModRealEstate.getLoadedEstates()) {
            if (estate.getWorld().equals(player.getEntityWorld()) && !estate.isInside(min, max) && estate.intersects(min, max)) {
                if (sendMessages)
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Selection is intersecting another estate."));
                else if (sendPopups)
                    PacketPopup.sendPopup("Selection is intersecting another estate.", player);
                return false;
            }
            if (estate.getWorld().provider.getDimension() == player.getEntityWorld().provider.getDimension() &&
                    estate.contains(min) && estate.contains(max)) {
                parentEstate = estate;
                break;
            }
        }

        if (parentEstate != null) {
            if (!parentEstate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.CREATE_ESTATES)) {
                if (sendMessages)
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " You do not have permission to create estates here."));
                else if (sendPopups)
                    PacketPopup.sendPopup("You do not have permission to create estates here.", player);
                return false;
            }
        }

        return true;
    }

}
