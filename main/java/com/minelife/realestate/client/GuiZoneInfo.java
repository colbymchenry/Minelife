package com.minelife.realestate.client;

import com.minelife.realestate.Zone;
import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public class GuiZoneInfo extends AbstractZoneGui {

    private Content content;
    private Zone zone;

    public GuiZoneInfo(Zone zone)
    {
        super(200, 200);
        this.zone = zone;
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

        private GuiTextField introField, outroField;
        private GuiTickBox allowPlacement, allowBreaking, allowInteracting;
        private CustomZoneBtn saveBtn, membersBtn, boundsBtn;

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            this.introField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, 30, 175, 20);
            this.introField.setMaxStringLength(this.introField.getMaxStringLength() + 2);
            this.outroField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, this.introField.yPosition + 60, 175, 20);
            this.outroField.setMaxStringLength(this.outroField.getMaxStringLength() + 2);

            this.introField.setText(zone.getIntro());
            this.outroField.setText(zone.getOutro());

            int tickboxPosX = width - 50;
            this.allowPlacement = new GuiTickBox(mc, tickboxPosX, this.outroField.yPosition + 70, zone.isPublicPlacing());
            this.allowBreaking = new GuiTickBox(mc, tickboxPosX, this.allowPlacement.yPosition + 30, zone.isPublicBreaking());
            this.allowInteracting = new GuiTickBox(mc, tickboxPosX, this.allowBreaking.yPosition + 30, zone.isPublicInteracting());

            this.saveBtn = new CustomZoneBtn(0, calcX( mc.fontRenderer.getStringWidth("Save") + 15) - this.xPosition, getObjectHeight(0) - 30, mc.fontRenderer.getStringWidth("Save") + 20, 20, "Save");
            this.membersBtn = new CustomZoneBtn(0, this.bounds.getWidth() - 75, this.allowInteracting.yPosition + 50, mc.fontRenderer.getStringWidth("Members") + 20, 20, "Members");
            this.boundsBtn = new CustomZoneBtn(0, 10, this.membersBtn.yPosition, mc.fontRenderer.getStringWidth("View Bounds") + 20, 20, "View Bounds");
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 350;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            mc.fontRenderer.drawString("Intro Message", calcX(mc.fontRenderer.getStringWidth("Intro Message")) - this.xPosition, this.introField.yPosition - 15, 0xFFFFFF);
            this.introField.drawTextBox();
            mc.fontRenderer.drawString("Outro Message", calcX(mc.fontRenderer.getStringWidth("Outro Message")) - this.xPosition, this.outroField.yPosition - 15, 0xFFFFFF);
            this.outroField.drawTextBox();

            mc.fontRenderer.drawString(EnumChatFormatting.UNDERLINE + "Public Permissions", calcX(mc.fontRenderer.getStringWidth("Public Permissions")) - this.xPosition, this.allowPlacement.yPosition - 20, 0xFFFFFF);
            mc.fontRenderer.drawString("Allow Placement", 10, this.allowPlacement.yPosition + 5, 0xFFFFFF);
            this.allowPlacement.draw();
            mc.fontRenderer.drawString("Allow Breaking", 10, this.allowBreaking.yPosition + 5, 0xFFFFFF);
            this.allowBreaking.draw();
            mc.fontRenderer.drawString("Allow Interacting", 10, this.allowInteracting.yPosition + 5, 0xFFFFFF);
            this.allowInteracting.draw();

            this.saveBtn.drawButton(mc, mouseX, mouseY);
            this.membersBtn.drawButton(mc, mouseX, mouseY);
            this.boundsBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            this.introField.mouseClicked(mouseX, mouseY, 0);
            this.outroField.mouseClicked(mouseX, mouseY, 0);

            this.allowPlacement.mouseClicked(mouseX, mouseY);
            this.allowBreaking.mouseClicked(mouseX, mouseY);
            this.allowInteracting.mouseClicked(mouseX, mouseY);

            // TODO: Test
            if(this.membersBtn.mousePressed(mc, mouseX, mouseY))
                Minecraft.getMinecraft().displayGuiScreen(new GuiZoneMembers(zone));
        }

        @Override
        public void keyTyped(char keycode, int keynum)
        {
            super.keyTyped(keycode, keynum);
            this.introField.textboxKeyTyped(keycode, keynum);
            this.outroField.textboxKeyTyped(keycode, keynum);
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
