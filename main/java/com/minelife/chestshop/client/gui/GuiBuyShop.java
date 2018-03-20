package com.minelife.chestshop.client.gui;

import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiBuyShop extends GuiScreen {

    private TileEntityChestShop tile;
    private int guiLeft, guiTop, xSize = 100, ySize = 80;
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
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_BACK) {
            this.amountField.textboxKeyTyped(typedChar, keyCode);
        } else if (NumberConversions.isInt(this.amountField.getText() + typedChar)) {
            this.amountField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {

    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.amountField = new GuiTextField(0, fontRenderer, (this.width - 50) / 2, (this.height - 15) / 2, 50, 15);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, (this.width - 30) / 2, this.amountField.y + 20, 30, 20, "Buy"));
    }
}
