package com.minelife.util;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.List;

public class SoundTrack {

    private World world;
    private double x, y, z;
    private EntityPlayer player;
    private List<Part> parts = Lists.newLinkedList();
    private long startTime;

    public void addPart(String id, long duration, float volume, float pitch) {
        long lastTime = parts.isEmpty() ? 0 : parts.get(parts.size() - 1).duration;
        parts.add(new Part(id, lastTime + duration, volume, pitch));
    }

    public void play(World world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        startTime = System.currentTimeMillis();
        playing.add(this);
    }

    public void play(EntityPlayer player) {
        this.player = player;
        this.startTime = System.currentTimeMillis();
        playing.add(this);
    }


    private class Part {
        String id;
        long duration;
        float volume, pitch;
        boolean played = false;

        public Part(String id, long duration, float volume, float pitch) {
            this.id = id;
            this.duration = duration;
            this.volume = volume;
            this.pitch = pitch;
        }
    }

    private static List<SoundTrack> playing = Lists.newArrayList();

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        List<SoundTrack> toRemove = Lists.newArrayList();

        for (SoundTrack soundTrack : playing) {
            boolean remove = soundTrack.parts.get(soundTrack.parts.size() - 1).played;
            for (Part part : soundTrack.parts) {
                if((soundTrack.startTime + part.duration <= System.currentTimeMillis()) && !part.played) {
                    part.played = true;
                    if(soundTrack.player != null) {
                        Minelife.getNetwork().sendTo(new PacketPlaySound(part.id, part.volume, part.pitch), (EntityPlayerMP) soundTrack.player);
                    } else if(soundTrack.world != null) {
                        soundTrack.world.playSound(null, soundTrack.x, soundTrack.y, soundTrack.z, new SoundEvent(new ResourceLocation(part.id)), SoundCategory.NEUTRAL, part.volume, part.pitch);
                    }
                }
            }
            if(remove) toRemove.add(soundTrack);
        }

        playing.removeAll(toRemove);
    }

}
