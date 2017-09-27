package com.minelife.util.client;

import com.google.common.collect.Lists;
import com.minelife.minebay.client.gui.SellItemGui;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public abstract class GuiFakeInventory extends Gui {

    public List<ItemSlot> slots = Lists.newArrayList();
    public MLItemRenderer item_renderer;
    public Color slotColor =  new Color(72, 0, 70, 200);
    public Color highlightColor = new Color(255, 255, 255, 100);

    public GuiFakeInventory(Minecraft mc) {
        item_renderer = new MLItemRenderer(mc);
        setupSlots();
    }
    
    public void draw(int x, int y) {
        item_renderer.attempt_gl_reset();

        // draw the slot background
        for (ItemSlot slot : slots) {
            this.drawGradientRect(slot.bounds.x, slot.bounds.y, slot.bounds.x + slot.bounds.width, slot.bounds.y + slot.bounds.height, slotColor.hashCode(), slotColor.hashCode());
        }

        // draw the item stack
        for (ItemSlot slot : slots) {
            if (slot.stack != null) item_renderer.drawItemStack(slot.stack, slot.bounds.x, slot.bounds.y);
        }

        // draw the highlight when mouse is over
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 400);
        for (ItemSlot slot : slots) {
            if (slot.bounds.contains(x, y)) {
                this.drawGradientRect(slot.bounds.x, slot.bounds.y, slot.bounds.x + slot.bounds.width, slot.bounds.y + slot.bounds.height, highlightColor.hashCode(), highlightColor.hashCode());
            }
        }
        GL11.glPopMatrix();

        // draw the tooltip
        for (ItemSlot slot : slots) {
            if (slot.stack != null && slot.bounds.contains(x, y)) {
                item_renderer.renderToolTip(slot.stack, x, y);
            }
        }

        item_renderer.attempt_gl_reset();
    }

    public int getClickedSlot(int x, int y) {
        for(ItemSlot slot : slots) {
            if(slot.bounds.contains(x, y)) {
                return slot.index;
            }
        }

        return -1;
    }
    
    public abstract void setupSlots();
    
    public static class ItemSlot {
        public ItemStack stack;
        public Rectangle bounds;
        public int index;

        public ItemSlot(ItemStack stack, Rectangle bounds, int index)
        {
            this.stack = stack;
            this.bounds = bounds;
            this.index= index;
        }
    }
}
