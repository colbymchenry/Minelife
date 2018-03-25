package com.minelife.chestshop.client.gui;

import com.minelife.Minelife;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.chestshop.network.PacketBuyFromShop;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiBuyShop extends GuiScreen {

    private TileEntityChestShop tile;
    private int guiLeft, guiTop, xSize = 120, ySize = 160;
    private GuiTextField amountField;

    public GuiBuyShop(TileEntityChestShop tile) {
        this.tile = tile;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.amountField.drawTextBox();

        fontRenderer.drawString(TextFormatting.BOLD + "Amount",
                guiLeft + ((xSize - fontRenderer.getStringWidth(TextFormatting.BOLD + "Amount")) / 2), amountField.y - 15, 4210752, false);

        fontRenderer.drawString(TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + "Stock",
                guiLeft + ((xSize - fontRenderer.getStringWidth(TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + "Stock")) / 2),
                guiTop + 10, 4210752, false);

        fontRenderer.drawString("" + NumberConversions.format(tile.getStockCount()),
                guiLeft + ((xSize - fontRenderer.getStringWidth("" + NumberConversions.toInt(tile.getStockCount()))) / 2),
                guiTop + 25, 4210752, false);

        int count = tile.getItem() == null ? 0 : tile.getItem().getCount();
        int total = NumberConversions.toInt(amountField.getText()) * count;
        int price = NumberConversions.toInt(amountField.getText()) * tile.getPrice();

        fontRenderer.drawString("Total: " + NumberConversions.format(total), guiLeft + 6, guiTop + 40, 4210752, false);
        fontRenderer.drawString("Total: $" + NumberConversions.format(price), guiLeft + 6, guiTop + 60, 4210752, false);
        fontRenderer.drawString("Price: $" + NumberConversions.format(tile.getPrice()), guiLeft + 6, guiTop + 80, 4210752, false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_BACK) {
            this.amountField.textboxKeyTyped(typedChar, keyCode);
        } else if (NumberConversions.isInt(this.amountField.getText() + typedChar)
                && NumberConversions.toInt(this.amountField.getText() + typedChar) * tile.getPrice() > 0
                && NumberConversions.toInt(this.amountField.getText() + typedChar) * tile.getItem().getCount() <= tile.getStockCount()) {
            this.amountField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        amountField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (NumberConversions.toInt(this.amountField.getText()) * tile.getPrice() > 0) {
            Minelife.getNetwork().sendToServer(new PacketBuyFromShop(tile.getPos(), NumberConversions.toInt(this.amountField.getText())));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.amountField = new GuiTextField(0, fontRenderer, (this.width - 50) / 2, guiTop + 115, 50, 15);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, (this.width - 30) / 2, this.amountField.y + 20, 30, 20, "Buy"));
        this.buttonList.get(0).enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.amountField.updateCursorCounter();
        this.buttonList.get(0).enabled = !amountField.getText().isEmpty();
    }
}
