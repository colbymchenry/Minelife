package com.minelife.tutorial;

import com.google.common.collect.Sets;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class PacketSendTutorials implements IMessage {

    public Set<Section> sections;

    public PacketSendTutorials() {}

    public PacketSendTutorials(Set<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sections = Sets.newTreeSet();
        int sectionsSize = buf.readInt();
        for (int i = 0; i < sectionsSize; i++) sections.add(Section.fromBytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(sections.size());
        sections.forEach(section -> section.toBytes(buf));
    }

    public static class Handler implements IMessageHandler<PacketSendTutorials, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendTutorials message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiTutorial(message.sections)));
            return null;
        }
    }

}
