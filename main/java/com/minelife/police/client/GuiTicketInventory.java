package com.minelife.police.client;

import com.minelife.police.TicketInventory;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiTicketInventory extends GuiContainer {

    private TicketInventory ticketInventory;

    public GuiTicketInventory(InventoryPlayer inventoryplayer, TicketInventory ticketInventory) {
        super(new ContainerTicketInventory(inventoryplayer, ticketInventory));
        this.ticketInventory = ticketInventory;
        this.xSize = 176;
        this.ySize = 126;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y) {
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 4, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize, backgroundColor);
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(this.guiLeft, this.guiTop, zLevel);
            for (Object inventorySlot : this.inventorySlots.inventorySlots) {
                Slot slot = ((Slot) inventorySlot);
                this.drawGradientRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, slotBackgroundColor.hashCode(), slotBackgroundColor.hashCode());
            }
        }
        GL11.glPopMatrix();
    }


    private static Color backgroundColor = new Color(0, 63, 126, 255);
    private static Color slotBackgroundColor = new Color(0, 102, 196, 200);
}