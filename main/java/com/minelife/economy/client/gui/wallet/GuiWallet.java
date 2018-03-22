package com.minelife.economy.client.gui.wallet;

import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiWallet extends GuiContainer {

    private InventoryWallet inventoryWallet;

    public GuiWallet(IInventory playerInventory, InventoryWallet inventoryWallet) {
        super(new ContainerWallet(playerInventory, inventoryWallet));
        this.inventoryWallet = inventoryWallet;
        this.allowUserInput = false;
        int inventoryRows = inventoryWallet.getInventory().getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        this.inventorySlots.inventorySlots.forEach(slot ->
                GuiFakeInventory.drawSlot(this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1, 17, 17));
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if(slotIn == null || slotIn.getStack() == null || inventoryWallet.getWalletStack() == null || ItemStack.areItemStacksEqual(slotIn.getStack(), inventoryWallet.getWalletStack()))
            return;
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
