package com.minelife.tutorial;

import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Set;

public class GuiTutorial extends GuiScreen {

    private static ResourceLocation bookTexture = new ResourceLocation("minecraft", "textures/gui/book.png");
    private Color sectionHighlightColor = new Color(0xFFA219);
    private int xPosition, yPosition, bookWidth = 146, bookHeight = 180;
    private Set<Section> sections;
    private Section section;
    private int pageIndex;

    public GuiTutorial(Set<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(bookTexture);
        drawTexturedModalRect(xPosition, yPosition, 20, 1, bookWidth, bookHeight);

        // there is nothing to draw
        if (sections.isEmpty() || (section != null && section.pages.isEmpty())) {
            fontRendererObj.drawString(EnumChatFormatting.BOLD + "Wow... Such empty!",
                    (this.width - fontRendererObj.getStringWidth(EnumChatFormatting.BOLD + "Wow... Such empty!")) / 2,
                    yPosition + ((bookHeight - fontRendererObj.FONT_HEIGHT) / 2), 0);
        } else {
            // draw main menu
            if (section == null) {
                int sectionY = 0;
                for (Section section : sections) {
                    if (mouseX >= xPosition + 20 && mouseY >= yPosition + 16 + sectionY &&
                            mouseX <= xPosition + 20 + bookWidth - 20 && mouseY <= yPosition + 16 + sectionY + fontRendererObj.FONT_HEIGHT) {
                        GuiUtil.drawDefaultBackground(xPosition + 15, yPosition + 12 + sectionY, bookWidth - 35, fontRendererObj.FONT_HEIGHT + 6, sectionHighlightColor);
                    }

                    fontRendererObj.drawString(section.name, xPosition + 20,
                            yPosition + 16 + sectionY, 0);

                    sectionY += 20;
                }
            } else {
                // draw page from .page file
                int yOffset = 0;
                for (String line : ((Page) section.pages.toArray()[pageIndex]).lines) {
                    if (line.startsWith("@image")) {
                        String[] data = line.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                        int posX = 0, posY = 0, picWidth = 16, picHeight = 16;
                        double scale = 1.0;
                        String picPath = "minecraft:textures/item/apple.png";

                        for (String d : data) {
                            if(d.contains("x=")) posX = NumberConversions.toInt(d.split("=")[1]);
                            if(d.contains("y=")) posY = NumberConversions.toInt(d.split("=")[1]);
                            if(d.contains("width=")) picWidth = NumberConversions.toInt(d.split("=")[1]);
                            if(d.contains("height=")) picHeight = NumberConversions.toInt(d.split("=")[1]);
                            if(d.contains("scale=")) scale = NumberConversions.toDouble(d.split("=")[1]);
                            if(d.contains("path=")) picPath = d.split("=")[1];
                        }

                        mc.getTextureManager().bindTexture(new ResourceLocation(picPath));
                        GL11.glPushMatrix();
                        GL11.glTranslatef(xPosition + posX, yPosition + posY, zLevel);
                        GL11.glTranslatef(picWidth / 2, picHeight / 2, zLevel);
                        GL11.glScaled(scale, scale, scale);
                        GL11.glTranslatef(-picWidth / 2, -picHeight / 2, zLevel);
                        GuiUtil.drawImage(0, 0, picWidth, picHeight);
                        GL11.glPopMatrix();
                    } else {
                        fontRendererObj.drawSplitString(StringHelper.ParseFormatting(line, '&'), xPosition + 18, yPosition + yOffset + 13, bookWidth - 30, 0xFFFFFF);
                        yOffset += (fontRendererObj.FONT_HEIGHT * fontRendererObj.listFormattedStringToWidth(StringHelper.ParseFormatting(line, '&'), bookWidth - 30).size());
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
    protected void keyTyped(char keyChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (section == null) {
                super.keyTyped(keyChar, keyCode);
            } else {
                section = null;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        if (section == null) {
            int sectionY = 0;
            for (Section section : sections) {
                if (mouseX >= xPosition + 20 && mouseY >= yPosition + 16 + sectionY &&
                        mouseX <= xPosition + 20 + bookWidth - 20 && mouseY <= yPosition + 16 + sectionY + fontRendererObj.FONT_HEIGHT) {
                    this.section = section;
                    pageIndex = 0;
                    break;
                }
                sectionY += 20;
            }
        } else {
            int arrowWidth = 18, arrowHeight = 10;
            if (mouseX >= xPosition + bookWidth - arrowWidth - 25 && mouseX <= xPosition + bookWidth - 25 &&
                    mouseY >= yPosition + bookHeight - arrowHeight - 15 && mouseY <= yPosition + bookHeight - 15) {
                if (pageIndex + 1 < section.pages.size()) pageIndex++;
            } else if (mouseX >= xPosition + arrowWidth && mouseX <= xPosition + arrowWidth + arrowWidth &&
                    mouseY >= yPosition + bookHeight - arrowHeight - 15 && mouseY <= yPosition + bookHeight - arrowHeight - 15 + arrowHeight) {
                if (pageIndex - 1 > -1) pageIndex--;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.xPosition = (this.width - bookWidth) / 2;
        this.yPosition = (this.height - bookHeight) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    public void drawArrow(boolean next, int mouseX, int mouseY) {
        int arrowWidth = 18, arrowHeight = 10;
        mc.getTextureManager().bindTexture(bookTexture);

        if (next) {
            if (mouseX >= xPosition + bookWidth - arrowWidth - 25 && mouseX <= xPosition + bookWidth - 25 &&
                    mouseY >= yPosition + bookHeight - arrowHeight - 15 && mouseY <= yPosition + bookHeight - 15) {
                drawTexturedModalRect(xPosition + bookWidth - arrowWidth - 25, yPosition + bookHeight - arrowHeight - 15,
                        26, 194, arrowWidth, arrowHeight);
            } else {
                drawTexturedModalRect(xPosition + bookWidth - arrowWidth - 25, yPosition + bookHeight - arrowHeight - 15,
                        3, 194, arrowWidth, arrowHeight);
            }
        } else {
            if (mouseX >= xPosition + arrowWidth && mouseX <= xPosition + arrowWidth + arrowWidth &&
                    mouseY >= yPosition + bookHeight - arrowHeight - 15 && mouseY <= yPosition + bookHeight - arrowHeight - 15 + arrowHeight) {
                drawTexturedModalRect(xPosition + arrowWidth, yPosition + bookHeight - arrowHeight - 15,
                        26, 207, arrowWidth, arrowHeight);
            } else {
                drawTexturedModalRect(xPosition + arrowWidth, yPosition + bookHeight - arrowHeight - 15,
                        3, 207, arrowWidth, arrowHeight);
            }
        }
    }
}
