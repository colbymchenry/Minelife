package com.minelife.util;

import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketPlaySound implements IMessage {

    private String sound;
    private float volume, pitch;

    public PacketPlaySound() {
    }

    public PacketPlaySound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sound = ByteBufUtils.readUTF8String(buf);
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.sound);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    public static class Handler implements IMessageHandler<PacketPlaySound, IMessage> {

        @Override
        public IMessage onMessage(PacketPlaySound message, MessageContext ctx) {
            Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + message.sound, message.volume, message.pitch);
            return null;
        }
    }
}
