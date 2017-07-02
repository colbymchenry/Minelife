package com.minelife.realestate.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

// TODO
public class GuiSell extends BaseGui {

    private GuiTextField priceField;

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        this.priceField.drawTextBox();
        super.drawScreen(mouseX, mouseY, f);
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        this.priceField.textboxKeyTyped(c, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
        this.priceField.mouseClicked(x, y, btn);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.priceField = new GuiTextField(mc.fontRenderer, calcX(50), calcY(20), 50, 20);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, calcX(50), calcY(20) + 40, 50, 20, "Sell"));
    }

    @Override
    public void updateScreen()
    {
        this.priceField.updateCursorCounter();
    }

}
