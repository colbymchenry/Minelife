package com.minelife.realestate.network;

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
            /**
             * Check every value from the estatedata with the estate in the server and update according to the
             * player's permissions on whether or not they can do so.
             */

            Estate estate = EstateHandler.getEstate(message.estateData.getID());
            EstateData estateData = message.estateData;
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            List<Permission> playerPermissions = estate.getPlayerPermissions(player);

            // if player has permission to sell, then set the purchase price
            if(playerPermissions.contains(Permission.SELL)) {
                estate.setPurchasePrice(estateData.getPurchasePrice());
            }

            // if player has permission to rent the estate, set the rent price
            if(playerPermissions.contains(Permission.RENT)) {
                estate.setRentPrice(estateData.getRentPrice());
            }

            // if player has permission to modify the rent period, set the rent period
            if(playerPermissions.contains(Permission.MODIFY_RENT_PERIOD)) {
                estate.setRentPeriod(estateData.getRentPeriod());
            }

            // if player has permission to set the intro update the intro
            if(playerPermissions.contains(Permission.MODIFY_INTRO)) {
                estate.setIntro(estateData.getIntro());
            }

            // if player has permission to set the intro update the outro
            if(playerPermissions.contains(Permission.MODIFY_OUTRO)) {
                estate.setOutro(estateData.getOutro());
            }

            // check if player can modify global perms allowed to change


            // check all global perms allowed to change and update the global perms
            for (Permission p : estate.getGlobalPermissionsAllowedToChange()) {
                estateData.getGlobalPermissionsAllowedToChange().removeAll(estate.getGlobalPermissionsAllowedToChange());
            }


            return null;
        }

    }

}
