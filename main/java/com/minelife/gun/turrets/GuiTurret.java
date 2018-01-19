package com.minelife.gun.turrets;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.client.config.GuiCheckBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;


public class GuiTurret extends GuiContainer {

    private ResourceLocation TexCog = new ResourceLocation(Minelife.MOD_ID, "textures/gui/cog.png");
    private ResourceLocation TexInv = new ResourceLocation(Minelife.MOD_ID, "textures/gui/inventory_icon.png");
    private boolean editSettings = false;
    private GuiButton BtnSettings, BtnLeft, BtnRight, BtnAddGang, BtnRemoveGang;
    private GuiTurretScrollList WhiteListMob, BlackListMob, WhiteListGang;

    private Color slotColor = new Color(139, 139, 139, 255);

    public GuiTurret(InventoryPlayer PlayerInventory, TileEntityTurret TileTurret) {
        super(new ContainerTurret(PlayerInventory, TileTurret));

        this.xSize = 176;

        int inventoryRows = TileTurret.getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        for (Object s : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) s;
            GuiUtil.drawSlot(this.guiLeft + slot.xDisplayPosition - 1, this.guiTop + slot.yDisplayPosition - 1, 17, 17, slotColor);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    // TODO: Finish the settings menu
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = "Turret's Ammo Supply";
        this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, 128, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        if(!editSettings) {
            super.drawScreen(mouseX, mouseY, f);
            BtnSettings.drawButton(mc, mouseX, mouseY);
            mc.getTextureManager().bindTexture(TexCog);
            GuiUtil.drawImage(BtnSettings.xPosition, BtnSettings.yPosition, 16, 16);
            return;
        }

        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        BtnSettings.drawButton(mc, mouseX, mouseY);
        mc.getTextureManager().bindTexture(TexInv);
        GuiUtil.drawImage(BtnSettings.xPosition, BtnSettings.yPosition, 16, 16);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
        if(!editSettings && BtnSettings.mousePressed(mc, mouseX, mouseY)) {
            editSettings = true;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        BtnSettings = new GuiButton(0, this.guiLeft + this.xSize, this.guiTop, 20, 20, "");
        String[] PassiveMobs = new String[EnumMob.values().length];
        for (int i = 0; i < EnumMob.values().length; i++) PassiveMobs[i] = EnumMob.values()[i].name();

//        WhiteListMob
    }
}
