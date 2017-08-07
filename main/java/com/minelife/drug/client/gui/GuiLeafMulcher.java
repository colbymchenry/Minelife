package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class GuiLeafMulcher extends GuiBuildCraft {

    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/leaf_mulcher.png");

    public GuiLeafMulcher(InventoryPlayer player_inventory, TileEntityLeafMulcher tile_leaf_mulcher)
    {
        super(new ContainerLeafMulcher(player_inventory, tile_leaf_mulcher), tile_leaf_mulcher, texture);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
    {
        super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);
        String title = "Leaf Mulcher";
        this.fontRendererObj.drawString(title, this.getCenteredOffset(title), 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.mc.renderEngine.bindTexture(texture);
        if (((TileEntityLeafMulcher) this.tile).progress() > 0) {
            int progress = ((TileEntityLeafMulcher) this.tile).progress_scaled(23);
            this.drawTexturedModalRect(this.guiLeft + 76, this.guiTop + 40, 176, 0, progress + 1, 60);
        }
    }
}
