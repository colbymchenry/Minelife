package com.minelife.gangs.client.gui;

import com.minelife.Minelife;
import com.minelife.gangs.network.PacketAddMember;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiAddMember extends GuiScreen {

    private GuiGangMenu previousScreen;
    private int guiLeft, guiTop, guiWidth = 100, guiHeight = 60;
    private GuiTextField playerField;

    public GuiAddMember(GuiGangMenu previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        playerField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(previousScreen);
        } else {
            playerField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        playerField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Minelife.getNetwork().sendToServer(new PacketAddMember(previousScreen.gang.getUniqueID(), playerField.getText()));
        mc.displayGuiScreen(previousScreen);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
        playerField = new GuiTextField(0, fontRenderer, guiLeft + 5, guiTop + 5, guiWidth - 10, 20);
        buttonList.clear();
        buttonList.add(new GuiButton(0, (this.width - 30) / 2, guiTop + guiHeight - 25, 30, 20, "ADD"));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        playerField.updateCursorCounter();
        buttonList.get(0).enabled = !playerField.getText().isEmpty();
    }
}
