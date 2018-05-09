package com.minelife.police.client;

import com.google.common.collect.Maps;
import com.minelife.police.ChargeType;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class GuiWriteUpPlayer extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 200, guiHeight = 230;
    private UUID playerID;
    private Map<ChargeType, GuiTextField> charges = Maps.newHashMap();

    public GuiWriteUpPlayer(UUID playerID) {
        this.playerID = playerID;
    }

    // TODO: Finish write up gui

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableTexture2D();
        GlStateManager.color(77f/255f, 77f/255f, 77f/255f, 128f/255f);
        GuiHelper.drawRect(guiLeft, guiTop, guiWidth, guiHeight);
        GlStateManager.color(1, 1, 1, 1);
        charges.forEach(((chargeType, guiTextField) -> {
            fontRenderer.drawString(WordUtils.capitalizeFully(chargeType.name().replace("_", " ")), guiLeft + 10, guiTextField.y + 2, 0xFFFFFF, true);
            guiTextField.drawTextBox();
        }));
        GlStateManager.enableTexture2D();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        charges.forEach(((chargeType, guiTextField) -> guiTextField.textboxKeyTyped(typedChar, keyCode)));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        charges.forEach(((chargeType, guiTextField) -> guiTextField.mouseClicked(mouseX, mouseY, mouseButton)));
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) /2;
        guiTop = (this.height - guiHeight) / 2;

        int y = guiTop;
        for (ChargeType chargeType : ChargeType.values())
            charges.put(chargeType, new GuiTextField(0, fontRenderer, guiLeft + guiWidth - 60, y += 20, 50, 15));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        charges.forEach(((chargeType, guiTextField) -> guiTextField.updateCursorCounter()));
    }
}
