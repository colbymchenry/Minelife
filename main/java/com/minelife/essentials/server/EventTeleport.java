package com.minelife.essentials.server;

import com.minelife.essentials.Location;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventTeleport extends Event {

    private Location location;
    private EntityPlayerMP player;

    public EventTeleport(Location location, EntityPlayerMP player) {
        this.location = location;
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }
}
