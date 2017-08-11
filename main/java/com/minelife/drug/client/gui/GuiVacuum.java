package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.BuildCraftContainer;
import buildcraft.core.lib.gui.GuiBuildCraft;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityPresser;
import com.minelife.drug.tileentity.TileEntityVacuum;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiVacuum extends GuiBuildCraft {

    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/vacuum.png");

    public GuiVacuum(InventoryPlayer player_inventory, TileEntityVacuum tile_vacuum)
    {
        super(new ContainerVacuum(player_inventory, tile_vacuum), tile_vacuum, texture);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
    {
        super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);
        String title = "Vacuum";
        this.fontRendererObj.drawString(title, this.getCenteredOffset(title), 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.mc.renderEngine.bindTexture(texture);
        if (((TileEntityVacuum) this.tile).progress() > 0) {
            int progress = ((TileEntityVacuum) this.tile).progress_scaled(23);
            this.drawTexturedModalRect(this.guiLeft + 75, this.guiTop + 40, 176, 0, progress + 1, 60);
        }
    }
}