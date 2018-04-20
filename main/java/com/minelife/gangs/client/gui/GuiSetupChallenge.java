package com.minelife.gangs.client.gui;

import com.google.common.collect.Lists;
import com.minelife.tdm.Arena;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class GuiSetupChallenge extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 180, guiHeight = 120;
    private GuiTextField moneyField, gangField;
    private GuiGangMenu previousScreen;
    private GuiDropDown dropDown;

    public GuiSetupChallenge(GuiGangMenu previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        this.moneyField.drawTextBox();
        this.gangField.drawTextBox();

        fontRenderer.drawString("Gang", (this.width - fontRenderer.getStringWidth("Gang")) / 2, gangField.y - 12, GuiHelper.defaultTextColor);
        fontRenderer.drawString("Bet Amount", (this.width - fontRenderer.getStringWidth("Bet Amount")) / 2, moneyField.y - 12, GuiHelper.defaultTextColor);
        super.drawScreen(mouseX, mouseY, partialTicks);
        dropDown.draw(mc, mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(previousScreen);
            return;
        }

        if (!StringHelper.containsSpecialChar("" + typedChar))
            gangField.textboxKeyTyped(typedChar, keyCode);

        if (NumberConversions.isInt("" + typedChar) || keyCode == Keyboard.KEY_BACK
                && NumberConversions.isLong(moneyField.getText() + typedChar))
            moneyField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(!dropDown.drop_down_active) {
            gangField.mouseClicked(mouseX, mouseY, mouseButton);
            moneyField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        dropDown.mouseClicked(mc, mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;
        this.gangField = new GuiTextField(0, fontRenderer, (this.width - 100) / 2, guiTop + 20, 100, 15);
        this.moneyField = new GuiTextField(0, fontRenderer, (this.width - 100) / 2, guiTop + 60, 100, 15);
        buttonList.clear();
        buttonList.add(new GuiButton(0, (this.width - 75) / 2, guiTop + guiHeight - 28, 75, 20, TextFormatting.RED + "Challenge!"));
        List<String> arenaNames = Lists.newArrayList();
        Arena.ARENAS.forEach(arena -> arenaNames.add(arena.getName()));
        String[] array = arenaNames.toArray(new String[arenaNames.size()]);
        this.dropDown = new GuiDropDown((this.width - 100) / 2, guiTop + 80, 100, 15, array);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        moneyField.updateCursorCounter();
        gangField.updateCursorCounter();
    }
}
