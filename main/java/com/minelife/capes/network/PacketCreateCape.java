package com.minelife.capes.network;

import com.minelife.MLItems;
import com.minelife.util.client.netty.NettyOutbound;
import cpw.mods.fml.common.network.ByteBufUtils;
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

public class PacketCreateCape implements IMessage {

    private String pixelInfo;

    public PacketCreateCape() {
    }

    public PacketCreateCape(String pixelInfo) {
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

    public static class Handler implements IMessageHandler<PacketCreateCape, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateCape message, MessageContext ctx) {
//            NettyOutbound outboundund = new NettyOutbound(0);
//            outbound.write(ctx.getServerHandler().playerEntity.getUniqueID().toString());
//            outbound.write(message.pixelInfo);
//            outbound.send();
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack capeStack = new ItemStack(MLItems.cape);
            MLItems.cape.setPixels(capeStack, message.pixelInfo);
            MLItems.cape.setUUID(capeStack);

            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            EntityItem entity_item = player.dropPlayerItemWithRandomChoice(capeStack, false);
            entity_item.delayBeforeCanPickup = 0;
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cape created!"));

            return null;
        }
    }

}
