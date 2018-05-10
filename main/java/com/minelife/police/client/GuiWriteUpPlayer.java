package com.minelife.police.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.police.ChargeType;
import com.minelife.police.Prisoner;
import com.minelife.police.network.PacketWriteup;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiWriteUpPlayer extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 230, guiHeight = 230;
    private UUID playerID;
    private Map<ChargeType, GuiTextField> charges = Maps.newHashMap();

    public GuiWriteUpPlayer(UUID playerID) {
        this.playerID = playerID;
        this.guiWidth = 230;
    }

    // TODO: Finish write up gui

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(77f / 255f, 77f / 255f, 77f / 255f, 200f / 255f);
        GuiHelper.drawRect(guiLeft, guiTop, guiWidth, guiHeight);
        GlStateManager.color(22f / 255f, 22f / 255f, 22f / 255f, 200f / 255f);
        GuiHelper.drawRect(guiLeft + 2, guiTop + 2, guiWidth - 4, guiHeight - 4);
        GlStateManager.enableTexture2D();

        GlStateManager.color(1, 1, 1, 1);
        drawCenteredString(fontRenderer, TextFormatting.UNDERLINE + NameFetcher.asyncFetchClient(playerID), width / 2, guiTop + 10, 0xFFFFFF);
        charges.forEach(((chargeType, guiTextField) -> {
            fontRenderer.drawString(WordUtils.capitalizeFully(chargeType.name().replace("_", " ")), guiLeft + 10, guiTextField.y + 2, 0xFFFFFF, true);
            guiTextField.drawTextBox();
        }));

        Map<ChargeType, Integer> totals = Maps.newHashMap();
        charges.forEach((chargeType, textField) -> {
            if (!textField.getText().isEmpty()) {
                totals.put(chargeType, NumberConversions.toInt(textField.getText()));
            }
        });

        long totalBailAmount = 0;
        for (ChargeType chargeType : totals.keySet())
            totalBailAmount += chargeType.chargeAmount * totals.get(chargeType);
        long totalSentenceTime = totalBailAmount / 5;

        fontRenderer.drawString("Total Bail: $" + NumberConversions.format(totalBailAmount), guiLeft + 10, guiTop + 180, 0xFFFFFF);
        fontRenderer.drawString("Total Time: " + NumberConversions.format(totalSentenceTime) + " minutes", guiLeft + 10, guiTop + 195, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.ITALIC + "Press (Enter) to create the ticket", width / 2, guiTop + guiHeight - 14, 0xFFFFFF);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if(keyCode == Keyboard.KEY_RETURN) {
            List<ChargeType> charges = Lists.newArrayList();
            this.charges.forEach((chargeType, textField) -> {
                if (!textField.getText().isEmpty())
                    for (int i = 0; i < NumberConversions.toInt(textField.getText()); i++) charges.add(chargeType);
            });
            Minelife.getNetwork().sendToServer(new PacketWriteup(playerID, charges));
        } else {
            if (NumberConversions.isInt("" + typedChar) || keyCode == Keyboard.KEY_BACK)
                charges.forEach(((chargeType, guiTextField) -> guiTextField.textboxKeyTyped(typedChar, keyCode)));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        charges.forEach(((chargeType, guiTextField) -> guiTextField.mouseClicked(mouseX, mouseY, mouseButton)));
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight) / 2;

        int y = guiTop + 10;
        for (ChargeType chargeType : ChargeType.values())
            charges.put(chargeType, new GuiTextField(0, fontRenderer, guiLeft + guiWidth - 40, y += 20, 30, 15));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        charges.forEach(((chargeType, guiTextField) -> guiTextField.updateCursorCounter()));
    }
}
