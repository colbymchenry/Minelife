package com.minelife.economy.client.gui.cash;

import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;

public class GuiCashBlock extends GuiContainer {

    public GuiCashBlock(IInventory playerInventory, IInventory cashInventory, TileEntityCash tileCash) {
        super(new ContainerCashBlock(playerInventory, cashInventory, tileCash));
        this.allowUserInput = false;
        int inventoryRows = cashInventory.getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
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
}