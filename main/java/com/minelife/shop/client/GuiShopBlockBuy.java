package com.minelife.shop.client;

import com.minelife.Minelife;
import com.minelife.shop.TileEntityShopBlock;
import com.minelife.shop.network.PacketBuyFromShop;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiShopBlockBuy extends GuiScreen {

    private int xPosition, yPosition, bgWidth = 100, bgHeight = 100;
    private TileEntityShopBlock tile_entity;
    private GuiButton buy_btn;
    private GuiTextField amount_field;
    private MLItemRenderer itemRenderer;
    private ItemStack itemToRender;

    public GuiShopBlockBuy(TileEntityShopBlock tile_entity) {
        this.tile_entity = tile_entity;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        buy_btn.drawButton(mc, mouse_x, mouse_y);
        amount_field.drawTextBox();
        if (itemToRender != null) {
            GL11.glPushMatrix();
            itemRenderer.attempt_gl_reset();
            GL11.glTranslatef(xPosition + ((bgWidth - 24) / 2), yPosition + 20, zLevel);
            GL11.glTranslatef(6, 6, zLevel);
            GL11.glScalef(2, 2, 2);
            GL11.glTranslatef(-6, -6, zLevel);
            itemRenderer.drawItemStack(itemToRender, 0, 0);
            GL11.glPopMatrix();

            if (mouse_x >= xPosition + ((bgWidth - 24) / 2) - 6 && mouse_x <= xPosition + ((bgWidth - 24) / 2) + 24 &&
                    mouse_y >= yPosition + 20 - 6 && mouse_y <= yPosition + 20 + 24) {
                itemRenderer.renderToolTip(itemToRender, mouse_x, mouse_y);
            }
        }
    }

    @Override
    protected void keyTyped(char key_char, int key_code) {
        super.keyTyped(key_char, key_code);
        if (key_code == Keyboard.KEY_BACK || NumberConversions.isInt("" + key_char)) {
            if (NumberConversions.isInt(amount_field.getText() + key_char) &&
                    NumberConversions.toInt(amount_field.getText() + key_char) <= tile_entity.getStackToSale().stackSize &&
                    NumberConversions.toInt(amount_field.getText() + key_char) > 0) {
                amount_field.textboxKeyTyped(key_char, key_code);
            } else if (key_code == Keyboard.KEY_BACK) {
                amount_field.textboxKeyTyped(key_char, key_code);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        amount_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
        if (buy_btn.mousePressed(mc, mouse_x, mouse_y) && NumberConversions.isInt(amount_field.getText())) {
            Minelife.NETWORK.sendToServer(new PacketBuyFromShop(tile_entity.xCoord, tile_entity.yCoord, tile_entity.zCoord, NumberConversions.toInt(amount_field.getText())));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - bgWidth) / 2;
        yPosition = (this.height - bgHeight) / 2;
        amount_field = new GuiTextField(fontRendererObj, xPosition + 7, yPosition + bgHeight - 25, 52, 20);
        buy_btn = new GuiButton(0, amount_field.xPosition + amount_field.width + 6, amount_field.yPosition, 30, 20, "Buy");

        amount_field.drawTextBox();
        buy_btn.enabled = false;
        itemRenderer = new MLItemRenderer(mc);

        if (tile_entity.getStackToSale() != null) {
            itemToRender = tile_entity.getStackToSale().copy();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        amount_field.updateCursorCounter();
        buy_btn.enabled = !amount_field.getText().isEmpty();
    }
}
