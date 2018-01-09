package com.minelife.essentials;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.util.Location;
import com.minelife.util.SoundTrack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;
import java.util.Set;

public class TeleportHandler {

    private static Set<TeleportQue> teleportQue = Sets.newTreeSet();
    private static Set<TeleportQue> toRemove = Sets.newTreeSet();

    private int tick = 0;

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        tick++;

        if(tick >= 20) {
            toRemove.clear();
            teleportQue.forEach(que -> {
                que.seconds--;
                if(que.seconds <= 0) {
                    que.player.setWorld(que.location.getEntityWorld());
                    que.player.setPositionAndUpdate(que.location.getX(), que.location.getY(), que.location.getZ());
                    toRemove.add(que);
                } else {
                    ModEssentials.sendTitle(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD.toString() + que.seconds, null, 1, que.player);
                    SoundTrack soundTrack = new SoundTrack();
                    soundTrack.addPart("minecraft:note.pling", 0L, 1, 1);
                    soundTrack.play(que.player);
                }
            });
            tick = 0;
        }
    }

    public static void teleport(EntityPlayerMP player, Location location, int duration) {
        teleportQue.add(new TeleportQue(player, location, duration));
    }

    private static class TeleportQue implements Comparable<TeleportQue> {
        public EntityPlayerMP player;
        public Location location;
        public int seconds;

        public TeleportQue(EntityPlayerMP player, Location location, int seconds) {
            this.player = player;
            this.location = location;
            this.seconds = seconds;
        }

        @Override
        public int compareTo(TeleportQue o) {
            return o.player.getUniqueID().toString().compareTo(player.getUniqueID().toString());
        }
    }

}
