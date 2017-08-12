package com.minelife.realestate.packets;

import com.minelife.realestate.Packet;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class ServerTestPacket extends Packet {

    private String message;

    public ServerTestPacket() { }

    ServerTestPacket(String message) {

        this.message = message;

    }

    @Override
    public Side sideOfHandling() {
        return Side.CLIENT;
    }

    @Override
    public void handle(MessageContext ctx) {
        System.out.println(this.message);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.message);
    }

}