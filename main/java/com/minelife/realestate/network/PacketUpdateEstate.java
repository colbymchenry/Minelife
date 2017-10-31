package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateData;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class PacketUpdateEstate implements IMessage {

    private EstateData estateData;

    public PacketUpdateEstate() {
    }

    public PacketUpdateEstate(EstateData estateData) {
        this.estateData = estateData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            estateData = EstateData.fromBytes(buf);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        estateData.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketUpdateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketUpdateEstate message, MessageContext ctx) {
            /*
             * Check every value from the estatedata with the estate in the server and update according to the
             * player's permissions on whether or not they can do so.
             */

            Estate estate = EstateHandler.getEstate(message.estateData.getID());
            EstateData estateData = message.estateData;
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Set<Permission> playerPermissions = estate.getPlayerPermissions(player);

            // if player has permission to sell, then set the purchase price
            if (playerPermissions.contains(Permission.SELL)) {
                estate.setPurchasePrice(estateData.getPurchasePrice());
            }

            // if player has permission to rent the estate, set the rent price
            if (playerPermissions.contains(Permission.RENT)) {
                estate.setRentPrice(estateData.getRentPrice());
            }

            // if player has permission to modify the rent period, set the rent period
            if (playerPermissions.contains(Permission.MODIFY_RENT_PERIOD)) {
                estate.setRentPeriod(estateData.getRentPeriod());
            }

            // if player has permission to set the intro update the intro
            if (playerPermissions.contains(Permission.MODIFY_INTRO)) {
                estate.setIntro(estateData.getIntro());
            }

            // if player has permission to set the outro update the outro
            if (playerPermissions.contains(Permission.MODIFY_OUTRO)) {
                estate.setOutro(estateData.getOutro());
            }

            /*
             *
             *
             *
             *
             *
             *
             */

            boolean isOwner = Objects.equals(estate.getOwner(), player.getUniqueID());
            boolean isAbsoluteOwner = estate.isAbsoluteOwner(player.getUniqueID());
            boolean isRenter = Objects.equals(estate.getRenter(), player.getUniqueID());
            Set<Permission> permissions = Sets.newTreeSet();

            /*
             * Set global permissions
             */
            if (isOwner || isRenter || isAbsoluteOwner) {
                permissions.addAll(estateData.getGlobalPermissions());
                Set<Permission> toRemove = Sets.newTreeSet();
                permissions.forEach(p -> {
                    if (!estate.getPlayerPermissions(player).contains(p)) toRemove.add(p);
                });
                permissions.removeAll(toRemove);
                estate.setGlobalPermissions(permissions);
            }

            permissions.clear();

            /*
             * Set owner permissions
             */
            if (isAbsoluteOwner) {
                permissions.addAll(estateData.getOwnerPermissions());
                Set<Permission> toRemove = Sets.newTreeSet();
                permissions.forEach(p -> {
                    if (!estate.getPlayerPermissions(player).contains(p)) toRemove.add(p);
                });
                permissions.removeAll(toRemove);
                estate.setOwnerPermissions(permissions);
            }
            permissions.clear();

            /*
             * Set renter permissions
             */
            if (isAbsoluteOwner || isOwner) {
                permissions.addAll(estateData.getRenterPermissions());
                Set<Permission> toRemove = Sets.newTreeSet();
                permissions.forEach(p -> {
                    if (!estate.getPlayerPermissions(player).contains(p)) toRemove.add(p);
                });
                permissions.removeAll(toRemove);
                estate.setRenterPermissions(permissions);
            }
            permissions.clear();

            /*
             * Set permissions allowed to change
             */
            if (isAbsoluteOwner) {
                permissions.addAll(estateData.getGlobalPermissionsAllowedToChange());
                Set<Permission> toRemove = Sets.newTreeSet();
                permissions.forEach(p -> {
                    if (!estate.getPlayerPermissions(player).contains(p)) toRemove.add(p);
                });
                permissions.removeAll(toRemove);
                estate.setPermissionsAllowedToChange(permissions);
            }

            permissions.clear();

            /*
             * Set estate permissions
             */
            if (isAbsoluteOwner || isOwner) {
                permissions.addAll(estate.getEstatePermissions());

                Set<Permission> toRemove = Sets.newTreeSet();
                permissions.forEach(p -> {
                    if (!estate.getPlayerPermissions(player).contains(p)) {
                        toRemove.add(p);
                    }
                    if (!ModPermission.hasPermission(player.getUniqueID(), "estate." + p.name().toLowerCase())) {
                        toRemove.add(p);
                    }
                });

                Permission.getEstatePermissions().forEach(p -> {
                    if(!toRemove.contains(p)) {
                        if(!permissions.contains(p) && estateData.getEstatePermissions().contains(p)) {
                            // turn on
                            permissions.add(p);
                        } else if (permissions.contains(p) && !estateData.getEstatePermissions().contains(p)) {
                            // turn off
                            permissions.remove(p);
                        }
                    }
                });

                estate.setEstatePermissions(permissions);
            }
            return null;
        }

    }

}
