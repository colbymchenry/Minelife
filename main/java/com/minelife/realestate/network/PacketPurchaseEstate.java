package com.minelife.realestate.network;

import com.minelife.economy.ModEconomy;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.UUID;

public class PacketPurchaseEstate implements IMessage {

    private UUID estateID;
    private boolean rent;

    public PacketPurchaseEstate() {
    }

    public PacketPurchaseEstate(UUID estateID, boolean rent) {
        this.estateID = estateID;
        this.rent = rent;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.estateID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.rent = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.estateID.toString());
        buf.writeBoolean(this.rent);
    }

    public static class Handler implements IMessageHandler<PacketPurchaseEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketPurchaseEstate message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Estate estate = ModRealEstate.getEstate(message.estateID);

                if(estate == null) {
                    PacketPopup.sendPopup("Estate not found.", player);
                    return;
                }

               // TODO: Checking balance
            });
            return null;
        }
    }

}
