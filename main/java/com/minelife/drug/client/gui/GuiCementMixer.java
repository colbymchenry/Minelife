package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class GuiCementMixer extends GuiBuildCraft {

    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/cement_mixer.png");

    public GuiCementMixer(InventoryPlayer player_inventory, TileEntityCementMixer tile_cement_mixer)
    {
        super(new ContainerCementMixer(player_inventory, tile_cement_mixer), tile_cement_mixer, texture);
        this.xSize = 176;
        this.ySize = 172;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
    {
        super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);
        String title = "Cement Mixer";
        this.fontRendererObj.drawString(title, this.getCenteredOffset(title), 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        this.mc.renderEngine.bindTexture(texture);
        this.drawTexturedModalRect(this.guiLeft + 155, this.guiTop + 19, 176, 0, 16, 60);

        if (((TileEntityCementMixer) this.tile).progress() > 0) {
            int progress = ((TileEntityCementMixer) this.tile).progress_scaled(23);
            this.drawTexturedModalRect(this.guiLeft + 89, this.guiTop + 45, 176, 0, progress + 1, 12);
        }
    }
}
