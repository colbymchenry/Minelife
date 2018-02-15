package com.minelife.gun.turrets;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.gun.packet.PacketGetGangName;
import com.minelife.gun.packet.PacketSetTurretSettings;
import com.minelife.util.client.GuiPopup;
import com.minelife.util.client.GuiUtil;
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
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class GuiTurret extends GuiContainer implements IGangNameReceiver {

    private TileEntityTurret TileTurret;
    private ResourceLocation TexCog = new ResourceLocation(Minelife.MOD_ID, "textures/gui/cog.png");
    private ResourceLocation TexInv = new ResourceLocation(Minelife.MOD_ID, "textures/gui/inventory_icon.png");
    private ResourceLocation TexArrow = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    private boolean editSettings = false;
    private GuiButton BtnSettings, BtnLeft, BtnRight, BtnAddGang, BtnRemoveGang, BtnCopy, BtnPaste;
    private GuiTurretScrollList WhiteListMob, BlackListMob, WhiteListGang;
    private GuiTextField TxtFieldWhiteListGang;
    private Set<UUID> GangWhiteListUUIDs = Sets.newTreeSet();

    private Color slotColor = new Color(139, 139, 139, 255);

    private static GuiTurret copied = null;

    public GuiTurret(InventoryPlayer PlayerInventory, TileEntityTurret TileTurret) {
        super(new ContainerTurret(PlayerInventory, TileTurret));
        this.TileTurret = TileTurret;
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

        if (!editSettings) {
            BtnSettings.drawButton(mc, mouseX, mouseY);
            mc.getTextureManager().bindTexture(TexCog);
            GuiUtil.drawImage(BtnSettings.xPosition + 3, BtnSettings.yPosition + 3, 14, 14);
            return;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = "Turret's Ammo Supply";
        this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, 128, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        /**
         * Draw bullet inventory
         */
        if (!editSettings) {
            super.drawScreen(mouseX, mouseY, f);
            return;
        }

        drawDefaultBackground();

        /**
         * Draw settings menu
         */


        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
        mc.fontRenderer.drawString("Blacklist", BlackListMob.xPosition + ((BlackListMob.width - mc.fontRenderer.getStringWidth("Blacklist")) / 2), BlackListMob.yPosition - 10, 4210752);
        mc.fontRenderer.drawString("Whitelist", WhiteListMob.xPosition + ((WhiteListMob.width - mc.fontRenderer.getStringWidth("Whitelist")) / 2), WhiteListMob.yPosition - 10, 4210752);
        mc.fontRenderer.drawString("Whitelisted Gangs", WhiteListGang.xPosition + ((WhiteListGang.width - mc.fontRenderer.getStringWidth("Whitelisted Gangs")) / 2), WhiteListGang.yPosition - 10, 4210752);

        TxtFieldWhiteListGang.drawTextBox();
        BtnAddGang.drawButton(mc, mouseX, mouseY);

        BtnSettings.drawButton(mc, mouseX, mouseY);
        mc.getTextureManager().bindTexture(TexInv);
        GuiUtil.drawImage(BtnSettings.xPosition + 4, BtnSettings.yPosition + 3.5f, 12, 12);

        int dWheel = Mouse.getDWheel();
        WhiteListMob.draw(mouseX, mouseY, dWheel);
        BlackListMob.draw(mouseX, mouseY, dWheel);
        WhiteListGang.draw(mouseX, mouseY, dWheel);
        BtnLeft.drawButton(mc, mouseX, mouseY);
        mc.getTextureManager().bindTexture(TexArrow);

        GL11.glPushMatrix();
        GL11.glTranslatef(BtnLeft.xPosition + 2, BtnLeft.yPosition + 18, zLevel);
        GL11.glRotatef(-90f, 0f, 0f, 1f);
        GuiUtil.drawImage(0, 0, 16, 16);
        GL11.glPopMatrix();

        BtnRight.drawButton(mc, mouseX, mouseY);
        mc.getTextureManager().bindTexture(TexArrow);
        GL11.glPushMatrix();
        GL11.glTranslatef(BtnRight.xPosition + 18, BtnRight.yPosition + 1, zLevel);
        GL11.glRotatef(90f, 0f, 0f, 1f);
        GuiUtil.drawImage(0, 0, 16, 16);
        GL11.glPopMatrix();

        BtnCopy.drawButton(mc, mouseX, mouseY);
        BtnPaste.drawButton(mc, mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char KeyChar, int KeyCode) {
        if (editSettings) {
            WhiteListMob.keyTyped(KeyChar, KeyCode);
            BlackListMob.keyTyped(KeyChar, KeyCode);
            WhiteListGang.keyTyped(KeyChar, KeyCode);
            TxtFieldWhiteListGang.textboxKeyTyped(KeyChar, KeyCode);
            if (KeyCode == Keyboard.KEY_ESCAPE) {
                super.keyTyped(KeyChar, KeyCode);
            }
        } else {
            super.keyTyped(KeyChar, KeyCode);
        }
    }

    // TODO: add way to remove gangs
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
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

            TxtFieldWhiteListGang.mouseClicked(mouseX, mouseY, mouseBtn);

            if (TileTurret.getWorldObj().isRemote) {
                if (BtnAddGang.mousePressed(mc, mouseX, mouseY)) {
                    Minelife.NETWORK.sendToServer(new PacketGetGangName(TxtFieldWhiteListGang.getText(), this));
                }
            }

            if (BtnCopy.mousePressed(mc, mouseX, mouseY)) {
                copied = this;
            }

            if (BtnPaste.mousePressed(mc, mouseX, mouseY) && copied != null) {
                WhiteListMob = copied.WhiteListMob;
                BlackListMob = copied.BlackListMob;
                GangWhiteListUUIDs.clear();
                WhiteListGang = copied.WhiteListGang;
                copied.WhiteListGang.StringList.forEach(gang -> Minelife.NETWORK.sendToServer(new PacketGetGangName(gang, this)));
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
        Minelife.NETWORK.sendToServer(new PacketSetTurretSettings(MobWhiteList, GangWhiteListUUIDs, TileTurret.xCoord, TileTurret.yCoord, TileTurret.zCoord));
    }

    @Override
    public void initGui() {
        super.initGui();
        BtnSettings = new GuiButton(0, this.guiLeft + this.xSize + 2, this.guiTop, 20, 20, "");

        if (BlackListMob == null) {
            Set<EnumMob> MobBlackList = Sets.newTreeSet();
            MobBlackList.addAll(Arrays.asList(EnumMob.values()));
            Set<String> MobWhiteList = Sets.newTreeSet();
            MobBlackList.removeAll(TileTurret.getMobWhiteList());

            String[] MBLArray = new String[MobBlackList.size()];
            for (int i = 0; i < MobBlackList.size(); i++) MBLArray[i] = ((EnumMob) MobBlackList.toArray()[i]).name();

            TileTurret.getMobWhiteList().forEach(mob -> MobWhiteList.add(mob.name()));

            BlackListMob = new GuiTurretScrollList(mc, guiLeft + 5, guiTop + 25, (xSize / 3) + 10, ySize / 4, MBLArray);
            WhiteListMob = new GuiTurretScrollList(mc, guiLeft + xSize - ((xSize / 3) + 10) - 5, BlackListMob.yPosition, (xSize / 3) + 10, ySize / 4, MobWhiteList.toArray(new String[MobWhiteList.size()]));
        } else {
            BlackListMob.xPosition = guiLeft + 5;
            BlackListMob.yPosition = guiTop + 25;
            BlackListMob.width = (xSize / 3) + 10;
            BlackListMob.height = ySize / 4;

            WhiteListMob.xPosition = guiLeft + xSize - ((xSize / 3) + 10) - 5;
            WhiteListMob.yPosition = BlackListMob.yPosition;
            WhiteListMob.width = (xSize / 3) + 10;
            WhiteListMob.height = ySize / 4;
        }

        if (WhiteListGang == null) {
            String[] gangs = new String[TileTurret.getGangWhiteList().size()];
            for (int i = 0; i < gangs.length; i++) {
                gangs[i] = "" + TileTurret.getGangWhiteList().toArray()[i];
            }

            WhiteListGang = new GuiTurretScrollList(mc, guiLeft + 5, BlackListMob.yPosition + BlackListMob.height + 30, (xSize / 2) + 10, ySize / 4, gangs);

            if (TileTurret.getWorldObj().isRemote)
                TileTurret.getGangWhiteList().forEach(gangID -> Minelife.NETWORK.sendToServer(new PacketGetGangName(gangID, this)));
        } else {
            WhiteListGang.xPosition = guiLeft + 5;
            WhiteListGang.yPosition = BlackListMob.yPosition + BlackListMob.height + 30;
            WhiteListGang.width = (xSize / 2) + 10;
            WhiteListGang.height = ySize / 4;
        }


        BtnRight = new GuiButton(0, guiLeft + ((xSize - 20) / 2), WhiteListMob.yPosition + 6, 20, 20, "");
        BtnLeft = new GuiButton(0, guiLeft + ((xSize - 20) / 2), WhiteListMob.yPosition + 30, 20, 20, "");

        TxtFieldWhiteListGang = new GuiTextField(fontRendererObj, WhiteListGang.xPosition + WhiteListGang.width + 5, WhiteListGang.yPosition + 5, 60, 20);
        BtnAddGang = new GuiButton(0, TxtFieldWhiteListGang.xPosition, TxtFieldWhiteListGang.yPosition + TxtFieldWhiteListGang.height + 5, 60, 20, "Add Gang");
        BtnAddGang.enabled = false;
        BtnCopy = new GuiButton(0, guiLeft + xSize + 2, guiTop + 80, 40, 20, "Copy");
        BtnPaste = new GuiButton(0, guiLeft + xSize + 2, guiTop + 101, 40, 20, "Paste");
        BtnPaste.enabled = copied != null;
    }

    @Override
    public void nameReceived(UUID uuid, String name) {
        if (uuid == null) {
            GuiScreen previousScreen = Minecraft.getMinecraft().currentScreen;
            Minecraft.getMinecraft().displayGuiScreen(new GuiPopup("Gang not found.", 0xC6C6C6, previousScreen));
            return;
        }

        if (name.equalsIgnoreCase(TxtFieldWhiteListGang.getText())) {
            TxtFieldWhiteListGang.setText("");
            WhiteListGang.StringList.add(name);
            GangWhiteListUUIDs.add(uuid);
            return;
        }

        for (int i = 0; i < WhiteListGang.StringList.size(); i++) {
            if (WhiteListGang.StringList.get(i).equals(uuid.toString())) {
                WhiteListGang.StringList.set(i, name);
                GangWhiteListUUIDs.add(uuid);
            }
        }

        if (!GangWhiteListUUIDs.contains(uuid)) GangWhiteListUUIDs.add(uuid);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (editSettings) {
            BtnAddGang.enabled = !TxtFieldWhiteListGang.getText().isEmpty();
            BtnPaste.enabled = copied != null;
        }
    }
}
