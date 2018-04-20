package com.minelife.essentials;

import com.google.common.collect.Sets;
import com.minelife.essentials.server.EventTeleport;
import com.minelife.util.SoundTrack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Teleporter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Set;

public class TeleportHandler {

    private static Set<TeleportQue> teleportQue = Sets.newTreeSet();
    private static Set<TeleportQue> toRemove = Sets.newTreeSet();

    private int tick = 0;

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        tick++;

        if(tick >= 40) {
            toRemove.clear();
            teleportQue.forEach(que -> {
                que.seconds--;
                if(que.seconds <= 0) {
                    EventTeleport eventTeleport = new EventTeleport(que.location, que.player);
                    MinecraftForge.EVENT_BUS.post(eventTeleport);

                    if(!eventTeleport.isCanceled()) {
                        // transfer to the location's world
                        if (que.player.getEntityWorld().provider.getDimension() != que.location.getEntityWorld().provider.getDimension()) {
                            que.player.mcServer.getPlayerList().transferPlayerToDimension(que.player, que.location.getEntityWorld().provider.getDimension(), new Teleporter(que.player.getServerWorld()));
                        }
                        que.player.connection.setPlayerLocation(que.location.getX(), que.location.getY(), que.location.getZ(), que.location.getYaw(), que.location.getPitch());
                    }

                    toRemove.add(que);
                } else {
                    ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + que.seconds, null, 1, que.player);
                    SoundTrack soundTrack = new SoundTrack();
                    if(que.seconds == 1) {
                        soundTrack.addPart("minecraft:block.note.pling", 0L, 1, 1.5F);
                    } else {
                        soundTrack.addPart("minecraft:block.note.pling", 0L, 1, 1);
                    }
                    soundTrack.play(que.player);
                }
            });

            teleportQue.removeAll(toRemove);
            tick = 0;
        }
    }

    public static void teleport(EntityPlayerMP player, Location location, int duration) {
        teleportQue.add(new TeleportQue(player, location, duration));
    }

    public static void teleport(EntityPlayerMP player, Location location) {
        teleportQue.add(new TeleportQue(player, location, ModEssentials.getConfig().getInt("teleport_warmup")));
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
