package com.minelife.tutorial;

import com.minelife.Minelife;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

public class GuiTutorial extends GuiScreen {

    private static ResourceLocation holoArrowRightTex = new ResourceLocation(Minelife.MOD_ID, "textures/gui/holo_arrow_right.png");
    private static ResourceLocation holoArrowLeftTex = new ResourceLocation(Minelife.MOD_ID, "textures/gui/holo_arrow_left.png");
    private int guiLeft, guiTop, guiWidth = 230, guiHeight = 230;
    private Set<Section> sections;
    private static Section section;
    private static int pageIndex;

    public GuiTutorial(Set<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(77f / 255f, 77f / 255f, 77f / 255f, 200f / 255f);
        GuiHelper.drawRect(guiLeft, guiTop, guiWidth, guiHeight);
        GlStateManager.color(22f / 255f, 22f / 255f, 22f / 255f, 200f / 255f);
        GuiHelper.drawRect(guiLeft + 2, guiTop + 2, guiWidth - 4, guiHeight - 4);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);

        if (section == null)
            drawCenteredString(fontRenderer, TextFormatting.UNDERLINE + "Help", width / 2, guiTop + 10, 0xFFFFFF);


        // there is nothing to draw
        if (sections.isEmpty() || (section != null && section.pages.isEmpty())) {
            fontRenderer.drawString(TextFormatting.BOLD + "Wow... Such empty!",
                    (this.width - fontRenderer.getStringWidth(TextFormatting.BOLD + "Wow... Such empty!")) / 2,
                    guiTop + ((guiHeight - fontRenderer.FONT_HEIGHT) / 2), 0);
        } else {
            // draw main menu
            if (section == null) {
                int sectionY = 10;

                for (Section section : sections) {
                    if (mouseX >= guiLeft + 20 && mouseY >= guiTop + 16 + sectionY &&
                            mouseX <= guiLeft + 20 + guiWidth - 20 && mouseY <= guiTop + 16 + sectionY + fontRenderer.FONT_HEIGHT) {
                        GlStateManager.disableTexture2D();
                        GlStateManager.color(77f / 255f, 77f / 255f, 77f / 255f, 200f / 255f);
                        GuiHelper.drawRect(guiLeft + 15, guiTop + 13 + sectionY, guiWidth - 35, fontRenderer.FONT_HEIGHT + 5);
                        GlStateManager.enableTexture2D();
                        GlStateManager.color(1, 1, 1, 1);
                    }
                    fontRenderer.drawString(section.name.equals("ZZZ") ? "What do I do?" : section.name, guiLeft + 20,
                            guiTop + 16 + sectionY, 0xFFFFFF);

                    sectionY += 20;
                }

            } else {
                // draw page from .page file
                int yOffset = 0;
                for (String line : ((Page) section.pages.toArray()[pageIndex]).lines) {
                    if (line.startsWith("@image")) {
                        GL11.glColor4f(1, 1, 1, 1);
                        String[] data = line.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                        int posX = 0, posY = 0, picWidth = 16, picHeight = 16;
                        double scale = 1.0;
                        String picPath = "minecraft:textures/item/apple.png";

                        for (String d : data) {
                            if (d.contains("x=")) posX = NumberConversions.toInt(d.split("=")[1]);
                            if (d.contains("y=")) posY = NumberConversions.toInt(d.split("=")[1]);
                            if (d.contains("width=")) picWidth = NumberConversions.toInt(d.split("=")[1]);
                            if (d.contains("height=")) picHeight = NumberConversions.toInt(d.split("=")[1]);
                            if (d.contains("scale=")) scale = NumberConversions.toDouble(d.split("=")[1]);
                            if (d.contains("path=")) picPath = d.split("=")[1];
                        }

                        mc.getTextureManager().bindTexture(new ResourceLocation(picPath));
                        GL11.glPushMatrix();
                        GL11.glTranslatef(guiLeft + posX, guiTop + posY, zLevel);
                        GL11.glTranslatef(picWidth / 2, picHeight / 2, zLevel);
                        GL11.glScaled(scale, scale, scale);
                        GL11.glTranslatef(-picWidth / 2, -picHeight / 2, zLevel);
                        GuiHelper.drawImage(0, 0, picWidth, picHeight, new ResourceLocation(picPath));
                        GL11.glPopMatrix();
                    } else {
                        if (line.startsWith("center("))
                            drawCenteredString(fontRenderer, StringHelper.ParseFormatting(line, '&').replace("center(", "").replace(")", ""), width / 2, guiTop + yOffset + 13, 0xFFFFFF);
                        else
                            fontRenderer.drawSplitString(StringHelper.ParseFormatting(line, '&'), guiLeft + 18, guiTop + yOffset + 13, guiWidth - 30, 0xFFFFFF);
                        yOffset += (fontRenderer.FONT_HEIGHT * fontRenderer.listFormattedStringToWidth(StringHelper.ParseFormatting(line, '&'), guiWidth - 30).size());
                    }
                }

                // draw arrows
                GL11.glColor4f(1, 1, 1, 1);

                if (pageIndex == 0 && pageIndex == section.pages.size() - 1) return;

                if (pageIndex == 0) {
                    drawArrow(true, mouseX, mouseY);
                } else if (pageIndex == section.pages.size() - 1) {
                    drawArrow(false, mouseX, mouseY);
                } else if (pageIndex > 0 && pageIndex < section.pages.size()) {
                    drawArrow(true, mouseX, mouseY);
                    drawArrow(false, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (section == null) {
                super.keyTyped(keyChar, keyCode);
            } else {
                section = null;
            }
        }
    }

    // TODO: Implement links
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        if (section == null) {
            int sectionY = 10;
            for (Section section : sections) {
                if (mouseX >= guiLeft + 20 && mouseY >= guiTop + 16 + sectionY &&
                        mouseX <= guiLeft + 20 + guiWidth - 20 && mouseY <= guiTop + 16 + sectionY + fontRenderer.FONT_HEIGHT) {
                    this.section = section;
                    pageIndex = 0;
                    break;
                }
                sectionY += 20;
            }
        } else {
            int arrowWidth = 16, arrowHeight = 16;
            if (mouseX >= guiLeft + guiWidth - arrowWidth - 5 && mouseX <= guiLeft + guiWidth - 5 &&
                    mouseY >= guiTop + guiHeight - arrowHeight - 5 && mouseY <= guiTop + guiHeight - 5) {
                if (pageIndex + 1 < section.pages.size()) pageIndex++;
            } else if (mouseX >= guiLeft + 5 && mouseX <= guiLeft + 5 + arrowWidth &&
                    mouseY >= guiTop + guiHeight - arrowHeight - 5 && mouseY <= guiTop + guiHeight - arrowHeight - 5 + arrowHeight) {
                if (pageIndex - 1 > -1) pageIndex--;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    public void drawArrow(boolean next, int mouseX, int mouseY) {
        int arrowWidth = 16, arrowHeight = 16;

        if (next) {
            mc.getTextureManager().bindTexture(holoArrowRightTex);
            if (mouseX >= guiLeft + guiWidth - arrowWidth - 5 && mouseX <= guiLeft + guiWidth - 5 &&
                    mouseY >= guiTop + guiHeight - arrowHeight - 5 && mouseY <= guiTop + guiHeight - 5) {
                GuiHelper.drawImage(guiLeft + guiWidth - arrowWidth - 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowRightTex);
                GuiHelper.drawImage(guiLeft + guiWidth - arrowWidth - 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowRightTex);
                GuiHelper.drawImage(guiLeft + guiWidth - arrowWidth - 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowRightTex);
            } else {
                GuiHelper.drawImage(guiLeft + guiWidth - arrowWidth - 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowRightTex);
            }
        } else {
            mc.getTextureManager().bindTexture(holoArrowLeftTex);
            if (mouseX >= guiLeft + 5 && mouseX <= guiLeft + 5 + arrowWidth &&
                    mouseY >= guiTop + guiHeight - arrowHeight - 5 && mouseY <= guiTop + guiHeight - arrowHeight - 5 + arrowHeight) {
                GuiHelper.drawImage(guiLeft + 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowLeftTex);
                GuiHelper.drawImage(guiLeft + 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowLeftTex);
                GuiHelper.drawImage(guiLeft + 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowLeftTex);
            } else {
                GuiHelper.drawImage(guiLeft + 5, guiTop + guiHeight - arrowHeight - 5, 16, 16, holoArrowLeftTex);
            }
        }
    }

    private void openWebLink(String url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, new URI(url));
        }
        catch (Throwable throwable1)
        {
        }
    }

}
