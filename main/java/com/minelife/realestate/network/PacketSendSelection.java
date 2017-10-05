package com.minelife.realestate.network;

import com.minelife.realestate.Selection;
import com.minelife.realestate.server.SelectionHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketSendSelection implements IMessage {

    private Selection selection;

    public PacketSendSelection(Selection selection) {
        this.selection = selection;
    }

    public PacketSendSelection() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if(buf.readBoolean()) return;
        selection = new Selection();
        selection.setPos1((int) buf.readDouble(), (int) buf.readDouble(), (int) buf.readDouble());
        selection.setPos2((int) buf.readDouble(), (int) buf.readDouble(), (int) buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(selection == null);
        if(selection == null) return;
        buf.writeDouble(selection.getMin().xCoord);
        buf.writeDouble(selection.getMin().yCoord);
        buf.writeDouble(selection.getMin().zCoord);
        buf.writeDouble(selection.getMax().xCoord);
        buf.writeDouble(selection.getMax().yCoord);
        buf.writeDouble(selection.getMax().zCoord);
    }

    public static class Handler implements IMessageHandler<PacketSendSelection, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendSelection message, MessageContext ctx) {
            if(message.selection != null) {
                message.selection.world = Minecraft.getMinecraft().theWorld;
                SelectionHandler.selection = message.selection;
            } else {
                SelectionHandler.selection = null;
            }
            return null;
        }

    }

}
