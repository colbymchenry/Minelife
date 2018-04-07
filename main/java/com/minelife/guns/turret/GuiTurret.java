package com.minelife.guns.turret;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiPopup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GuiTurret extends GuiContainer {

    private TileEntityTurret TileTurret;
    private ResourceLocation TexCog = new ResourceLocation(Minelife.MOD_ID, "textures/gui/cog.png");
    private ResourceLocation TexInv = new ResourceLocation(Minelife.MOD_ID, "textures/gui/inventory_icon.png");
    private ResourceLocation TexArrow = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    private boolean editSettings = false;
    private GuiButton BtnSettings, BtnLeft, BtnRight, BtnCopy, BtnPaste;
    protected GuiTurretScrollList WhiteListMob, BlackListMob;

    private Color slotColor = new Color(139, 139, 139, 255);

    private static GuiTurret copied = null;

    public GuiTurret(InventoryPlayer PlayerInventory, TileEntityTurret TileTurret) {
        super(new ContainerTurret(PlayerInventory, TileTurret.getInventory()));
        this.TileTurret = TileTurret;
        this.xSize = 176;

        int inventoryRows = TileTurret.getInventory().getSizeInventory() / 9;
        this.ySize = 114 + inventoryRows * 18;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        for (Object s : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) s;
            GuiFakeInventory.drawSlot(this.guiLeft + slot.xPos - 1, this.guiTop + slot.yPos - 1, 17, 17, slotColor.getRGB());
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);

        if (!editSettings) {
            BtnSettings.drawButton(mc, mouseX, mouseY, partialTicks);
            mc.getTextureManager().bindTexture(TexCog);
            GuiHelper.drawImage(BtnSettings.x + 3, BtnSettings.y + 3, 14, 14, TexCog);
            return;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = "Turret's Ammo Supply";
        this.fontRenderer.drawString(s, 88 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString("Inventory", 8, 128, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        /**
         * Draw bullet inventory
         */
        if (!editSettings) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }

        drawDefaultBackground();

        /**
         * Draw settings menu
         */


        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        mc.fontRenderer.drawString("Blacklist", BlackListMob.x + ((BlackListMob.width - mc.fontRenderer.getStringWidth("Blacklist")) / 2), BlackListMob.y - 10, 4210752);
        mc.fontRenderer.drawString("Whitelist", WhiteListMob.x + ((WhiteListMob.width - mc.fontRenderer.getStringWidth("Whitelist")) / 2), WhiteListMob.y - 10, 4210752);

        BtnSettings.drawButton(mc, mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(TexInv);
        GuiHelper.drawImage(BtnSettings.x + 4, BtnSettings.y + 4, 12, 12, TexInv);

        int dWheel = Mouse.getDWheel();
        WhiteListMob.draw(mouseX, mouseY, dWheel);
        BlackListMob.draw(mouseX, mouseY, dWheel);
        BtnLeft.drawButton(mc, mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(TexArrow);

        GL11.glPushMatrix();
        GL11.glTranslatef(BtnLeft.x + 2, BtnLeft.y + 18, zLevel);
        GL11.glRotatef(-90f, 0f, 0f, 1f);
        GuiHelper.drawImage(0, 0, 16, 16, TexArrow);
        GL11.glPopMatrix();

        BtnRight.drawButton(mc, mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(TexArrow);
        GL11.glPushMatrix();
        GL11.glTranslatef(BtnRight.x + 18, BtnRight.y + 1, zLevel);
        GL11.glRotatef(90f, 0f, 0f, 1f);
        GuiHelper.drawImage(0, 0, 16, 16, TexArrow);
        GL11.glPopMatrix();

        BtnCopy.drawButton(mc, mouseX, mouseY, partialTicks);
        BtnPaste.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char KeyChar, int KeyCode) throws IOException {
        if (editSettings) {
            WhiteListMob.keyTyped(KeyChar, KeyCode);
            BlackListMob.keyTyped(KeyChar, KeyCode);
            if (KeyCode == Keyboard.KEY_ESCAPE) {
                super.keyTyped(KeyChar, KeyCode);
            }
        } else {
            super.keyTyped(KeyChar, KeyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseBtn);

        if (BtnSettings.mousePressed(mc, mouseX, mouseY)) {
            editSettings = !editSettings;
            return;
        }

        if (editSettings) {
            if (BtnRight.mousePressed(mc, mouseX, mouseY)) {
                if (BlackListMob.selected != -1 && BlackListMob.selected < BlackListMob.StringList.size()) {
                    String mob = BlackListMob.StringList.get(BlackListMob.selected);
                    BlackListMob.StringList.remove(mob);
                    WhiteListMob.StringList.add(mob);
                }
            }
            if (BtnLeft.mousePressed(mc, mouseX, mouseY)) {
                if (WhiteListMob.selected != -1 && WhiteListMob.selected < WhiteListMob.StringList.size()) {
                    String mob = WhiteListMob.StringList.get(WhiteListMob.selected);
                    WhiteListMob.StringList.remove(mob);
                    BlackListMob.StringList.add(mob);
                }
            }

            if (BtnCopy.mousePressed(mc, mouseX, mouseY)) {
                copied = this;
            }

            if (BtnPaste.mousePressed(mc, mouseX, mouseY) && copied != null) {
                WhiteListMob = copied.WhiteListMob;
                BlackListMob = copied.BlackListMob;
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Set<EnumMob> MobWhiteList = Sets.newTreeSet();
        if (!WhiteListMob.StringList.isEmpty()) WhiteListMob.StringList.forEach(mob -> {
            if (mob != null) {
                MobWhiteList.add(EnumMob.valueOf(mob));
            }
        });

        Minelife.getNetwork().sendToServer(new PacketSetTurretSettings(MobWhiteList, TileTurret.getPos().getX(), TileTurret.getPos().getY(), TileTurret.getPos().getZ()));
    }

    @Override
    public void initGui() {
        super.initGui();
        BtnSettings = new GuiButton(0, this.guiLeft - 22, this.guiTop, 20, 20, "");

        if (BlackListMob == null) {
            Set<EnumMob> MobBlackList = Sets.newTreeSet();
            MobBlackList.addAll(Arrays.asList(EnumMob.values()));
            Set<String> MobWhiteList = Sets.newTreeSet();
            MobBlackList.removeAll(TileTurret.getMobWhiteList());

            String[] MBLArray = new String[MobBlackList.size()];
            for (int i = 0; i < MobBlackList.size(); i++) MBLArray[i] = ((EnumMob) MobBlackList.toArray()[i]).name();

            TileTurret.getMobWhiteList().forEach(mob -> MobWhiteList.add(mob.name()));

            BlackListMob = new GuiTurretScrollList(mc, guiLeft + 5, guiTop + 25, (xSize / 3) + 10, ySize / 4, MBLArray);
            WhiteListMob = new GuiTurretScrollList(mc, guiLeft + xSize - ((xSize / 3) + 10) - 5, BlackListMob.y, (xSize / 3) + 10, ySize / 4, MobWhiteList.toArray(new String[MobWhiteList.size()]));
        } else {
            BlackListMob.x = guiLeft + 5;
            BlackListMob.y = guiTop + 25;
            BlackListMob.width = (xSize / 3) + 10;
            BlackListMob.height = ySize / 4;

            WhiteListMob.x = guiLeft + xSize - ((xSize / 3) + 10) - 5;
            WhiteListMob.y = BlackListMob.y;
            WhiteListMob.width = (xSize / 3) + 10;
            WhiteListMob.height = ySize / 4;
        }

        BtnRight = new GuiButton(0, guiLeft + ((xSize - 20) / 2), WhiteListMob.y + 6, 20, 20, "");
        BtnLeft = new GuiButton(0, guiLeft + ((xSize - 20) / 2), WhiteListMob.y + 30, 20, 20, "");

        BtnCopy = new GuiButton(0, guiLeft - 42, guiTop + 80, 40, 20, "Copy");
        BtnPaste = new GuiButton(0, guiLeft - 42, guiTop + 101, 40, 20, "Paste");
        BtnPaste.enabled = copied != null;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (editSettings) {
            BtnPaste.enabled = copied != null;
        }
    }
}