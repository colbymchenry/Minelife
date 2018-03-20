package com.minelife.util.client;

import com.minelife.Minelife;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPopup implements IMessage {

    private String msg;
    private int bgColor, txtColor;

    public PacketPopup() {
    }

    public PacketPopup(String msg, int bgColor, int txtColor) {
        this.msg = msg;
        this.bgColor = bgColor;
        this.txtColor = txtColor;
    }

    public static void sendPopup(String msg, int bgColor, int txtColor, EntityPlayerMP player) {
        Minelife.getNetwork().sendTo(new PacketPopup(msg, bgColor, txtColor), player);
    }

    public static void sendPopup(String msg, EntityPlayerMP player) {
        Minelife.getNetwork().sendTo(new PacketPopup(msg, 0xC6C6C6, 4210752), player);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.msg = ByteBufUtils.readUTF8String(buf);
        this.bgColor = buf.readInt();
        this.txtColor = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.msg);
        buf.writeInt(this.bgColor);
        buf.writeInt(this.txtColor);
    }

    public static class Handler implements IMessageHandler<PacketPopup, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketPopup message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               Minecraft.getMinecraft().displayGuiScreen(new GuiPopup(Minecraft.getMinecraft().currentScreen, message.msg, message.bgColor, message.txtColor));
            });
            return null;
        }
    }

}
