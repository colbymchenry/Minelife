package com.minelife.realestate.client.gui;

import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;

// TODO
public class GuiSell extends BaseGui {

    private Content content;

    public GuiSell()
    {
        super(200, 200);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        content.draw(mouseX, mouseY, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        content.keyTyped(c, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
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
        content = new Content(this.xPosition, this.yPosition, this.bgWidth, this.bgHeight);
    }

    @Override
    public void updateScreen()
    {
    }

    private class Content extends GuiScrollList {

        private GuiTextField titleField, priceField;
        private com.minelife.util.client.GuiTextField description;
        private GuiTickBox forRent, allowPlacement, allowBreaking, allowGuests;
        private CustomButton sellBtn;

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            this.titleField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, 30, 175, 20);
            this.titleField.setMaxStringLength(this.titleField.getMaxStringLength() + 2);

            this.description = new com.minelife.util.client.GuiTextField(calcX(175) - this.xPosition, this.titleField.yPosition + 50, 175, 50);
            this.priceField = new GuiTextField(mc.fontRenderer, calcX(100) - this.xPosition, this.description.getBounds().getY() + this.description.getBounds().getHeight() + 30, 100, 20);

            int tickboxPosX = width - 50;
            this.forRent = new GuiTickBox(mc, tickboxPosX, this.priceField.yPosition + 50, false);
            this.allowPlacement = new GuiTickBox(mc, tickboxPosX, this.forRent.yPosition + 30, false);
            this.allowBreaking = new GuiTickBox(mc, tickboxPosX, this.allowPlacement.yPosition + 30, false);
            this.allowGuests = new GuiTickBox(mc, tickboxPosX, this.allowBreaking.yPosition + 30, false);

            this.sellBtn = new CustomButton(0, (width - mc.fontRenderer.getStringWidth("Sell") + 4) / 2, getObjectHeight(0) - 30, mc.fontRenderer.getStringWidth("Sell") + 20, 20, "Sell");
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 375;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            mc.fontRenderer.drawString("Title", calcX(mc.fontRenderer.getStringWidth("Title")) - this.xPosition, this.titleField.yPosition - 15, 0xFFFFFF);
            this.titleField.drawTextBox();
            mc.fontRenderer.drawString("Price", calcX(mc.fontRenderer.getStringWidth("Price")) - this.xPosition, this.priceField.yPosition - 15, 0xFFFFFF);
            this.priceField.drawTextBox();
            mc.fontRenderer.drawString("Description", calcX(mc.fontRenderer.getStringWidth("Description")) - this.xPosition, this.description.getBounds().getY() - 15, 0xFFFFFF);
            this.description.drawTextBox();

            mc.fontRenderer.drawString("For Rent", 10, this.forRent.yPosition + 5, 0xFFFFFF);
            this.forRent.draw();
            mc.fontRenderer.drawString("Allow Placement", 10, this.allowPlacement.yPosition + 5, 0xFFFFFF);
            this.allowPlacement.draw();
            mc.fontRenderer.drawString("Allow Breaking", 10, this.allowBreaking.yPosition + 5, 0xFFFFFF);
            this.allowBreaking.draw();
            mc.fontRenderer.drawString("Allow Guests", 10, this.allowGuests.yPosition + 5, 0xFFFFFF);
            this.allowGuests.draw();

            this.sellBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            this.titleField.mouseClicked(mouseX, mouseY, 0);
            this.priceField.mouseClicked(mouseX, mouseY, 0);
            this.description.mouseClicked(mouseX, mouseY);

            this.forRent.mouseClicked(mouseX, mouseY);
            this.allowPlacement.mouseClicked(mouseX, mouseY);
            this.allowBreaking.mouseClicked(mouseX, mouseY);
            this.allowGuests.mouseClicked(mouseX, mouseY);

            if(sellBtn.mousePressed(mc, mouseX, mouseY))
            {

            }
        }

        @Override
        public void keyTyped(char keycode, int keynum)
        {
            super.keyTyped(keycode, keynum);
            this.titleField.textboxKeyTyped(keycode, keynum);
            this.priceField.textboxKeyTyped(keycode, keynum);
            this.description.textboxKeyTyped(keycode, keynum);
        }

        @Override
        public void drawBackground()
        {
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {
        }

    }

}
