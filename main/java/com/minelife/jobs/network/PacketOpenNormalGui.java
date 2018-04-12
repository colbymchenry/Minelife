package com.minelife.jobs.network;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.farmer.gui.GuiFarmer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenNormalGui implements IMessage {

    private EnumJob enumJob;

    public PacketOpenNormalGui() {
    }

    public PacketOpenNormalGui(EnumJob enumJob) {
        this.enumJob = enumJob;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        enumJob = EnumJob.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(enumJob.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketOpenNormalGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenNormalGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                switch (message.enumJob) {
                    case FARMER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiFarmer());
                        return;
                }
            });
            return null;
        }
    }

}