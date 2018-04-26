package com.minelife.drugs.client.gui;

import com.minelife.Minelife;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiVacuum {}

//public class GuiVacuum extends GuiContainer {
//
//    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/vacuum.png");
//    private TileEntityVacuum tile;
//
//    public GuiVacuum(EntityPlayer player, TileEntityVacuum tile) {
//        super(new ContainerVacuum(player, tile));
//        this.tile = tile;
//        this.allowUserInput = false;
//        this.xSize = 176;
//        this.ySize = 166;
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        this.renderHoveredToolTip(mouseX, mouseY);
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//    }
//
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GlStateManager.color(1, 1, 1, 1);
//
//        mc.getTextureManager().bindTexture(texture);
//        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
//
//        if (this.tile.progress > 0) {
//            int progress = this.tile.progressScaled(23);
//            this.drawTexturedModalRect(this.guiLeft + 75, this.guiTop + 40, 176, 0, progress + 1, 18);
//        }
//    }
//}