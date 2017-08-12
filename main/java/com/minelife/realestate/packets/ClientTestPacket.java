package com.minelife.realestate.packets;

import com.minelife.Minelife;
import com.minelife.realestate.Packet;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class ClientTestPacket extends Packet {

    private String message;

    public ClientTestPacket() { }

    ClientTestPacket(String message) {

        this.message = message;

    }

    @Override
    public Side sideOfHandling() {
        return Side.SERVER;
    }

    @Override
    public void handle(MessageContext ctx) {
        System.out.println(this.message);
        Minelife.NETWORK.sendTo(new ServerTestPacket("Packet delivered to Client."), ctx.getServerHandler().playerEntity);
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