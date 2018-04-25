package com.minelife.drugs.client.gui;

public class GuiCementMixer {

}

//public class GuiCementMixer extends GuiContainer {
//
//    private static final ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID + ":textures/gui/cement_mixer.png");
//
//    private TileEntityCementMixer tile;
//
//    private static IGuiArea fuelArea, solventArea;
//
//    public GuiCementMixer(EntityPlayer player, TileEntityCementMixer tile) {
//        super(new ContainerCementMixer(player, tile));
//        this.xSize = 219;
//        this.ySize = 172;
//        this.tile = tile;
//    }
//
//    @Override
//    public void initGui() {
//        super.initGui();
//        this.fuelArea = new GuiRectangle(guiLeft + 9, guiTop + 13, 16, 58);
//        this.solventArea = new GuiRectangle(guiLeft + 194, guiTop + 13, 16, 58);
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        this.renderHoveredToolTip(mouseX, mouseY);
//
//        mc.getTextureManager().bindTexture(texture);
//        drawTexturedModalRect((int) fuelArea.getX(), (int) fuelArea.getY() + 5, 219, 18, 16, 49);
//        drawTexturedModalRect((int) solventArea.getX(), (int) solventArea.getY() + 5, 219, 68, 16, 49);
//
//        if(this.tile.getTankManager() != null && this.tile.getTankManager().get(0) != null && this.tile.getTankManager().get(1) != null) {
//            if(fuelArea.contains(mouseX, mouseY)) {
//                ToolTip tooltip = this.tile.getTankManager().get(0).getToolTip();
//                tooltip.refresh();
//                this.drawHoveringText(tooltip, mouseX, mouseY);
//            }
//            if(solventArea.contains(mouseX, mouseY)) {
//                ToolTip tooltip = this.tile.getTankManager().get(1).getToolTip();
//                tooltip.refresh();
//                this.drawHoveringText(tooltip, mouseX, mouseY);
//            }
//        }
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//
//    }
//
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GlStateManager.color(1, 1, 1, 1);
//        this.mc.renderEngine.bindTexture(texture);
//        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
//
//        if (this.tile.progress > 0) {
//            int progress = this.tile.progressScaled(23);
//            this.drawTexturedModalRect(this.guiLeft + 112, this.guiTop + 33, 219, 0, progress + 1, 18);
//        }
//
//        if(this.tile.getTankManager() != null && this.tile.getTankManager().get(0).getFluidForRender() != null) {
//            GuiUtil.drawFluid(fuelArea, this.tile.getTankManager().get(0).getFluidForRender(), TileEntityLeafMulcher.MAX_FLUID);
//        }
//
//        if(this.tile.getTankManager() != null && this.tile.getTankManager().get(1).getFluidForRender() != null) {
//            GuiUtil.drawFluid(solventArea, this.tile.getTankManager().get(1).getFluidForRender(), TileEntityLeafMulcher.MAX_FLUID);
//        }
//    }
//
//}