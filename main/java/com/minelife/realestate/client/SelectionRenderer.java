package com.minelife.realestate.client;

import com.minelife.util.client.render.LineRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SelectionRenderer {

    public static BlockPos MIN, MAX;
    private static Color COLOR = new Color(255, 100, 100, 128);

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        if (MIN != null && MAX != null &&
                Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.GOLDEN_HOE)
            LineRenderer.drawCuboidAroundsBlocks(Minecraft.getMinecraft(), new AxisAlignedBB(MIN.getX(), MIN.getY(), MIN.getZ(), MAX.getX(), MAX.getY(), MAX.getZ()), event.getPartialTicks(), COLOR, true, false);
    }

}
