package com.minelife.realestate.client;

import com.minelife.util.BlockVector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class RenderSelection {

    public static BlockVector min, max;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
//        Chunk chunk = selectedTile.getWorld().getChunkFromBlockCoords(selectedTile.getPos());
//
//        int x = chunk.xPosition * 16;
//        int y = selectedTile.getPos().getY();
//        int z = chunk.zPosition * 16;

//        int color = 0xffee00;
//
//        GuiUtil.drawSquareInWorld(x + 16, y, z, 16, 1, 180f, event.getPartialTicks(), color);
//        GuiUtil.drawSquareInWorld(x, y, z, 16, 1, 0f, event.getPartialTicks(), color);
//
//        GuiUtil.drawSquareInWorld(x + 16, y, z + 16, 16, 1, 90f, event.getPartialTicks(), color);
//        GuiUtil.drawSquareInWorld(x + 16, y, z, 16, 1, -90f, event.getPartialTicks(), color);
//
//        GuiUtil.drawSquareInWorld(x, y, z + 16, 16, 1, 0f, event.getPartialTicks(), color);
//        GuiUtil.drawSquareInWorld(x + 16, y, z + 16, 16, 1, 180f, event.getPartialTicks(), color);
//
//        GuiUtil.drawSquareInWorld(x, y, z, 16, 1, -90f, event.getPartialTicks(), color);
//        GuiUtil.drawSquareInWorld(x, y, z + 16, 16, 1, 90f, event.getPartialTicks(), color);
    }

}
