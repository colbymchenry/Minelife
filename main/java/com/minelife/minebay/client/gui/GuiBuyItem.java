package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.network.PacketBuyItem;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiBuyItem extends GuiMinebay {

    private ItemListing itemListing;
    private GuiMinebayBtn buyBtn;
    private GuiTextField amountField;

    public GuiBuyItem(ItemListing itemListing) {
        this.itemListing = itemListing;
        this.xSize = 160;
        this.ySize = 180;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.color(64 / 255f, 0, 62 / 255f, 188f/255f);
        GuiHelper.drawRect(guiLeft + 48, guiTop + 2, 60, 60);
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft + 55, this.guiTop + 7, this.zLevel - 200);
        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.scale(3, 3, 3);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        GuiFakeInventory.renderItemInventory(this.itemListing.getItemStack(), 0, 0, true);
        GlStateManager.popMatrix();

        amountField.drawTextBox();

        drawCenteredString(fontRenderer, "Storage: " + NumberConversions.format(itemListing.getAmountStored()), width / 2, guiTop + 70, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("Amount: ", amountField.x - 40, amountField.y + 4, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Price for 1 = $" + NumberConversions.format(itemListing.getPrice()), width / 2, guiTop + 85, 0xFFFFFF);
        int amount = amountField.getText().isEmpty() ? 0 : NumberConversions.toInt(amountField.getText());
        drawCenteredString(fontRenderer, "Total: $" + NumberConversions.format(amount * itemListing.getPrice()), width / 2, guiTop + 125, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Total: x" + (amount * itemListing.getItemStack().getCount()), width / 2, guiTop + 140, 0xFFFFFF);

        this.buyBtn.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiItemListings());
            return;
        }
        if((keyCode == Keyboard.KEY_BACK || NumberConversions.isInt(amountField.getText() + typedChar))
                && NumberConversions.toInt(amountField.getText() + typedChar) > 0 &&
                NumberConversions.toInt(amountField.getText() + typedChar) * itemListing.getItemStack().getCount() <= itemListing.getAmountStored()) {
            amountField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        amountField.mouseClicked(mouseX, mouseY, mouseButton);

        if(this.buyBtn.mousePressed(mc, mouseX, mouseY)) {
            Minelife.getNetwork().sendToServer(new PacketBuyItem(NumberConversions.toInt(amountField.getText()), itemListing.getUniqueID()));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        amountField = new GuiTextField(0, fontRenderer, guiLeft + 73, guiTop + 100, 50, 15);
        this.buyBtn = new GuiMinebayBtn(0, (this.width - 30) / 2, this.guiTop + this.ySize - 25, "Buy", fontRenderer);
        this.buyBtn.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        amountField.updateCursorCounter();
        this.buyBtn.enabled = !this.amountField.getText().isEmpty();
    }
}
