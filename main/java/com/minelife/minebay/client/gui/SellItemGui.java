package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.packet.PacketSellItem;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class SellItemGui extends MasterGui {

    private GuiFakeInventory fakeInventory;
    private ItemStack item_to_sale;
    private GuiTextField price_field, amount_field;
    private GuiButton sell_btn;
    private int slot;

    @Override
    public void initGui()
    {
        super.initGui();
        int xOffset = left + ((bg_width - (16 * 10)) / 2);
        int yOffset = top + 92;

        fakeInventory = new GuiFakeInventory(mc) {
            @Override
            public void setupSlots()
            {
                // players hotbar
                for (int x = 0; x < 9; ++x) {
                    this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x], new Rectangle(xOffset + x * 18, yOffset + 58, 16, 16), x));
                }

                // players inventory
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 9; ++x) {
                        this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x + y * 9 + 9], new Rectangle(xOffset + x * 18, yOffset + y * 18, 16, 16), x + y * 9 + 9));
                    }
                }
            }
        };

        int middle = bg_width / 2;
        int x_pos = left + middle + ((middle - 75) / 2);
        price_field = new GuiTextField(fontRendererObj, x_pos, this.top + 20, 75, 10);
        amount_field = new GuiTextField(fontRendererObj, x_pos, this.top + 48, 75, 10);
        sell_btn = new CustomButton(0, amount_field.xPosition, amount_field.yPosition + 16, "Sell", fontRendererObj);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        super.drawScreen(mouse_x, mouse_y, f);
        fakeInventory.draw(mouse_x, mouse_y);

        int middle = bg_width / 2;
        int item_render_width = 60;
        int item_render_x = left + ((middle - item_render_width) / 2);
        // draw item rendering background
        Color color = new Color(64, 0, 62, 200);
        fakeInventory.item_renderer.attempt_gl_reset();
        this.drawGradientRect(item_render_x, top + 10, item_render_x + item_render_width, top + 10 + 60, color.hashCode(), color.hashCode());

        // render selected item
        if (item_to_sale != null) {
            GL11.glPushMatrix();
            {
                float scale = 3;

                GL11.glTranslatef(item_render_x + ((60 - (8 * scale)) / 2) - 4, top + ((60 - (8 * scale)) / 2) + 6, 0);
                GL11.glTranslatef(4, 4, 4);
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(-4, -4, -4);
                fakeInventory.item_renderer.drawItemStack(item_to_sale, 0, 0);
            }
            GL11.glPopMatrix();
        }

        // draw price and amount fields
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        amount_field.drawTextBox();
        price_field.drawTextBox();
        fontRendererObj.drawString("Price: ", price_field.xPosition, price_field.yPosition - 10, 0xFFFFFF);
        fontRendererObj.drawString("Amount: ", amount_field.xPosition, amount_field.yPosition - 10, 0xFFFFFF);

        sell_btn.drawButton(mc, mouse_x, mouse_y);

        if (item_to_sale != null) {
            // TODO: FOr some reason item_to_sale keeps getting set to null. GOtta figure out whats up.
            if (mouse_x >= item_render_x && mouse_x <= item_render_x + item_render_width && mouse_y >= top + 10 && mouse_y <= top + 10 + 60) {
                fakeInventory.item_renderer.renderToolTip(item_to_sale, mouse_x, mouse_y);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if(fakeInventory.getClickedSlot(mouse_x, mouse_y)  != -1) {
            this.slot = fakeInventory.getClickedSlot(mouse_x, mouse_y);
            item_to_sale = fakeInventory.slots.get(this.slot).stack;
        }

        if (item_to_sale == null) amount_field.setText("");

        price_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if (sell_btn.mousePressed(mc, mouse_x, mouse_y)) {
            Minelife.NETWORK.sendToServer(new PacketSellItem(slot, NumberConversions.toInt(amount_field.getText()), NumberConversions.toDouble(price_field.getText())));
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        price_field.updateCursorCounter();
        amount_field.updateCursorCounter();
        sell_btn.enabled = item_to_sale != null && !price_field.getText().isEmpty() && !amount_field.getText().isEmpty() && slot > -1;
        amount_field.setEnabled(item_to_sale != null);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if (i == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
            return;
        }

        super.keyTyped(c, i);

        if (!NumberConversions.isDouble("" + c) && i != Keyboard.KEY_BACK) return;
        price_field.textboxKeyTyped(c, i);

        if (amount_field.isFocused()) {

            amount_field.textboxKeyTyped(c, i);
            if (!amount_field.getText().isEmpty()) {
                if (NumberConversions.toDouble(amount_field.getText()) > item_to_sale.stackSize) {
                    amount_field.setText(amount_field.getText().substring(0, amount_field.getText().length() - 1));
                }
            }
        }

    }
}
