package com.minelife.tutorial;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiUtil;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class GuiTutorial extends GuiScreen {

    private static ResourceLocation bookTexture = new ResourceLocation("minecraft", "textures/gui/book.png");
    private Color sectionHighlightColor = new Color(0xFFA219);
    private int xPosition, yPosition, bookWidth = 146, bookHeight = 180;
    private File section, page;
    private List<File> sections, pages;

    public GuiTutorial() {
        sections = Lists.newArrayList();
        pages = Lists.newArrayList();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(bookTexture);
        drawTexturedModalRect(xPosition, yPosition, 20, 1, bookWidth, bookHeight);

        if (sections.isEmpty() && pages.isEmpty()) {
            fontRendererObj.drawString(EnumChatFormatting.BOLD + "Wow... Such empty!",
                    (this.width - fontRendererObj.getStringWidth(EnumChatFormatting.BOLD + "Wow... Such empty!")) / 2,
                    yPosition + ((bookHeight - fontRendererObj.FONT_HEIGHT) / 2), 0);
        } else {
            if (section == null && page == null) {
                int sectionY = 0;
                for (File file : sections) {
                    if (mouseX >= xPosition + 20 && mouseY >= yPosition + 16 + sectionY &&
                            mouseX <= xPosition + 20 + bookWidth - 20 && mouseY <= yPosition + 16 + sectionY + fontRendererObj.FONT_HEIGHT) {
                        GuiUtil.drawDefaultBackground(xPosition + 15, yPosition + 12 + sectionY, bookWidth - 35, fontRendererObj.FONT_HEIGHT + 6, sectionHighlightColor);
                    }

                    fontRendererObj.drawString(file.getName().replaceAll(".section", ""), xPosition + 20,
                            yPosition + 16 + sectionY, 0);

                    sectionY += 20;
                }
            } else {
                try {
                    Scanner scanner = new Scanner(page);
                    int yOffset = 0;
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if(line.startsWith("@image")) {
                            String[] data = line.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                            int posX = NumberConversions.toInt(data[0].split("=")[1]);
                            int posY = NumberConversions.toInt(data[1].split("=")[1]);
                            int picWidth = NumberConversions.toInt(data[2].split("=")[1]);
                            int picHeight = NumberConversions.toInt(data[3].split("=")[1]);
                            String picPath = data[4].split("=")[1];
                            mc.getTextureManager().bindTexture(new ResourceLocation(picPath));
                            GuiUtil.drawImage(posX, posY, picWidth, picHeight);
                        } else {
                            fontRendererObj.drawString(StringHelper.ParseFormatting(line, '&'), xPosition, yPosition + yOffset, 0xFFFFFF);
                        }
                        yOffset += 14;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        if (section == null && page == null) {
            int sectionY = 0;
            // TODO: Breaks here.
            for (File file : sections) {
                if (mouseX >= xPosition + 20 && mouseY >= yPosition + 16 + sectionY &&
                        mouseX <= xPosition + 20 + bookWidth - 20 && mouseY <= yPosition + 16 + sectionY + fontRendererObj.FONT_HEIGHT) {
                    section = file;
                    sections.clear();
                    sections.addAll(ModTutorial.getSections(section));
                    pages.clear();
                    pages.addAll(ModTutorial.getPages(section));
                }
                sectionY += 20;
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.xPosition = (this.width - bookWidth) / 2;
        this.yPosition = (this.height - bookHeight) / 2;

        if (section == null || page == null) {
            sections.addAll(ModTutorial.getSections(new File(Minelife.getConfigDirectory(), "tutorials")));
            pages.addAll(ModTutorial.getSections(new File(Minelife.getConfigDirectory(), "tutorials")));
        }

//        final MediaPlayer oracleVid = new MediaPlayer(
//                new Media("https://www.youtube.com/embed?v=cBi3m27a30w")
//        );
//        Display.getParent().get.setScene(new Scene(new Group(new MediaView(oracleVid)), 640, 480));
//        stage.show();
//
//        oracleVid.play();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    public void drawArrow(boolean next, boolean enabled) {
        int arrowWidth = 18, arrowHeight = 10;

        mc.getTextureManager().bindTexture(bookTexture);
        if (next) {
            if (enabled) {
                drawTexturedModalRect(xPosition + bookWidth - arrowWidth, yPosition + bookHeight - arrowHeight,
                        26, 194, bookWidth, bookHeight);
            } else {
                drawTexturedModalRect(xPosition + bookWidth - arrowWidth, yPosition + bookHeight - arrowHeight,
                        3, 194, bookWidth, bookHeight);
            }
        } else {
            if (enabled) {
                drawTexturedModalRect(xPosition + arrowWidth, yPosition + bookHeight - arrowHeight,
                        26, 207, bookWidth, bookHeight);
            } else {
                drawTexturedModalRect(xPosition + arrowWidth, yPosition + bookHeight - arrowHeight,
                        3, 207, bookWidth, bookHeight);
            }
        }
    }
}
