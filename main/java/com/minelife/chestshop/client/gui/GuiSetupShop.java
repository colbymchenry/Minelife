package com.minelife.chestshop.client.gui;

import com.minelife.Minelife;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.chestshop.network.PacketSaveShop;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiSetupShop extends GuiScreen {

    private TileEntityChestShop tile;
    private int guiLeft, guiTop, xSize = 176, ySize = 186;
    private GuiFakeInventory fakeInventory;
    private ItemStack stack;
    private GuiTextField priceField, sizeField;
    private GuiButton saveBtn;

    public GuiSetupShop(TileEntityChestShop tile) {
        this.tile = tile;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color(139f / 255f, 139f / 255f, 139f / 255f);
        GuiHelper.drawRect(this.guiLeft + 10, this.guiTop + 10, this.xSize - 100, 70);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();

        if (this.stack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft + 24, this.guiTop + 22, this.zLevel - 200);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.scale(3, 3, 3);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            GuiFakeInventory.renderItemInventory(this.stack, 0, 0, true);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableLighting();
        this.priceField.drawTextBox();
        this.sizeField.drawTextBox();
        this.fontRenderer.drawString(TextFormatting.BOLD + "Price", this.priceField.x, this.priceField.y - 10, 4210752);
        this.fontRenderer.drawString(TextFormatting.BOLD + "Stack Size", this.sizeField.x, this.sizeField.y - 10, 4210752);
        this.fontRenderer.drawString(TextFormatting.BOLD + "Pick an item to sale!", this.guiLeft + 7, this.guiTop + 90, 4210752);
        this.saveBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        GlStateManager.enableLighting();

        this.fakeInventory.drawInventory(mc, mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_BACK) {
            this.priceField.textboxKeyTyped(typedChar, keyCode);
            this.sizeField.textboxKeyTyped(typedChar, keyCode);
            if (this.stack != null) {
                if (NumberConversions.toInt(this.sizeField.getText()) <= 0) this.stack.setCount(1);
                else this.stack.setCount(NumberConversions.toInt(this.sizeField.getText()));
            }
            return;
        }

        if (NumberConversions.isInt(this.priceField.getText() + typedChar)
                && NumberConversions.toInt(this.priceField.getText() + typedChar) > 0) {
            this.priceField.textboxKeyTyped(typedChar, keyCode);
        }

        if (NumberConversions.isInt(this.sizeField.getText() + typedChar) && this.stack != null) {
            if (NumberConversions.toInt(this.sizeField.getText() + typedChar) <= this.stack.getMaxStackSize() &&
                    NumberConversions.toInt(this.sizeField.getText() + typedChar) > 0) {
                this.sizeField.textboxKeyTyped(typedChar, keyCode);
                if (NumberConversions.toInt(this.sizeField.getText()) <= 0) this.stack.setCount(1);
                else this.stack.setCount(NumberConversions.toInt(this.sizeField.getText()));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int slot = this.fakeInventory.mouseClicked(mouseX, mouseY);
        if (slot != -1) {
            ItemStack stack = this.mc.player.inventory.getStackInSlot(slot).copy();
            this.stack = stack.getItem() == Items.AIR ? null : stack;
            if (this.stack != null) this.stack.setCount(1);
        }

        this.priceField.mouseClicked(mouseX, mouseY, mouseButton);
        this.sizeField.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.saveBtn.mousePressed(mc, mouseX, mouseY)) {
            int price = this.priceField.getText().isEmpty() ? 0 : NumberConversions.toInt(this.priceField.getText());
            Minelife.getNetwork().sendToServer(new PacketSaveShop(this.tile, this.stack, price));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.fakeInventory = new GuiFakeInventory(this.guiLeft, this.guiTop + 20, 18, 18, this);
        this.priceField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 100, this.guiTop + 25, 60, 15);
        this.sizeField = new GuiTextField(1, this.fontRenderer, this.guiLeft + 100, this.guiTop + 60, 60, 15);
        this.saveBtn = new GuiButton(0, priceField.x + 30, priceField.y + 55, 40, 20, "Save");
        this.priceField.setText(tile.getPrice() > 0 ? String.valueOf(tile.getPrice()) : "");
        this.stack = tile.getItem();

        if (this.stack != null) this.sizeField.setText(String.valueOf(this.stack.getCount()));
        if (this.stack != null) this.priceField.setText(String.valueOf(tile.getPrice()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.priceField.updateCursorCounter();
        this.sizeField.updateCursorCounter();
        this.priceField.setEnabled(this.stack != null);
        this.sizeField.setEnabled(this.stack != null);
    }
}
