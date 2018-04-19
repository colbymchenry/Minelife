package com.minelife.gangs.network;

import com.google.common.collect.Lists;
import com.minelife.gangs.Gang;
import com.minelife.gangs.client.gui.GuiGangMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PacketOpenGangGui implements IMessage {

    private Gang gang;
    private long balance;
    private List<Gang> alliances;

    public PacketOpenGangGui() {
    }

    public PacketOpenGangGui(Gang gang) {
        this.gang = gang;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gang = Gang.fromBytes(buf);
        balance = buf.readLong();
        alliances = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) alliances.add(Gang.fromBytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        gang.toBytes(buf);
        buf.writeLong(gang.getBalance());
        buf.writeInt(gang.getAlliances().size());
        gang.getAlliances().forEach(gang -> gang.toBytes(buf));
    }

    public static class Handler implements IMessageHandler<PacketOpenGangGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenGangGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiGangMenu(message.gang, message.balance, message.alliances)));
            return null;
        }

    }


}
