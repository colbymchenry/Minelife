package com.minelife.minebay.client.gui;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.network.PacketGetItemListings;
import com.minelife.minebay.network.PacketGetPlayerListings;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiLoadingAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiItemListings extends GuiMinebay {

    private static ResourceLocation texProfile = new ResourceLocation(Minelife.MOD_ID, "textures/gui/minebay/profile.png");
    private static ResourceLocation texSearch = new ResourceLocation(Minelife.MOD_ID, "textures/gui/magnify.png");
    private static ResourceLocation texSort = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    private GuiScrollableListing guiListings;
    private GuiLoadingAnimation guiLoading;
    private static GuiDropDown guiSort, guiPage;
    private static GuiTextField searchField;
    private static boolean ascend = true;
    private List<ItemListing> itemListings;
    private int pageCount = 0;
    private GuiMinebayBtn sellBtn, myListingsBtn;
    private Rectangle sortRec;

    public GuiItemListings() {
        Minelife.getNetwork().sendToServer(new PacketGetItemListings(0, " ", 0, ascend));
        if (guiPage != null) guiPage.selected = 0;
        if (guiSort != null) guiSort.selected = 0;
        if (searchField != null) searchField.setText("");
        ascend = true;
    }

    public GuiItemListings(List<ItemListing> itemListings, int pageCount) {
        this.itemListings = itemListings;
        this.pageCount = pageCount;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (guiListings == null) {
            guiLoading.drawLoadingAnimation();
            return;
        }

        Color color = new Color(72, 0, 70, 200);
        this.drawGradientRect(guiLeft - 2, guiTop - 2, guiLeft + xSize, guiTop + 25, color.hashCode(), color.hashCode());


        this.sellBtn.drawButton(mc, mouseX, mouseY, partialTicks);
        this.myListingsBtn.drawButton(mc, mouseX, mouseY, partialTicks);

        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(texProfile);
        Gui.drawModalRectWithCustomSizedTexture(this.myListingsBtn.x + 7, this.myListingsBtn.y + 4, 0, 0, 12, 12, 12, 12);

        boolean hovering = sortRec.contains(mouseX, mouseY);
        GuiHelper.drawDefaultBackground(sortRec.x, sortRec.y, sortRec.width, sortRec.height, hovering ? 0xe600e4 : 0xcb00cb);

        mc.getTextureManager().bindTexture(texSort);
        GlStateManager.pushMatrix();
        GlStateManager.translate(sortRec.x + (!ascend ? 1.5f : 3f), sortRec.y + 2.5f, 0);
        GlStateManager.translate(8, 8, 0);
        GlStateManager.rotate(ascend ? 180 : 0, 0, 0, 1);
        GlStateManager.translate(-8, -8, 0);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
        GlStateManager.popMatrix();

        this.guiListings.draw(mouseX, mouseY, Mouse.getDWheel());

        GlStateManager.disableLighting();
        guiSort.draw(mc, mouseX, mouseY);
        guiPage.draw(mc, mouseX, mouseY);
        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(texSearch);
        Gui.drawModalRectWithCustomSizedTexture(guiLeft + searchField.getWidth() + 13, searchField.y + 3, 0, 0, 16, 16, 16, 16);
        searchField.drawTextBox();

        if (this.itemListings.isEmpty()) {
            String msg = TextFormatting.BOLD + "Uh-Oh! No item were found :'(";
            int msg_width = mc.fontRenderer.getStringWidth(msg);
            mc.fontRenderer.drawStringWithShadow(msg, (this.width - msg_width) / 2, guiTop + ((ySize - mc.fontRenderer.FONT_HEIGHT) / 2), 0xFFFFFF);
            return;
        }


        if (this.guiListings.getHoveringStack() != null)
            renderToolTip(this.guiListings.getHoveringStack(), mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (guiListings != null)
            this.guiListings.keyTyped(typedChar, keyCode);

        if (searchField != null) {
            if (searchField.isFocused() && keyCode == Keyboard.KEY_RETURN) {
                guiSort.selected = 0;
                guiPage.selected = 0;
                Minelife.getNetwork().sendToServer(new PacketGetItemListings(NumberConversions.toInt(guiPage.options[guiPage.selected]) - 1, searchField.getText(), guiSort.selected, ascend));
            }
            searchField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (guiSort != null) {
            int selected = guiSort.selected;
            if (guiSort.mouseClicked(mc, mouseX, mouseY)) {
                if (selected != guiSort.selected) {
                    Minelife.getNetwork().sendToServer(new PacketGetItemListings(NumberConversions.toInt(guiPage.options[guiPage.selected]) - 1,
                            searchField.getText(), guiSort.selected, ascend));
                }
                return;
            }
        }

        if (guiPage != null) {
            int selected = guiPage.selected;
            if (guiPage.mouseClicked(mc, mouseX, mouseY)) {
                if (selected != guiPage.selected) {
                    Minelife.getNetwork().sendToServer(new PacketGetItemListings(NumberConversions.toInt(guiPage.options[guiPage.selected]) - 1,
                            searchField.getText(), guiSort.selected, ascend));
                }
                return;
            }
        }

        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.sellBtn != null)
            if (this.sellBtn.mousePressed(mc, mouseX, mouseY))
                Minecraft.getMinecraft().displayGuiScreen(new GuiSellItem());

        if (mouseX >= guiLeft + searchField.getWidth() + 13 && mouseX <= guiLeft + searchField.getWidth() + 13 + 16) {
            if (mouseY >= searchField.y + 3 && mouseY <= searchField.y + 3 + 16) {
                guiPage.selected = 0;
                Minelife.getNetwork().sendToServer(new PacketGetItemListings(NumberConversions.toInt(guiPage.options[guiPage.selected]) - 1, searchField.getText(), guiSort.selected, ascend));
            }
        }

        if (sortRec.contains(mouseX, mouseY)) {
            ascend = !ascend;
            Minelife.getNetwork().sendToServer(new PacketGetItemListings(NumberConversions.toInt(guiPage.options[guiPage.selected]) - 1, searchField.getText(), guiSort.selected, ascend));
        }

        if(this.myListingsBtn.mousePressed(mc, mouseX, mouseY)) {
            Minelife.getNetwork().sendToServer(new PacketGetPlayerListings());
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.itemListings == null) {
            this.guiLoading = new GuiLoadingAnimation((this.width - 75) / 2, 20 + (this.height - 75) / 2, 75, 75, Color.white);
            return;
        }

        this.guiListings = new GuiScrollableListing(mc, this.guiLeft, this.guiTop + 26, this.xSize, this.ySize - 30, itemListings);

        if (searchField == null)
            searchField = new GuiTextField(0, mc.fontRenderer, this.guiLeft + 1, this.guiTop + 1, (xSize / 2) - 60, 20);
        else {
            searchField.x = this.guiLeft + 1;
            searchField.y = this.guiTop + 1;
            searchField.width = (xSize / 2) - 60;
        }

        if (guiSort == null)
            guiSort = new GuiDropDown(searchField.x + searchField.width + 30, searchField.y + 3, 60, 13, PacketGetItemListings.options);
        else {
            guiSort.x = searchField.x + searchField.width + 30;
            guiSort.y = searchField.y + 3;
        }

        guiSort.colorBG = new Color(206, 0, 204);
        guiSort.colorHighlight = guiSort.colorBG.darker().darker();

        sortRec = new Rectangle(guiSort.x + guiSort.width + 5, guiSort.y - 3, 20, 20);

        List<String> pageList = Lists.newArrayList();
        for (int i = 1; i < this.pageCount + 1; i++) pageList.add("" + i);

        if (this.pageCount == 0) pageList.add("1");

        if (guiPage == null)
            guiPage = new GuiDropDown(this.sortRec.x + this.sortRec.width + 6, guiSort.y, 20, 13, pageList.toArray(new String[pageList.size()]));
        else {
            guiPage.x = this.sortRec.x + this.sortRec.width + 6;
            guiPage.y = guiSort.y;
            guiPage.options = pageList.toArray(new String[pageList.size()]);
        }
        guiPage.colorBG = new Color(206, 0, 204);
        guiPage.colorHighlight = guiPage.colorBG.darker().darker();


        this.sellBtn = new GuiMinebayBtn(0, this.guiPage.x + 25, this.guiTop + 1, "Sell", fontRenderer);
        this.myListingsBtn = new GuiMinebayBtn(1, this.sellBtn.x + 37, this.guiTop + 1, "   ", fontRenderer);
    }

    @Override
    public void updateScreen() {
        if (searchField != null) searchField.updateCursorCounter();
    }

}
