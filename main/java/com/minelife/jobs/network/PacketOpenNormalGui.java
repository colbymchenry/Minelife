package com.minelife.jobs.network;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.GuiJobBase;
import com.minelife.jobs.job.drugproducer.GuiDrugProducer;
import com.minelife.jobs.job.farmer.GuiFarmer;
import com.minelife.jobs.job.fisherman.GuiFisherman;
import com.minelife.jobs.job.lumberjack.GuiLumberjack;
import com.minelife.jobs.job.miner.GuiMiner;
import com.minelife.jobs.job.signup.*;
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
                    case FISHERMAN:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiFisherman());
                        return;
                    case LUMBERJACK:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiLumberjack());
                        return;
                    case MINER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiMiner());
                        return;
                    case DRUG_PRODUCER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiDrugProducer());
                        return;
                }
            });
            return null;
        }
    }

}