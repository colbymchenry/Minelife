package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.packet.PacketBuyItem;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BuyItemGui extends GuiScreen {

    private ItemListing listing;
    private MLItemRenderer item_renderer;
    private int left, top, bg_width, bg_height;
    private CustomButton buy_btn, cancel_btn;
    private long price_per_item;
    private GuiTextField amount_field;

    public BuyItemGui(ItemListing listing)
    {
        this.listing = listing;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        this.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(this.left - 5, this.top - 5, this.bg_width + 7, this.bg_height + 7, new Color(0x8F008D));

        // draw item rendering background
        Color color = new Color(64, 0, 62, 200);
        item_renderer.attempt_gl_reset();
        this.drawGradientRect(left + 4, top + 4, left + 4 + 60, top + 4 + 60, color.hashCode(), color.hashCode());

        item_renderer.attempt_gl_reset();

        GL11.glPushMatrix();
        {
            float scale = 3;

            GL11.glTranslatef(left + ((60 - (8 * scale)) / 2), top + ((60 - (8 * scale)) / 2), 0);
            GL11.glTranslatef(4, 4, 4);
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(-4, -4, -4);
            item_renderer.drawItemStack(listing.item_stack(), 0, 0, null);
        }
        GL11.glPopMatrix();
        item_renderer.renderToolTip(listing.item_stack(), left + 60, top + 20, width, height);
        this.buy_btn.drawButton(mc, mouse_x, mouse_y);
        this.cancel_btn.drawButton(mc, mouse_x, mouse_y);

        fontRendererObj.drawString("1x = $" + NumberConversions.formatter.format(this.price_per_item), left + 4, top + 68, 0xFFFFFF);
        fontRendererObj.drawString((amount_field.getText().isEmpty() ? 0 : NumberConversions.toLong(amount_field.getText())) + "x = $" + NumberConversions.formatter.format(this.price_per_item * this.amount_field_value()), left + 4, top + 78, 0xFFFFFF);

        fontRendererObj.drawString("Amount:", this.amount_field.xPosition - 40, this.amount_field.yPosition + 1, 0xFFFFFF);
        this.amount_field.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        if (buy_btn.mousePressed(mc, mouse_x, mouse_y)) {
            Minelife.NETWORK.sendToServer(new PacketBuyItem(listing.uuid(), NumberConversions.toInt(amount_field.getText())));
        } else if (cancel_btn.mousePressed(mc, mouse_x, mouse_y)) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
        }

        this.amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if (i == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
            return;
        }

        if(i == Keyboard.KEY_BACK) {
            this.amount_field.textboxKeyTyped(c, i);
            return;
        }

        if(!NumberConversions.isLong("" + c)) return;

        if(Long.parseLong(this.amount_field.getText() + c) <= this.listing.item_stack().stackSize) {
            this.amount_field.textboxKeyTyped(c, i);
        }
        super.keyTyped(c, i);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.item_renderer = new MLItemRenderer(mc);
        int[] tool_tip_bounds = this.item_renderer.getToolTipBounds(listing.item_stack(), this.width / 2, this.height / 2, this.width, this.height);
        bg_width = (tool_tip_bounds[0]) + 80;
        bg_height = 100 + (tool_tip_bounds[1] > 60 ? tool_tip_bounds[1] - 60 : 0);
        bg_height += 50;
        left = (this.width - bg_width) / 2;
        top = (this.height - bg_height) / 2;
        int middle = bg_width / 2;
        int buy_width = fontRendererObj.getStringWidth("Buy") + 15;
        int cancel_width = fontRendererObj.getStringWidth("Cancel") + 15;
        this.buy_btn = new CustomButton(0, left + ((middle - buy_width) / 2), top + bg_height - 25, "Buy", fontRendererObj);
        this.cancel_btn = new CustomButton(1, left + middle + ((middle - cancel_width) / 2), top + bg_height - 25, "Cancel", fontRendererObj);
        this.price_per_item = listing.price() / listing.item_stack().stackSize;
        this.amount_field = new GuiTextField(fontRendererObj, left + 4 + fontRendererObj.getStringWidth("Amount: ") + 1, top + 98, 20, 10);

        this.buy_btn.enabled = false;
    }

    @Override
    public void updateScreen()
    {
        buy_btn.enabled = !amount_field.getText().isEmpty() && NumberConversions.toLong(amount_field.getText()) > 0;
    }

    private long amount_field_value() {
        if(this.amount_field.getText().isEmpty()) return 0;
        if(!NumberConversions.isLong(this.amount_field.getText())) return 0;
        return NumberConversions.toLong(this.amount_field.getText());
    }
}
