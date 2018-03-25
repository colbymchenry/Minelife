package com.minelife.minebay.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.ModMinebay;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PacketGetPlayerListings implements IMessage {

    public PacketGetPlayerListings() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketGetPlayerListings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketGetPlayerListings message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            try {
                List<ItemListing> itemListings = Lists.newArrayList();
                ResultSet result = ModMinebay.getDatabase().query("SELECT * FROM items WHERE seller='" + player.getUniqueID().toString() + "'");
                while(result.next()) itemListings.add(new ItemListing(result));
                Minelife.getNetwork().sendTo(new PacketSendItemListings(itemListings, 1), player);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
