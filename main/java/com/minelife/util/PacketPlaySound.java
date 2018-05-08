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
    private int posX = -777, posY = -777, posZ = -777;

    public PacketPlaySound() {
    }

    public PacketPlaySound(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }


    public PacketPlaySound(String sound, float volume, float pitch, double posX, double posY, double posZ) {
        this(sound, volume, pitch);
        this.posX = (int) posX;
        this.posY = (int) posY;
        this.posZ = (int) posZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sound = ByteBufUtils.readUTF8String(buf);
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.sound);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
    }

    public static class Handler implements IMessageHandler<PacketPlaySound, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketPlaySound message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().player;
//            Minecraft.getMinecraft().addScheduledTask(() -> player.getEntityWorld().playSound(player, player.posX, player.posY, player.posZ, new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.MASTER, message.volume, message.pitch));

            if(message.posX != -777 && message.posY != -777 && message.posZ != -777) {
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().world.playSound((double)message.posX,(double) message.posY, (double)message.posZ, new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.NEUTRAL, message.pitch, message.volume, false));
            } else {
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().getSoundHandler().
                        playSound(PositionedSoundRecord.getRecord(new SoundEvent(new ResourceLocation(message.sound)), message.pitch, message.volume)));
            }
            return null;
        }
    }
}
