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

    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/leaf_mulcher_gui.png");

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
        TileEntityLeafMulcher tile_leaf_mulcher = (TileEntityLeafMulcher) this.tile;
        FluidStack stack = null;
        if (tile_leaf_mulcher != null && mouse_y >= this.guiTop + 19 && mouse_y < this.guiTop + 19 + 60) {
            if (mouse_x >= this.guiLeft + 155 && mouse_x < this.guiLeft + 155 + 16) {
                stack = tile_leaf_mulcher.fuel();
            }
        }

        if (stack != null && stack.amount > 0) {
            List<String> fluidTip = Lists.newArrayList();
            fluidTip.add(stack.getLocalizedName());
            this.drawHoveringText(fluidTip, mouse_x - this.guiLeft, mouse_y - this.guiTop, this.fontRendererObj);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        TileEntityLeafMulcher tile_leaf_mulcher = (TileEntityLeafMulcher) this.tile;
        if (tile_leaf_mulcher != null) {
            this.drawFluid(tile_leaf_mulcher.fuel(), this.guiLeft + 155, this.guiTop + 19, 16, 58, tile_leaf_mulcher.max_fuel());
        }

        this.mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect(this.guiLeft + 155, this.guiTop + 19, 176, 0, 16, 60);
    }
}
