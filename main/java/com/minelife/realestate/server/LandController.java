package com.minelife.realestate.server;

import com.minelife.realestate.Zone;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.world.BlockEvent;

public class LandController {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        Zone zone = Zone.getZone(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));

        if(zone != null) {
            event.getPlayer().addChatComponentMessage(new ChatComponentText("ZONE"));
        }
    }

}
