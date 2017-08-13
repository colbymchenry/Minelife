package com.minelife.minebay.client.gui;

import com.google.common.collect.Lists;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class SellItemGui extends MasterGui {

    private MLItemRenderer item_renderer;
    private List<ItemSlot> slots;
    private ItemStack item_to_sale;
    private float rotY;
    private GuiTextField price_field, amount_field;

    @Override
    public void initGui()
    {
        super.initGui();
        item_renderer = new MLItemRenderer(this);
        // setup slots
        slots = Lists.newArrayList();

        int xOffset = left + ((bg_width - (16 * 10)) / 2);
        int yOffset = top + 92;

        // players inventory
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x + y * 9 + 9], new Rectangle(xOffset + x * 18, yOffset + y * 18, 16, 16)));
            }
        }

        // players hotbar
        for (int x = 0; x < 9; ++x) {
            this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x], new Rectangle(xOffset + x * 18, yOffset + 58, 16, 16)));
        }

        price_field = new GuiTextField(fontRendererObj, this.left + this.bg_width - 85, this.top + 20, 75, 10);
        amount_field = new GuiTextField(fontRendererObj, this.left + this.bg_width - 85, this.top + 60, 75, 10);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        super.drawScreen(mouse_x, mouse_y, f);
        item_renderer.attempt_gl_reset();

        // draw the slot background
        Color color = new Color(72, 0, 70, 200);
        for (ItemSlot slot : slots) {
            this.drawGradientRect(slot.bounds.x, slot.bounds.y, slot.bounds.x + slot.bounds.width, slot.bounds.y + slot.bounds.height, color.hashCode(), color.hashCode());
        }

        // draw the item stack
        for (ItemSlot slot : slots) {
            if (slot.stack != null) item_renderer.drawItemStack(slot.stack, slot.bounds.x, slot.bounds.y, null);
        }

        // draw the highlight when mouse is over
        color = new Color(255, 255, 255, 100);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 400);
        for (ItemSlot slot : slots) {
            if (slot.bounds.contains(mouse_x, mouse_y)) {
                this.drawGradientRect(slot.bounds.x, slot.bounds.y, slot.bounds.x + slot.bounds.width, slot.bounds.y + slot.bounds.height, color.hashCode(), color.hashCode());
            }
        }
        GL11.glPopMatrix();

        // draw the tooltip
        for (ItemSlot slot : slots) {
            if (slot.stack != null && slot.bounds.contains(mouse_x, mouse_y)) {
                item_renderer.renderToolTip(slot.stack, mouse_x, mouse_y);
            }
        }

        color = new Color(64, 0, 62, 200);
        item_renderer.attempt_gl_reset();
        this.drawGradientRect(left + 10, top + 10, left + 10 + 60, top + 10 + 60, color.hashCode(), color.hashCode());

        // render selected item
        if(item_to_sale != null) {
            item_renderer.renderItem3D(item_to_sale, left + 40, top + 38, 30, rotY += 0.5f);
            item_renderer.fontRendererObj.setUnicodeFlag(true);
            item_renderer.renderToolTip(item_to_sale, left + 10 + 60, top + 26);
            item_renderer.fontRendererObj.setUnicodeFlag(false);
        }

        // draw price and amount fields
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        amount_field.drawTextBox();
        price_field.drawTextBox();
        fontRendererObj.drawString("Price: ", price_field.xPosition, price_field.yPosition - 10, 0xFFFFFF);
        fontRendererObj.drawString("Amount: ", amount_field.xPosition, amount_field.yPosition - 10, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        for(ItemSlot slot : slots) {
            if(slot.bounds.contains(mouse_x, mouse_y)) item_to_sale = slot.stack;
        }

        price_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        price_field.updateCursorCounter();
        amount_field.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        price_field.textboxKeyTyped(c, i);
        amount_field.textboxKeyTyped(c, i);

        // TODO: Make amount not go above item stack amount
    }

    class ItemSlot {
        ItemStack stack;
        Rectangle bounds;

        ItemSlot(ItemStack stack, Rectangle bounds)
        {
            this.stack = stack;
            this.bounds = bounds;
        }
    }
}
