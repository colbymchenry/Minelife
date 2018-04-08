package com.minelife.realestate.network;

import com.minelife.realestate.client.gui.GuiMembers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;
import java.util.UUID;

public class PacketAddedMember implements IMessage {

    private UUID playerID;

    public PacketAddedMember() {
    }

    public PacketAddedMember(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
    }

    public static class Handler implements IMessageHandler<PacketAddedMember, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketAddedMember message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if(Minecraft.getMinecraft().currentScreen instanceof GuiMembers) {
                    GuiMembers guiMembers = (GuiMembers) Minecraft.getMinecraft().currentScreen;
                    Set<UUID> memberIDs = guiMembers.getEstate().getMemberIDs();
                    memberIDs.add(message.playerID);
                }
            });

            return null;
        }
    }

}
