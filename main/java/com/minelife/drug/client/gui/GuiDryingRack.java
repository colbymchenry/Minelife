package com.minelife.drug.client.gui;

import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDryingRack extends GuiContainer {

    private TileEntityDryingRack tile_drying_rack;
    private static ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/drying_rack.png");

    public GuiDryingRack(InventoryPlayer inventoryplayer, TileEntityDryingRack tile) {
        super(new ContainerDryingRack(inventoryplayer, tile));
        this.tile_drying_rack = tile;
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
        this.mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    protected int getCenteredOffset(String string) {
        return this.getCenteredOffset(string, this.xSize);
    }

    protected int getCenteredOffset(String string, int xWidth) {
        return (xWidth - this.fontRendererObj.getStringWidth(string)) / 2;
    }
}
