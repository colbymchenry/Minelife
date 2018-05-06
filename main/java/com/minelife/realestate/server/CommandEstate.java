package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.*;
import com.minelife.realestate.network.PacketBuyGui;
import com.minelife.realestate.network.PacketCreateGui;
import com.minelife.realestate.network.PacketModifyGui;
import com.minelife.realestate.network.PacketSelection;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
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
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e modify"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e buy"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e rent"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e receptionist <name> <skinID>"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e receptionist delete"));
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Estate]" + TextFormatting.GOLD + " /e identifier <identifier>"));
        return null;
    }

    // TODO: Add way to view estate land

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) return;

        if (args.length < 1) {
            getUsage(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        Estate estateAtPos = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        Set<PlayerPermission> basePermissions = getPlayerPermissions(player.getUniqueID());
        Set<EstateProperty> baseProperties = getEstateProperties(player.getUniqueID());

        if (args[0].equalsIgnoreCase("create")) {
            if (creationCheck(player, true, false)) {
                BlockPos min = SelectionListener.getMinimum(player), max = SelectionListener.getMaximum(player);
                long width = Math.abs(max.getX() - min.getX());
                long length = Math.abs(max.getZ() - min.getZ());
                long height = Math.abs(max.getY() - min.getY());
                long area = width * length * height;
                long price = area * ModRealEstate.getConfig().getInt("price_per_block", 2);

                if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.price") && ModEconomy.getBalanceInventory(player) < price) {
                    sendMessage(player, TextFormatting.RED + "Insufficient funds! Price: " + TextFormatting.DARK_RED + "$" + NumberConversions.format(price));
                    return;
                }

                Minelife.getNetwork().sendTo(new PacketCreateGui(basePermissions, baseProperties), player);
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (estateAtPos == null) {
                sendMessage(player, TextFormatting.RED + "There is no estate at your location.");
                return;
            }

            if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.delete") && !Objects.equals(estateAtPos.getOwnerID(), player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You are not the owner of this estate.");
                return;
            }

            try {
                estateAtPos.delete();
                sendMessage(player, TextFormatting.GOLD + "Estate deleted.");
            } catch (SQLException e) {
                e.printStackTrace();
                sendMessage(player, TextFormatting.RED + "An error occurred while attempting to delete the estate.");
            }
        } else if (args[0].equalsIgnoreCase("modify")) {
            if (estateAtPos == null) {
                sendMessage(player, TextFormatting.RED + "There is no estate at your location.");
                return;
            }

            if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.modify") && !Objects.equals(estateAtPos.getOwnerID(), player.getUniqueID()) && !Objects.equals(estateAtPos.getRenterID(), player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You are not authorized to modify this estate.");
                return;
            }

            Minelife.getNetwork().sendTo(new PacketModifyGui(estateAtPos, basePermissions, baseProperties), player);
        } else if (args[0].equalsIgnoreCase("rent") || args[0].equalsIgnoreCase("buy")) {
            if (estateAtPos == null) {
                sendMessage(player, TextFormatting.RED + "There is no estate at your location.");
                return;
            }

            if (estateAtPos.getRenterID() != null) {
                sendMessage(player, TextFormatting.RED + "This estate has someone renting it.");
                return;
            }

            if (Objects.equals(estateAtPos.getOwnerID(), player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You cannot buy from yourself.");
                return;
            }

            if (args[0].equalsIgnoreCase("rent")) {
                if (estateAtPos.getRentPrice() <= 0 || estateAtPos.getRentPeriod() <= 0) {
                    sendMessage(player, TextFormatting.RED + "Estate is not for rent.");
                    return;
                }
            } else {
                if (estateAtPos.getPurchasePrice() <= 0) {
                    sendMessage(player, TextFormatting.RED + "Estate is not for purchase.");
                    return;
                }
            }

            Minelife.getNetwork().sendTo(new PacketBuyGui(estateAtPos), player);
        } else if (args[0].equalsIgnoreCase("receptionist")) {
            Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

            // delete a receptionist
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("delete")) {
                    if (estate != null && !Objects.equals(estate.getOwnerID(), player.getUniqueID())
                            && !Objects.equals(estate.getRenterID(), player.getUniqueID())) {
                        sendMessage(player, TextFormatting.RED + "You are not authorized to do that here.");
                        return;
                    }

                    PlayerHelper.TargetResult result = PlayerHelper.getTarget(player, 10);
                    if (result.getEntity() == null) {
                        sendMessage(player, TextFormatting.RED + "Could not find receptionist.");
                    } else if (result.getEntity() instanceof EntityReceptionist) {
                        player.getEntityWorld().removeEntity(result.getEntity());
                        sendMessage(player, "Receptionist removed.");
                    } else {
                        sendMessage(player, TextFormatting.RED + "Could not find receptionist.");
                    }
                } else {
                    getUsage(sender);
                }
                return;
            }

            // continue on past deletion
            if (args.length != 3) {
                getUsage(sender);
                return;
            }

            if (estate == null) {
                sendMessage(player, TextFormatting.RED + "Estate not found at your location.");
                return;
            }

            if (!Objects.equals(estate.getOwnerID(), player.getUniqueID())
                    && !Objects.equals(estate.getRenterID(), player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "You are not authorized to do that here.");
                return;
            }

            if (!NumberConversions.isInt(args[2])) {
                sendMessage(player, TextFormatting.RED + "Skin ID must be an integer.");
                return;
            }

            String name = args[1];
            int skinID = NumberConversions.toInt(args[2]);

            if (skinID > 2) {
                sendMessage(player, TextFormatting.RED + "Skin ID must range from 0-2");
                return;
            }

            BlockPos min = estate.getMinimum(), max = estate.getMaximum();
            List<EntityReceptionist> receptionists = player.getEntityWorld().getEntitiesWithinAABB(EntityReceptionist.class, new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));

            if (receptionists.size() >= 2) {
                sendMessage(player, TextFormatting.RED + "You are only allowed 2 receptions per estate.");
                return;
            }

            if (estate.getContainingEstates().size() < 4) {
                sendMessage(player, TextFormatting.RED + "You must have more than 3 estates within the estate the receptions will be in.");
                return;
            }


            EntityReceptionist receptionist = new EntityReceptionist(player.getEntityWorld(), name, skinID);
            receptionist.setPosition(player.posX, player.posY, player.posZ);
            player.getEntityWorld().spawnEntity(receptionist);
        } else if (args[0].equalsIgnoreCase("identifier")) {
            Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());
            if (args.length < 2) {
                getUsage(sender);
                return;
            }

            if (estate == null) {
                sendMessage(player, TextFormatting.RED + "Estate not found at your location.");
                return;
            }

            if (!Objects.equals(estate.getOwnerID(), player.getUniqueID())) {
                sendMessage(player, TextFormatting.RED + "Only the owner can set identifiers.");
                return;
            }

            String id = args[1];
            for (int i = 2; i < args.length; i++) id += " " + args[i];

            estate.setIdentifier(id);
            try {
                sendMessage(player, "Identifier set!");
                estate.save();
            } catch (SQLException e) {
                e.printStackTrace();
                sendMessage(player, TextFormatting.RED + "Something went wrong.");
            }
        } else if (args[0].equalsIgnoreCase("expand")) {
            if (creationCheck(player, true, false)) {
                BlockPos min = SelectionListener.getMinimum(player), max = SelectionListener.getMaximum(player);
                long width = Math.abs(max.getX() - min.getX());
                long length = Math.abs(max.getZ() - min.getZ());
                long height = Math.abs(max.getY() - min.getY());
                long area = width * length * height;
                long priceNewSelection = area * ModRealEstate.getConfig().getInt("price_per_block", 2);
                width = Math.abs(estateAtPos.getMaximum().getX() - estateAtPos.getMinimum().getX());
                length = Math.abs(estateAtPos.getMaximum().getZ() - estateAtPos.getMinimum().getZ());
                height = Math.abs(estateAtPos.getMaximum().getY() - estateAtPos.getMinimum().getY());
                area = width * length * height;
                long priceCurrentEstate = area * ModRealEstate.getConfig().getInt("price_per_block", 2);
                long price = priceNewSelection - priceCurrentEstate;

                if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.price") && ModEconomy.getBalanceInventory(player) < price) {
                    sendMessage(player, TextFormatting.RED + "Insufficient funds! Price for expansion: " + TextFormatting.DARK_RED + "$" + NumberConversions.format(price));
                    return;
                }

                if(!ModPermission.hasPermission(player.getUniqueID(), "estate.override.price") && !estateAtPos.getOwnerID().equals(player.getUniqueID())) {
                    sendMessage(player, TextFormatting.RED + "Only the owner can expand the estate.");
                    return;
                }

                if(price > Integer.MAX_VALUE) {
                    sendMessage(player, TextFormatting.RED + "Expansion too large.");
                    return;
                }

                ModEconomy.withdrawInventory(player, (int) price);
                estateAtPos.setMinimum(new BlockPos(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ())));
                estateAtPos.setMaximum(new BlockPos(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ())));
                sendMessage(player, "Estate expanded!");
            }
        } else if (args[0].equalsIgnoreCase("bounds")) {
            if (estateAtPos == null) {
                sendMessage(player, TextFormatting.RED + "There is no estate here.");
                return;
            }

            BlockPos[] selection = new BlockPos[]{estateAtPos.getMinimum(), estateAtPos.getMaximum()};
            SelectionListener.SELECTIONS.put(player, selection);
            BlockPos pos1 = selection[0];
            BlockPos pos2 = selection[1];
            BlockPos min = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
            BlockPos max = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
            Minelife.getNetwork().sendTo(new PacketSelection(min, max), player);
        } else {
            getUsage(sender);
        }
    }

    public static Set<PlayerPermission> getPlayerPermissions(UUID playerID) {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        for (PlayerPermission playerPermission : PlayerPermission.values()) {
            if (ModPermission.hasPermission(playerID, "estate." + playerPermission.name().toLowerCase())) {
                permissions.add(playerPermission);
            }
        }
        permissions.addAll(Lists.newArrayList(PlayerPermission.values()));
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

        if (!ModPermission.hasPermission(player.getUniqueID(), "estate.bypass.size") && width * length * height < 125) {
            if (sendMessages)
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Selection is not big enough. Must be at least 5x5x5."));
            else if (sendPopups)
                PacketPopup.sendPopup("Selection is not big enough. Must be at least 5x5x5.", player);
            return false;
        }

        Estate parentEstate = null;

        for (Estate estate : ModRealEstate.getLoadedEstates()) {
            boolean sameWorld = estate.getWorld().equals(player.getEntityWorld());
            boolean insideSelection = min.getX() < estate.getMinimum().getX() && min.getY() < estate.getMinimum().getY() && min.getZ() < estate.getMinimum().getZ()
                    && max.getX() > estate.getMaximum().getX() && max.getY() > estate.getMaximum().getY() && max.getZ() > estate.getMaximum().getZ();
            boolean intersects = estate.intersects(min, max);

            if (sameWorld) {
                if (insideSelection) {
                    if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.claim") && !estate.getOwnerID().equals(player.getUniqueID())) {
                        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Selection is overlapping another estate."));
                        if (sendPopups) PacketPopup.sendPopup("Selection is overlapping another estate.", player);
                        return false;
                    }
                } else if (intersects) {
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " Selection is intersecting another estate."));
                    if (sendPopups) PacketPopup.sendPopup("Selection is intersecting another estate.", player);
                    return false;
                }
            }

            if (sameWorld && estate.contains(min) && estate.contains(max)) {
                parentEstate = estate;
                break;
            }
        }

        if (parentEstate != null) {
            if (!ModPermission.hasPermission(player.getUniqueID(), "estate.override.claim") && !parentEstate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.CREATE_ESTATES)) {
                if (sendMessages)
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.RED + " You do not have permission to create estates here."));
                else if (sendPopups)
                    PacketPopup.sendPopup("You do not have permission to create estates here.", player);
                return false;
            }
        }

        return true;
    }

    public static void sendMessage(EntityPlayerMP player, String msg) {
        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.GOLD + msg));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
