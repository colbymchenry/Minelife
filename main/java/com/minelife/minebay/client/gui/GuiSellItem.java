package com.minelife.minebay.client.gui;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.minebay.network.PacketSellItem;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

public class GuiSellItem extends GuiMinebay {

    private GuiFakeInventory fakeInventory;
    private ItemStack itemToSale;
    private Map<Integer, ItemStack> stacksWithinInventory = Maps.newHashMap();
    private GuiTextField priceField, storageField, stackSizeField;
    private GuiButton sellBtn;

    @Override
    public void initGui() {
        super.initGui();

        fakeInventory = new GuiFakeInventory(guiLeft, guiTop, 18, 18, this);
        fakeInventory.slotColor = 0x930093;

        int middle = xSize / 2;
        int x_pos = guiLeft + middle + ((middle - 75) / 2);
        priceField = new GuiTextField(0, fontRenderer, x_pos, this.guiTop + 20, 85, 10);
        storageField = new GuiTextField(1, fontRenderer, x_pos, this.guiTop + 48, 85, 10);
        stackSizeField = new GuiTextField(2, fontRenderer, x_pos, this.guiTop + 76, 85, 10);
        sellBtn = new GuiMinebayBtn(0, storageField.x, stackSizeField.y + 16, "Sell", fontRenderer);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        fakeInventory.drawInventory(mc, mouse_x, mouse_y);

        RenderHelper.enableGUIStandardItemLighting();

        int middle = xSize / 2;
        int item_render_width = 60;
        int item_render_x = guiLeft + ((middle - item_render_width) / 2);
        // draw item rendering background
        Color color = new Color(64, 0, 62, 200);
        this.drawGradientRect(item_render_x, guiTop + 10, item_render_x + item_render_width, guiTop + 10 + 60, color.hashCode(), color.hashCode());

        // render selected item
        if (itemToSale != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft + 54, this.guiTop + 17, this.zLevel - 200);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.scale(3, 3, 3);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            GuiFakeInventory.renderItemInventory(this.itemToSale, 0, 0, false);
            GlStateManager.popMatrix();
        }

        // draw price and amount fields
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        storageField.drawTextBox();
        priceField.drawTextBox();
        stackSizeField.drawTextBox();
        fontRenderer.drawString("Price per Item: ", priceField.x, priceField.y - 10, 0xFFFFFF);
        fontRenderer.drawString("Amount to Store: ", storageField.x, storageField.y - 10, 0xFFFFFF);
        fontRenderer.drawString("Stack Size: ", stackSizeField.x, stackSizeField.y - 10, 0xFFFFFF);

        RenderHelper.enableGUIStandardItemLighting();
        sellBtn.drawButton(mc, mouse_x, mouse_y, f);

        if (itemToSale != null) {
            if (mouse_x >= item_render_x && mouse_x <= item_render_x + item_render_width && mouse_y >= guiTop + 10 && mouse_y <= guiTop + 10 + 60) {
                fakeInventory.renderToolTip(itemToSale, mouse_x, mouse_y);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) throws IOException {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if (fakeInventory.mouseClicked(mouse_x, mouse_y) != -1) {
            itemToSale = mc.player.inventory.getStackInSlot(fakeInventory.mouseClicked(mouse_x, mouse_y)).copy();
            if (itemToSale != null) {
                itemToSale.setCount(1);
                populateStacksList();
            }
        }

        if (itemToSale == null) storageField.setText("");

        priceField.mouseClicked(mouse_x, mouse_y, mouse_btn);
        storageField.mouseClicked(mouse_x, mouse_y, mouse_btn);
        stackSizeField.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if (sellBtn.mousePressed(mc, mouse_x, mouse_y)) {
            Minelife.getNetwork().sendToServer(new PacketSellItem(itemToSale, NumberConversions.toInt(storageField.getText()),
                    NumberConversions.toInt(priceField.getText()), NumberConversions.toInt(stackSizeField.getText())));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        priceField.updateCursorCounter();
        storageField.updateCursorCounter();
        stackSizeField.updateCursorCounter();
        sellBtn.enabled = itemToSale != null && !priceField.getText().isEmpty() && !storageField.getText().isEmpty()
                && NumberConversions.isInt(storageField.getText()) && NumberConversions.isInt(priceField.getText()) &&
                NumberConversions.toInt(storageField.getText()) > 0 && NumberConversions.toInt(stackSizeField.getText()) > 0 &&
                NumberConversions.toInt(storageField.getText()) >= NumberConversions.toInt(stackSizeField.getText());
        storageField.setEnabled(itemToSale != null);
        stackSizeField.setEnabled(itemToSale != null && !storageField.getText().isEmpty());
    }

    @Override
    protected void keyTyped(char key_char, int key_id) throws IOException {
        if (key_id == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiItemListings());
            return;
        }

        super.keyTyped(key_char, key_id);

        if (key_id == Keyboard.KEY_BACK || priceField.isFocused() && NumberConversions.isInt(priceField.getText() + key_char))
            priceField.textboxKeyTyped(key_char, key_id);

        if (itemToSale == null) return;

        if ((key_id == Keyboard.KEY_BACK || NumberConversions.isInt(String.valueOf(key_char))) &&
                NumberConversions.toInt(storageField.getText() + String.valueOf(key_char)) <= getTotalStacks()) {
            storageField.textboxKeyTyped(key_char, key_id);
        }

        if ((key_id == Keyboard.KEY_BACK || NumberConversions.isInt(String.valueOf(key_char))) &&
                NumberConversions.toInt(stackSizeField.getText() + String.valueOf(key_char)) <= itemToSale.getMaxStackSize() &&
                NumberConversions.toInt(stackSizeField.getText() + String.valueOf(key_char)) <= NumberConversions.toInt(storageField.getText()))
            stackSizeField.textboxKeyTyped(key_char, key_id);
    }

    private void populateStacksList() {
        stacksWithinInventory = Maps.newHashMap();

        if (itemToSale == null) return;

        for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && ItemHelper.areStacksIdentical(stack, itemToSale))
                stacksWithinInventory.put(i, stack);
        }
    }

    private int getTotalStacks() {
        int total = 0;
        for (ItemStack stack : stacksWithinInventory.values()) total += stack.getCount();
        return total;
    }

}