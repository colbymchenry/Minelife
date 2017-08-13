package com.minelife.realestate.client.estateselection;

import com.minelife.MLItems;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.client.SelectionState;
import com.minelife.realestate.util.GUIUtil;
import com.minelife.realestate.util.PlayerUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Selection {

    private static SelectionState state = SelectionState.INACTIVE;
    private static Vector start;
    private static Vector end;
    private static Vector lastVariableEnd;

    private static Color[] selectionColor =  {  new Color(255, 0, 0, 60), new Color(0, 255, 0, 60) };
    private static int ableToPurchase = 0;

    private static Vector[] ranges = new Vector[4];
    private static Vector[] savedRanges;
    private static boolean savedSelected;

    private static long price;

    @SideOnly(Side.CLIENT)
    private static long pricePerBlock;

    private static String priceString = "";

    @SideOnly(Side.CLIENT)
    public static void render(RenderWorldLastEvent event) {

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;

        // Stop Selecting if player switches items and Save State
        if (player.getHeldItem() == null || player.getHeldItem() != null && !player.getHeldItem().getItem().equals(MLItems.estate_create_form)) {
            switch (state) {
                case SELECTING:
                    state = SelectionState.SAVED;
                    savedRanges = ranges;
                    savedSelected = false;
                    break;
                case SELECTED:
                    state = SelectionState.SAVED;
                    savedRanges = ranges;
                    savedSelected = true;
                    break;
                case INACTIVE:
                    state = SelectionState.INACTIVE;
                    break;
                default:
                    break;
            }
        }

        // Reselect if Saved
        if (player.getHeldItem() != null && player.getHeldItem().getItem().equals(MLItems.estate_create_form) && state.equals(SelectionState.SAVED)) {
            state = savedSelected ? SelectionState.SELECTED : SelectionState.SELECTING;
            ranges = savedRanges;
        }

        // Calculating Selection
        switch (state) {
            case SELECTING:
                // Draw Cuboid based on start and cursor block
                Vector e = PlayerUtil.getBlockCoordinatesInFocus();
                if (e == null) e = lastVariableEnd;
                if ((start == null && end == null) || e == null) break;
                lastVariableEnd = e;
                ranges[2] = ranges[0];
                ranges[3] = ranges[1];
                ranges[0] = start;
                ranges[1] = lastVariableEnd = e;
                break;
            case SELECTED:
                // Draw Cuboid based on start and end
                if (start == null || end == null) break;
                ranges[2] = ranges[0];
                ranges[3] = ranges[1];
                ranges[0] = start;
                ranges[1] = end;
                break;
            default: break;
        }

        // Pricing
        if (ranges[0] != null && ranges[1] != null && (ranges[2] == null || ranges[3] == null || !ranges[0].equals(ranges[2]) || !ranges[1].equals(ranges[3]))) {
            int diffX = Math.abs(ranges[0].getBlockX() - ranges[1].getBlockX()) + 1;
            int diffY = Math.abs(ranges[0].getBlockY() - ranges[1].getBlockY()) + 1;
            int diffZ = Math.abs(ranges[0].getBlockZ() - ranges[1].getBlockZ()) + 1;
            int numberOfBlocks = (diffX) * (diffY) * (diffZ);
            price = numberOfBlocks * pricePerBlock;
            priceString = "Total Price: $" + NumberConversions.formatter.format(price);
            long wallet = ModEconomy.BALANCE_WALLET_CLIENT;
            ableToPurchase = price > wallet ? 0 : 1;
        }

        // Drawing Selection
        switch (state) {
            case SELECTING:
                // Draw Cuboid based on start and cursor block
                Vector e = PlayerUtil.getBlockCoordinatesInFocus();
                if (e == null) e = lastVariableEnd;
                if ((start == null && end == null) || e == null) break;
                if (start != null) GUIUtil.drawCuboidAroundBlocks(minecraft, start, e, event.partialTicks, selectionColor[ableToPurchase], true);
                else GUIUtil.drawCuboidAroundBlocks(minecraft, end, e, event.partialTicks, selectionColor[ableToPurchase], true);
                break;
            case SELECTED:
                // Draw Cuboid based on start and end
                if (start == null || end == null) break;
                GUIUtil.drawCuboidAroundBlocks(minecraft, start, end, event.partialTicks, selectionColor[ableToPurchase], true);
                break;
            default: break;
        }

    }

    @SideOnly(Side.CLIENT)
    public static void renderPrice(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (!(state.equals(SelectionState.INACTIVE))) {
                GL11.glColor4f(1f, 1f, 1f, 1f);
                int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(priceString);
                Minecraft.getMinecraft().fontRenderer.drawString(priceString, event.resolution.getScaledWidth() - stringWidth - 2,  2, 0xFFFFFF);
                GL11.glColor4f(1f, 1f, 1f, 1f);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void cancelSelectionLeftClick(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem().equals(MLItems.estate_create_form) && event.button == 0) {
            event.setCanceled(true);
            player.getHeldItem().getItem().onEntitySwing(player, player.getHeldItem());
        }
    }

    public static SelectionState getState() {
        return state;
    }

    private static void setState() {
        if (start != null && end != null) state = SelectionState.SELECTED;
        else if (start == null && end == null) state = SelectionState.INACTIVE;
        else state = SelectionState.SELECTING;
    }

    public static void setStart(Vector point) {
        start = point;
        setState();
    }

    public static void setEnd(Vector point) {
        end = point;
        setState();
    }

    public static void setPricePerBlock(long price) {
        pricePerBlock = price;
    }

    public static AxisAlignedBB getSelection() {
        if (state.equals(SelectionState.SELECTED) && start != null && end != null) {
            Vector s = new Vector(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
            Vector e = new Vector(Math.max(start.getX(), end.getX()) + 1, Math.max(start.getY(), end.getY()) + 1, Math.max(start.getZ(), end.getZ()) + 1);
            return AxisAlignedBB.getBoundingBox(s.getBlockX(), s.getBlockY(), s.getBlockZ(), e.getBlockX(), e.getBlockY(), e.getBlockZ());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static long getPrice() {
        return price;
    }

}