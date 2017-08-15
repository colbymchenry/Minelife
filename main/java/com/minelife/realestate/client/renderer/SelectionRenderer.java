package com.minelife.realestate.client.renderer;

import com.minelife.MLItems;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.client.Selection;
import com.minelife.realestate.util.PlayerUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class SelectionRenderer {

    private static Selection selection, lastSelection;
    private static Vector start, end;
    private static String text;
    private static long price = 0;

    static void onEventTick(RenderWorldLastEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        // Don't Render The Selection If They Are Not Holding The Form
        if (player.getHeldItem() == null || !player.getHeldItem().getItem().equals(MLItems.estate_claim_form)) return;
        Vector focus = PlayerUtil.getBlockCoordinatesInFocus();
        if (selection != null) lastSelection = selection.copy();
        if (start != null && end != null) {
            // Has Full Selection
            if (selection == null) selection = new Selection(start, end);
            else selection.setBounds(start, end);
        } else if (start != null && focus != null) {
            // Has Start
            if (selection == null) selection = new Selection(start, focus);
            else selection.setBounds(start, focus);
        } else if (end != null && focus != null) {
            // Has End
            if (selection == null) selection = new Selection(focus, end);
            else selection.setBounds(focus, end);
        } else {
            // No Selection
            selection = null;
        }
        if (selection != null) {
            if (!selection.equals(lastSelection)) price = selection.getPrice();
            boolean purchasable = ModEconomy.BALANCE_WALLET_CLIENT >= price;
            if (purchasable) selection.highlight(event.partialTicks, new Color(0, 255, 0, 50));
            else selection.highlight(event.partialTicks, new Color(255, 0, 0, 50));
            text = "Total Price: " + (purchasable ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + "$" + NumberConversions.formatter.format(price);
        }
    }

    static void onEventTick(RenderGameOverlayEvent event) {
        int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        Minecraft.getMinecraft().fontRenderer.drawString(text, event.resolution.getScaledWidth() - width - 2, 2, 0xFFFFFF);
    }

    public static Selection getSelection() {
        return selection;
    }

    public static void setStart(Vector vector) {
        if (vector != null) start = vector;
    }

    public static void setEnd(Vector vector) {
        if (vector != null) end = vector;
    }

    public static void clear() {
        start = null;
        end = null;
        selection = null;
    }

}