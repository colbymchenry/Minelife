package com.minelife.capes.network;

import com.minelife.MLItems;
import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketEditCape implements IMessage {

    private String pixelInfo;

    public PacketEditCape() {
    }

    public PacketEditCape(String pixelInfo) {
        this.pixelInfo = pixelInfo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pixelInfo = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, pixelInfo);
    }


    public static class Handler implements IMessageHandler<PacketEditCape, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketEditCape message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack capeStack = player.getHeldItem();

            String oldID = MLItems.cape.getUUID(capeStack);
            MLItems.cape.setPixels(capeStack, message.pixelInfo);
            MLItems.cape.setUUID(capeStack);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, capeStack);
            Minelife.NETWORK.sendToAllAround(new PacketRemoveCapeItemTexture(oldID), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 150));

            player.closeScreen();
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cape edited!"));


            return null;
        }

    }
}
