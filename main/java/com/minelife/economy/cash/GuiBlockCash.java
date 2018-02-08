package com.minelife.economy.cash;

import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiBlockCash extends GuiContainer {

    private TileEntityCash TileCash;

    private Color slotColor = new Color(139, 139, 139, 255);

    public GuiBlockCash(InventoryPlayer PlayerInventory, TileEntityCash TileCash) {
        super(new ContainerBlockCash(PlayerInventory, TileCash));
        this.TileCash = TileCash;
        this.xSize = 176;

        int inventoryRows = TileCash.getInventory().getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String s = "Cash Pile";
        this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, 128, 4210752);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        for (Object s : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) s;
            GuiUtil.drawSlot(this.guiLeft + slot.xDisplayPosition - 1, this.guiTop + slot.yDisplayPosition - 1, 17, 17, slotColor);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
