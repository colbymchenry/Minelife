package com.minelife.guns.packet;

import com.minelife.guns.client.GuiModifyGun;
import com.minelife.guns.item.EnumGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketChangeSkinResponse implements IMessage {

    private EnumGun gunType;

    public PacketChangeSkinResponse() {
    }

    public PacketChangeSkinResponse(EnumGun gunType) {
        this.gunType = gunType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gunType = EnumGun.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gunType.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketChangeSkinResponse, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketChangeSkinResponse message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               if(Minecraft.getMinecraft().currentScreen != null &&
                       Minecraft.getMinecraft().currentScreen instanceof GuiModifyGun) {
                   GuiModifyGun guiModifyGun = (GuiModifyGun) Minecraft.getMinecraft().currentScreen;
                   guiModifyGun.gunType = message.gunType;
                   guiModifyGun.gunStack.setItemDamage(message.gunType.ordinal());
               }
            });
            return null;
        }
    }

}
