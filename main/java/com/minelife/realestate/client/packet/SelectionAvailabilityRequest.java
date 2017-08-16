package com.minelife.realestate.client.packet;

import com.minelife.realestate.client.Selection;
import com.minelife.realestate.server.packet.SelectionAvailabilityResult;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SelectionAvailabilityRequest implements IMessage {

    private Selection selection;

    public SelectionAvailabilityRequest() { }

    public SelectionAvailabilityRequest(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.selection = Selection.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        selection.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<SelectionAvailabilityRequest, SelectionAvailabilityResult> {

        @Override
        public SelectionAvailabilityResult onMessage(SelectionAvailabilityRequest message, MessageContext ctx) {
            return new SelectionAvailabilityResult(message.selection.isAvailable());
        }

    }

}