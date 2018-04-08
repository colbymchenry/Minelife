package com.minelife.cape.network;

import com.minelife.cape.ModCapes;
import com.minelife.cape.server.CommandCape;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketSetPixels implements IMessage  {

    private int slot;
    private String pixels;

    public PacketSetPixels() {
    }

    public PacketSetPixels(int slot, String pixels) {
        this.slot = slot;
        this.pixels = pixels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        pixels = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeUTF8String(buf, pixels);
    }

    public static class Handler implements IMessageHandler<PacketSetPixels, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetPixels message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                ItemStack capeStack = player.inventory.getStackInSlot(message.slot).copy();

                if(capeStack.getItem() != ModCapes.itemCape) {
                    PacketPopup.sendPopup("Cape stack not found.", player);
                    return;
                }

                ModCapes.itemCape.setPixels(capeStack, message.pixels);
                ModCapes.itemCape.setUniqueID(capeStack);
                player.inventory.setInventorySlotContents(message.slot, capeStack);
                player.closeScreen();
                CommandCape.sendMessage(player, "Cape updated!");
            });
            return null;
        }
    }

}
