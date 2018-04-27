package com.minelife.tdm.client.gui;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.tdm.Match;
import com.minelife.tdm.network.PacketLeaveMatch;
import com.minelife.tdm.network.PacketSetLoudout;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GuiLobby extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 250, guiHeight = 230;
    private GuiDropDown primaryDropDown, secondaryDropDown, primarySightDropDown, secondarySightDropDown;
    private Match match;
    private String arena;
    private List<EnumGun> gunSkins;
    private ItemStack primaryGunStack = null, secondaryGunStack = null;
    private GuiButton leaveBtn, readyBtn;


    public GuiLobby(Match match, String arena, List<EnumGun> gunSkins) {
        this.match = match;
        this.arena = arena;
        this.gunSkins = gunSkins;
        this.leaveBtn = null;
        this.readyBtn = null;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        GlStateManager.disableTexture2D();
//        GlStateManager.color(0, 0, 0, 1);
//        drawTexturedModalRect(0, 0, 0, 0, width, height);
//        GlStateManager.enableTexture2D();

        drawDefaultBackground();

        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);

        GlStateManager.disableTexture2D();
        GlStateManager.color(150 / 255f, 150 / 255f, 150 / 255f, 188f / 255f);
        GuiHelper.drawRect(guiLeft + 10, guiTop + 20, 100, 100);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        if (this.primaryGunStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft + 45, this.guiTop + 70, zLevel - 2200);
            GlStateManager.translate(8, 8, 0);
            double scale = 8;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-8, -8, 0);
            GuiFakeInventory.renderItemInventory(this.primaryGunStack, 0, 0, true);
            GlStateManager.popMatrix();
            GlStateManager.disableLighting();
        }

        GlStateManager.disableTexture2D();
        GlStateManager.color(150 / 255f, 150 / 255f, 150 / 255f, 188f / 255f);
        GuiHelper.drawRect(guiLeft + 10, guiTop + 125, 100, 100);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        if (this.secondaryGunStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft + 45, this.guiTop + 165, zLevel - 1200);
            GlStateManager.translate(8, 8, 0);
            double scale = 4;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(-8, -8, 0);
            GuiFakeInventory.renderItemInventory(this.secondaryGunStack, 0, 0, true);
            GlStateManager.popMatrix();
            GlStateManager.disableLighting();
        }

        drawCenteredString(fontRenderer, TextFormatting.RED.toString() + GuiLobbyPlayers.secondsTillStart + " seconds till start!", guiLeft + (this.guiWidth / 2) + (this.guiWidth  / 4) - 5, this.guiTop + this.guiHeight - 60, 0xFFFFFF);

        fontRenderer.drawString(TextFormatting.UNDERLINE + "Primary", primaryDropDown.x, primaryDropDown.y - 12, GuiHelper.defaultTextColor);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Primary Site", primarySightDropDown.x, primarySightDropDown.y - 12, GuiHelper.defaultTextColor);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Secondary", secondaryDropDown.x, secondaryDropDown.y - 12, GuiHelper.defaultTextColor);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Secondary Site", secondarySightDropDown.x, secondarySightDropDown.y - 12, GuiHelper.defaultTextColor);

        secondarySightDropDown.draw(mc, mouseX, mouseY);
        secondaryDropDown.draw(mc, mouseX, mouseY);
        primarySightDropDown.draw(mc, mouseX, mouseY);
        primaryDropDown.draw(mc, mouseX, mouseY);

        fontRenderer.drawString(TextFormatting.BOLD + "Lobby/Loudout", guiLeft + 5, guiTop + 5, GuiHelper.defaultTextColor);
        this.readyBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        this.leaveBtn.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (primaryDropDown.mouseClicked(mc, mouseX, mouseY)) {
        } else if (primarySightDropDown.mouseClicked(mc, mouseX, mouseY)) {
        } else if (secondaryDropDown.mouseClicked(mc, mouseX, mouseY)) {
        } else if (secondarySightDropDown.mouseClicked(mc, mouseX, mouseY)) {
        } else {
            if (this.readyBtn.mousePressed(mc, mouseX, mouseY)) {
                Minelife.getNetwork().sendToServer(new PacketSetLoudout(getSelectedPrimary(), getSelectedSecondary(), getSelectedPrimaryAttachment(), getSelectedSecondaryAttachment()));
            } else if (this.leaveBtn.mousePressed(mc, mouseX, mouseY)) {
                Minelife.getNetwork().sendToServer(new PacketLeaveMatch());
            }
        }

        primaryGunStack.setItemDamage(getSelectedPrimary().ordinal());
        secondaryGunStack.setItemDamage(getSelectedSecondary().ordinal());

        ItemGun.setAttachment(primaryGunStack, getSelectedPrimaryAttachment());
        ItemGun.setAttachment(secondaryGunStack, getSelectedSecondaryAttachment());


    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if(keyCode == Keyboard.KEY_ESCAPE) {
//            Minelife.getNetwork().sendToServer(new PacketLeaveMatch());
//        }
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight) / 2;
        int yOffset = 20;

        List<String> primaryGunNames = Lists.newArrayList();
        gunSkins.forEach(skin -> {
            if (skin.defaultSkin == null && (skin == EnumGun.M4A4 || skin == EnumGun.AK47 || skin == EnumGun.AWP || skin == EnumGun.BARRETT)) {
                primaryGunNames.add(WordUtils.capitalizeFully(skin.name().replace("_", " ")));
            } else if (skin.defaultSkin == EnumGun.M4A4 || skin.defaultSkin == EnumGun.AK47
                    || skin.defaultSkin == EnumGun.AWP || skin.defaultSkin == EnumGun.BARRETT)
                primaryGunNames.add(WordUtils.capitalizeFully(skin.name().replace("_", " ")));
        });

        primaryDropDown = new GuiDropDown(guiLeft + guiWidth - 110, guiTop + yOffset, 100, 15, primaryGunNames.toArray(new String[primaryGunNames.size()]));


        List<String> primarySightDropdown = Lists.newArrayList();
        primarySightDropdown.add("None");
        for (EnumAttachment attachment : EnumAttachment.values())
            primarySightDropdown.add(WordUtils.capitalizeFully(attachment.name().replace("_", " ")));

        primarySightDropDown = new GuiDropDown(guiLeft + guiWidth - 110, guiTop + yOffset + 40, 100, 15, primarySightDropdown.toArray(new String[primarySightDropdown.size()]));

        List<String> secondaryGunNames = Lists.newArrayList();
        gunSkins.forEach(skin -> {
            if (skin.defaultSkin == null && (skin == EnumGun.DESERT_EAGLE || skin == EnumGun.MAGNUM)) {
                secondaryGunNames.add(WordUtils.capitalizeFully(skin.name().replace("_", " ")));
            } else if (skin.defaultSkin == EnumGun.DESERT_EAGLE || skin.defaultSkin == EnumGun.MAGNUM)
                secondaryGunNames.add(WordUtils.capitalizeFully(skin.name().replace("_", " ")));
        });

        secondaryDropDown = new GuiDropDown(guiLeft + guiWidth - 110, guiTop + yOffset + 80, 100, 15, secondaryGunNames.toArray(new String[secondaryGunNames.size()]));
        secondarySightDropDown = new GuiDropDown(guiLeft + guiWidth - 110, guiTop + yOffset + 120, 100, 15, primarySightDropdown.toArray(new String[primarySightDropdown.size()]));

        this.primaryGunStack = new ItemStack(ModGuns.itemGun, 1, getSelectedPrimary().ordinal());
        this.secondaryGunStack = new ItemStack(ModGuns.itemGun, 1, getSelectedSecondary().ordinal());
        ItemGun.setAttachment(primaryGunStack, getSelectedPrimaryAttachment());
        ItemGun.setAttachment(secondaryGunStack, getSelectedSecondaryAttachment());

        buttonList.clear();
        this.readyBtn = new GuiButton(0, guiLeft + guiWidth - 65, guiTop + guiHeight - 40, 40, 20, "Ready");
        this.leaveBtn = new GuiButton(1, guiLeft + guiWidth - 115, guiTop + guiHeight - 40, 40, 20, "Leave");
    }

    public EnumGun getSelectedPrimary() {
        return EnumGun.valueOf(primaryDropDown.options[primaryDropDown.selected].replace(" ", "_").toUpperCase());
    }

    public EnumGun getSelectedSecondary() {
        return EnumGun.valueOf(secondaryDropDown.options[secondaryDropDown.selected].replace(" ", "_").toUpperCase());
    }

    public EnumAttachment getSelectedPrimaryAttachment() {
        return primarySightDropDown.options[primarySightDropDown.selected].equalsIgnoreCase("None") ? null : EnumAttachment.valueOf(primarySightDropDown.options[primarySightDropDown.selected].replace(" ", "_").toUpperCase());
    }

    public EnumAttachment getSelectedSecondaryAttachment() {
        return secondarySightDropDown.options[secondarySightDropDown.selected].equalsIgnoreCase("None") ? null : EnumAttachment.valueOf(secondarySightDropDown.options[secondarySightDropDown.selected].replace(" ", "_").toUpperCase());
    }
}
