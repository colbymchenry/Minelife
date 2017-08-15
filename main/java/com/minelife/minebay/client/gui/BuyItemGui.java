package com.minelife.minebay.client.gui;

import com.minelife.minebay.ItemListing;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class BuyItemGui extends GuiScreen {

    private ItemListing listing;
    private MLItemRenderer item_renderer;
    private int left, top, bg_width, bg_height;
    private float rotY = 0F;
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
        item_renderer.renderItem3D(listing.item_stack(), left + 35, top + 34, 30, rotY += 0.5f);
        item_renderer.renderToolTip(listing.item_stack(), left + 60, top + 20, width, height);
        this.buy_btn.drawButton(mc, mouse_x, mouse_y);
        this.cancel_btn.drawButton(mc, mouse_x, mouse_y);

        fontRendererObj.drawString("Cost Per Item: $" + NumberConversions.formatter.format(this.price_per_item), left + 4, top + 68, 0xFFFFFF);
        this.amount_field.drawTextBox();
        fontRendererObj.drawString("Amount:", this.amount_field.xPosition - 40, this.amount_field.yPosition + 1, 0xFFFFFF);
        fontRendererObj.drawString("Total: $" + NumberConversions.formatter.format(this.price_per_item * this.amount_field_value()), left + 4, top + 78, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        if (buy_btn.mousePressed(mc, mouse_x, mouse_y)) {

        } else if (cancel_btn.mousePressed(mc, mouse_x, mouse_y)) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
        }

        this.amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        if (p_73869_2_ == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
            return;
        }
        this.amount_field.textboxKeyTyped(p_73869_1_, p_73869_2_);
        super.keyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.item_renderer = new MLItemRenderer(mc);
        int[] tool_tip_bounds = this.item_renderer.getToolTipBounds(listing.item_stack(), this.width / 2, this.height / 2, this.width, this.height);
        bg_width = (tool_tip_bounds[0]) + 80;
        bg_height = 100 + (tool_tip_bounds[1] > 60 ? tool_tip_bounds[1] - 60 : 0);
        bg_height += 80;
        left = (this.width - bg_width) / 2;
        top = (this.height - bg_height) / 2;
        int middle = bg_width / 2;
        int buy_width = fontRendererObj.getStringWidth("Buy") + 15;
        int cancel_width = fontRendererObj.getStringWidth("Cancel") + 15;
        this.buy_btn = new CustomButton(0, left + ((middle - buy_width) / 2), top + bg_height - 25, "Buy", fontRendererObj);
        this.cancel_btn = new CustomButton(1, left + middle + ((middle - cancel_width) / 2), top + bg_height - 25, "Cancel", fontRendererObj);
        this.price_per_item = listing.price() / listing.item_stack().stackSize;
        this.amount_field = new GuiTextField(fontRendererObj, left + ((bg_width - 20) / 2) + 30, top + 88, 20, 10);
    }

    private long amount_field_value() {
        if(this.amount_field.getText().isEmpty()) return 0;
        if(!NumberConversions.isLong(this.amount_field.getText())) return 0;
        return NumberConversions.toLong(this.amount_field.getText());
    }
}
