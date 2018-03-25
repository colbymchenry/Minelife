package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.network.PacketDeleteListing;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiDeleteListing extends GuiMinebay {

    private ItemListing listing;
    private GuiMinebayBtn yesBtn, noBtn;

    public GuiDeleteListing(ItemListing listing) {
        this.listing = listing;
        xSize = 120;
        ySize = 60;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(fontRenderer, "Delete item listing?", width / 2, height / 2, 0xFFFFFF);
        this.yesBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        this.noBtn.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.yesBtn.mousePressed(mc, mouseX, mouseY)) {
            Minelife.getNetwork().sendToServer(new PacketDeleteListing(listing.getUniqueID()));
            mc.displayGuiScreen(new GuiItemListings());
        } else if (this.noBtn.mousePressed(mc, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiItemListings());
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiItemListings());
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.yesBtn = new GuiMinebayBtn(0, guiLeft + (((xSize / 2) - 30) / 2), guiTop + ySize - 25, "Yes", fontRenderer);
        this.noBtn = new GuiMinebayBtn(0, guiLeft +  (xSize / 2) + (((xSize / 2) - 30) / 2), guiTop + ySize - 25, "No", fontRenderer);
    }
}
