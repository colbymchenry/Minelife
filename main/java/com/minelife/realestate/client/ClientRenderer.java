package com.minelife.realestate.client;

import com.minelife.MLItems;
import com.minelife.realestate.client.util.GUIUtil;
import com.minelife.realestate.client.util.PlayerUtil;
import com.minelife.realestate.item.SelectionState;
import com.minelife.util.Vector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.*;

public class ClientRenderer {

    private static SelectionState state = SelectionState.INACTIVE;
    private static Vector start;
    private static Vector end;
    private static Vector lastVariableEnd;

    private static Color selectionColor = new Color(152, 251, 152, 60);

    private static Vector[] ranges = new Vector[4];
    private static Block[] priceExclusions = {Blocks.air, Blocks.fire};

    @SubscribeEvent
    public void tick(RenderWorldLastEvent event) {

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;

        // Stop Selecting if player switches items
        if (player.getHeldItem() != null && !player.getHeldItem().getItem().equals(MLItems.estate_claiming_tool)) state = SelectionState.INACTIVE;


        // Drawing Selection
        switch (state) {

            case SELECTING:

                // Draw Cuboid based on start and cursor block
                Vector e = PlayerUtil.getBlockCoordinatesInFocus();
                if (e == null) e = lastVariableEnd;
                if (start == null || e == null) break;
                GUIUtil.drawCuboidAroundBlocks(minecraft, start, e, event.partialTicks, selectionColor);
                lastVariableEnd = e;
                ranges[2] = ranges[0];
                ranges[3] = ranges[1];
                ranges[0] = start;
                ranges[1] = e;
                break;

            case SELECTED:

                // Draw Cuboid based on start and end
                if (start == null || end == null) break;
                GUIUtil.drawCuboidAroundBlocks(minecraft, start, end, event.partialTicks, selectionColor);
                ranges[2] = ranges[0];
                ranges[3] = ranges[1];
                ranges[0] = start;
                ranges[1] = end;
                break;

            default: break;

        }

        // Pricing
        if (ranges[0] != null && ranges[1] != null && (ranges[2] == null || ranges[3] == null || !ranges[0].equals(ranges[2]) || !ranges[1].equals(ranges[3]))) {

            int numberOfBlocks = 0;

            for (int i = Math.min(ranges[0].getBlockX(), ranges[1].getBlockX()); i <= Math.max(ranges[0].getBlockX(), ranges[1].getBlockX()); i++) {
                for (int j = Math.min(ranges[0].getBlockY(), ranges[1].getBlockY()); j <= Math.max(ranges[0].getBlockY(), ranges[1].getBlockY()); j++) {
                    ZLoop:
                    for (int k = Math.min(ranges[0].getBlockZ(), ranges[1].getBlockZ()); k <= Math.max(ranges[0].getBlockZ(), ranges[1].getBlockZ()); k++) {
                        for (Block block : priceExclusions) {
                            if (player.worldObj.getBlock(i, j, k) != null && player.worldObj.getBlock(i, j, k).equals(block)) {
                                continue ZLoop;
                            }
                        }
                        ++numberOfBlocks;
                    }
                }
            }

            PlayerUtil.sendTo(player, "Total Price: $" + (numberOfBlocks * 2) + ".");

        }

    }

    public static SelectionState getState() {
        return state;
    }

    public static void setSelecting(SelectionState selectionState) {
        state = selectionState;
    }

    public static void setStart(Vector point) {
        start = point;
    }

    public static void setEnd(Vector point) {
        end = point;
    }

}