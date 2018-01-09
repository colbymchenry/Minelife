package com.minelife.essentials;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.util.Location;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.Map;
import java.util.Set;

public class TeleportHandler {

    private static Set<TeleportQue> teleportQue = Sets.newTreeSet();

    private int tick = 0;

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        tick++;

        if(tick >= 20) {
            teleportQue.forEach(que -> {
//                que.player.addChatComponentMessage(new ChatComponentText());
            });
            tick = 0;
        }
    }

    private class TeleportQue implements Comparable<TeleportQue> {
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
