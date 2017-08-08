package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import buildcraft.energy.TileEngineIron;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
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
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 100 + 2, 4210752);

        TileEntityCementMixer cement_mixer = (TileEntityCementMixer) this.tile;
        FluidStack stack = null;
        if (cement_mixer != null && mouse_y >= this.guiTop + 13 && mouse_y < this.guiTop + 13 + 60) {
            if (mouse_x >= this.guiLeft + 8 && mouse_x < this.guiLeft + 8 + 16) {
                stack = cement_mixer.fluid();
            }
        }

        if (stack != null && stack.amount > 0) {
            List<String> fluidTip = new ArrayList();
            fluidTip.add(stack.getLocalizedName());
            this.drawHoveringText(fluidTip, mouse_x - this.guiLeft, mouse_y - this.guiTop, this.fontRendererObj);
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        TileEntityCementMixer engine = (TileEntityCementMixer) this.tile;
        this.drawFluid(engine.fluid(), this.guiLeft + 9, this.guiTop + 13, 16, 58, 10000);
        this.mc.renderEngine.bindTexture(texture);

        if (engine.progress() > 0) {
            int progress = engine.progress_scaled(23);
            this.drawTexturedModalRect(this.guiLeft + 94, this.guiTop + 33, 176, 0, progress + 1, 18);
        }
        this.drawTexturedModalRect(this.guiLeft + 8, this.guiTop + 18, 176, 18, 16, 50);
    }
}
