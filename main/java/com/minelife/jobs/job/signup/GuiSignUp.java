package com.minelife.jobs.job.signup;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.network.PacketJoinProfession;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.LinkedList;

public abstract class GuiSignUp extends GuiScreen {

    private int xPos;
    private int bgWidth = 250;

    private int part = 0;
    private LinkedList<String> parts = Lists.newLinkedList();
    private EnumJob job;

    public GuiSignUp(EnumJob job) {
        this.job = job;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String s = parts.get(part);
        int stringHeight = fontRenderer.listFormattedStringToWidth(s, bgWidth - 10).size() * fontRenderer.FONT_HEIGHT;
        GuiHelper.drawDefaultBackground(xPos, height - stringHeight - 40, bgWidth, stringHeight + fontRenderer.FONT_HEIGHT);
        fontRenderer.drawSplitString(s, xPos + 5, height - stringHeight - 35, bgWidth - 10, 4210752);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
        part++;
        if (part >= parts.size()) part = parts.size() - 1;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0)
            Minelife.getNetwork().sendToServer(new PacketJoinProfession(job));
        else if (button.id == 1)
            mc.player.closeScreen();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        xPos = (this.width - bgWidth) / 2;

        if (parts.isEmpty()) addParts(parts);

        this.buttonList.add(new GuiButton(0, ((this.width - 30) / 2) + 20, height - 75, 30, 20, "Yes"));
        this.buttonList.add(new GuiButton(1, ((this.width - 30) / 2) - 20, height - 75, 30, 20, "No"));

        this.buttonList.get(0).visible = false;
        this.buttonList.get(1).visible = false;
    }

    @Override
    public void updateScreen() {
        this.buttonList.get(0).visible = part == parts.size() - 1;
        this.buttonList.get(1).visible = part == parts.size() - 1;
    }

    public abstract void addParts(LinkedList<String> parts);

}