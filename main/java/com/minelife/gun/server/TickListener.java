package com.minelife.gun.server;

import com.minelife.util.PlayerHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;

public class TickListener {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.WorldTickEvent event) {
        for (Vec3 currentPosVec : PlayerHelper.VECTORS) {
            event.world.setBlock((int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord, Blocks.diamond_block);
        }
    }

}
