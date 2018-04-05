package com.minelife.realestate.network;

import com.minelife.realestate.Estate;
import com.minelife.realestate.client.gui.GuiBuyEstate;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketBuyGui implements IMessage {

    private Estate estate;

    public PacketBuyGui() {
    }

    public PacketBuyGui(Estate estate) {
        this.estate = estate;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estate = new Estate(UUID.fromString(ByteBufUtils.readUTF8String(buf)), ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, estate.getUniqueID().toString());
        ByteBufUtils.writeTag(buf, estate.getTagCompound());
    }

    public static class Handler implements IMessageHandler<PacketBuyGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBuyGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiBuyEstate(message.estate)));
            return null;
        }
    }

}
