package com.minelife.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketPlaySound message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().player;
//            Minecraft.getMinecraft().addScheduledTask(() -> player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.MASTER, message.volume, message.pitch));
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getSoundHandler().
                    playSound(PositionedSoundRecord.getRecord(new SoundEvent(new ResourceLocation(message.sound)), message.pitch, message.volume)));
            return null;
        }
    }
}
