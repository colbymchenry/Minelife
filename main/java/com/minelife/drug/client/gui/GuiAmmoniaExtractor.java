package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import com.google.common.collect.Lists;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiAmmoniaExtractor extends GuiBuildCraft {

    private TileEntityAmmoniaExtractor tile_ammonia_extractor;

    public GuiAmmoniaExtractor(InventoryPlayer inventoryplayer, TileEntityAmmoniaExtractor tile) {
        super(new ContainerAmmoniaExtractor(inventoryplayer, tile), tile, new ResourceLocation("buildcraftfactory:textures/gui/ammonia_extractor.png"));
        this.tile_ammonia_extractor = tile;
        this.xSize = 176;
        this.ySize = 197;
    }

    // TODO: Make gui
    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y) {
        String title = "Ammonia Extractor";
        this.fontRendererObj.drawString(title, this.getCenteredOffset(title), 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);

        TileEntityAmmoniaExtractor tile_ammonia_extractor = (TileEntityAmmoniaExtractor) this.tile;
        FluidStack stack = null;
        if (tile_ammonia_extractor != null && mouse_y >= this.guiTop + 19 && mouse_y < this.guiTop + 19 + 60) {
            if (mouse_x >= this.guiLeft + 155 && mouse_x < this.guiLeft + 155 + 16) {
                stack = tile_ammonia_extractor.fuel();
            }
        }

        if (stack != null && stack.amount > 0) {
            List<String> fluidTip = Lists.newArrayList();
            fluidTip.add(stack.getLocalizedName());
            this.drawHoveringText(fluidTip, mouse_x - this.guiLeft, mouse_y - this.guiTop, this.fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.tile_ammonia_extractor.progress() > 0) {
            int progress = this.tile_ammonia_extractor.get_progress_scaled(23);
            this.drawTexturedModalRect(this.guiLeft + 89, this.guiTop + 45, 176, 0, progress + 1, 12);
        }


        TileEntityAmmoniaExtractor tile_ammonia_extractor = (TileEntityAmmoniaExtractor) this.tile;
        if (tile_ammonia_extractor != null) {
            this.drawFluid(tile_ammonia_extractor.fuel(), this.guiLeft + 155, this.guiTop + 19, 16, 58, tile_ammonia_extractor.max_fuel());
        }
    }
}
