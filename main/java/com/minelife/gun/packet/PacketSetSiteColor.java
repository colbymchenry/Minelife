package com.minelife.gun.packet;

import com.minelife.gun.item.attachments.ItemSite;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketSetSiteColor implements IMessage {

    private int red, green, blue;

    public PacketSetSiteColor() {
    }

    public PacketSetSiteColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        red = buf.readInt();
        green = buf.readInt();
        blue = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(red);
        buf.writeInt(green);
        buf.writeInt(blue);
    }

    public static class Handler implements IMessageHandler<PacketSetSiteColor, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetSiteColor message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack held_item = player.getHeldItem();

            if(held_item == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Site not found."));
                player.closeScreen();
                return null;
            }

            if(!(held_item.getItem() instanceof ItemSite)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Site not found."));
                player.closeScreen();
                return null;
            }

            ItemSite.setSiteColor(held_item, new int[]{message.red, message.green, message.blue});
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Site color updated!"));
            player.closeScreen();
            return null;
        }
    }

}
