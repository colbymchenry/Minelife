package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.*;
import com.minelife.util.client.PacketPopupMessage;
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
        } catch (IOException | InvalidConfigurationException e) {
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
            Set<Permission> playerPermissions = estate.getPlayerPermissions(player.getUniqueID());

            // if player has permission to sell, then set the purchase price
            if (Objects.equals(estate.getOwner(), player.getUniqueID())) {
                estate.setPurchasePrice(estateData.getPurchasePrice());
            }

            // if player has permission to rent the estate, set the rent price
            if (Objects.equals(estate.getOwner(), player.getUniqueID())) {
                estate.setRentPrice(estateData.getRentPrice());

                // remove renter
                if(estate.getRentPrice() < 1) {
                    if(estate.getRenter() != null) {
                        List<Billing.Bill> bills = Billing.getBillsForPlayer(estate.getRenter());
                        for (Billing.Bill bill : bills) {
                            if(bill.getBillHandler() instanceof RentBillHandler) {
                                RentBillHandler rentBillHandler = (RentBillHandler) bill.getBillHandler();
                                if(rentBillHandler.estateID == estate.getID()) rentBillHandler.bill.delete();
                            }
                        }
                    }
                    estate.setRenter(null);
                }

            }

            // if player has permission to modify the rent period, set the rent period
            if (Objects.equals(estate.getOwner(), player.getUniqueID())) {
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
                Set<Permission> toRemove = Sets.newTreeSet();

                if(!isAbsoluteOwner) {
                    permissions.addAll(estate.getGlobalPermissions());
                    estateData.getGlobalPermissions().forEach(p -> {
                        if (!estate.getGlobalPermissions().contains(p) && estate.getActualPermsAllowedToChange().contains(p)) {
                            permissions.add(p);
                        }
                    });

                    estate.getActualPermsAllowedToChange().forEach(p -> {
                        System.out.println(p.name());
                    });

                    estate.getGlobalPermissions().forEach(p -> {
                        if (!estateData.getGlobalPermissions().contains(p) && estate.getActualPermsAllowedToChange().contains(p)) {
                            toRemove.add(p);
                        }
                    });
                } else {
                    permissions.addAll(estateData.getGlobalPermissions());
                }

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
                    if (!playerPermissions.contains(p)) toRemove.add(p);
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
                    if (!playerPermissions.contains(p)) toRemove.add(p);
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
                    if (!playerPermissions.contains(p)) toRemove.add(p);
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
                    if (!playerPermissions.contains(p)) {
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

            Minelife.NETWORK.sendTo(new PacketPopupMessage("Estate updated!"), player);
            return null;
        }

    }

}
