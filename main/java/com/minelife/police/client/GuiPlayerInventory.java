package com.minelife.police.client;

import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;

import java.io.IOException;

public class GuiPlayerInventory extends GuiContainer {

    private boolean canLoot;

    public GuiPlayerInventory(IInventory playerInventory, IInventory otherInventory, boolean canLoot) {
        super(new ContainerPlayerInventory(playerInventory, otherInventory, canLoot));
        int inventoryRows = otherInventory.getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
        this.canLoot = canLoot;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        this.inventorySlots.inventorySlots.forEach(slot ->
                GuiFakeInventory.drawSlot(this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1, 17, 17));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (canLoot)
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}