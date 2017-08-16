package com.minelife.realestate.server.packet;

import com.minelife.realestate.client.Selection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SelectionAvailabilityResult implements IMessage {

    private boolean isAvailable;

    public SelectionAvailabilityResult() { }

    public SelectionAvailabilityResult(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isAvailable = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isAvailable);
    }

    public static class Handler implements IMessageHandler<SelectionAvailabilityResult, IMessage> {

        @Override
        public IMessage onMessage(SelectionAvailabilityResult message, MessageContext ctx) {
            Selection.setAvailable(message.isAvailable);
            return null;
        }

    }

}