package com.minelife.realestate.client.packet;

import com.minelife.realestate.client.Selection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SelectionPurchaseRequest implements IMessage {

    private Selection selection;

    public SelectionPurchaseRequest() { }

    public SelectionPurchaseRequest(Selection selection) {
        this.selection = selection;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.selection = Selection.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.selection.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<SelectionPurchaseRequest, IMessage> {

        @Override
        public IMessage onMessage(SelectionPurchaseRequest message, MessageContext ctx) {
            message.selection.purchase();
            return null;
        }

    }

}