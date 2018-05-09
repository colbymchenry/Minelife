package com.minelife.jobs.network;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.signup.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenSignupGui implements IMessage {

    private EnumJob enumJob;

    public PacketOpenSignupGui() {
    }

    public PacketOpenSignupGui(EnumJob enumJob) {
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

    public static class Handler implements IMessageHandler<PacketOpenSignupGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenSignupGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                switch (message.enumJob) {
                    case FARMER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiFarmerSignup());
                        return;
                    case FISHERMAN:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiFishermanSignup());
                        return;
                    case LUMBERJACK:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiLumberjackSignup());
                        return;
                    case MINER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiMinerSignup());
                        return;
                    case RESTAURATEUR:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiRestaurateurSignup());
                        return;
                    case BOUNTY_HUNTER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiBountyHunterSignup());
                        return;
                    case DRUG_PRODUCER:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiDrugProducerSignup());
                        return;
                    case EMT:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiEMTSignup());
                        return;
                    case COP:
                        Minecraft.getMinecraft().displayGuiScreen(new GuiCopSignup());
                        return;
                }
            });
            return null;
        }
    }

}
