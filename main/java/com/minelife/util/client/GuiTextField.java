package com.minelife.util.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuiTextField extends Gui {

    public String text = "";
    public boolean focused, unicodeFlag, ignoreHeight = false, enabled = true;
    private int cursorCounter, cursorPos;
    public int xPosition, yPosition, width, height;
    public FontRenderer fontRenderer;

    public GuiTextField(FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.fontRenderer = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(int x, int y) {
        return x >= xPosition && x <= xPosition + width && y >= yPosition && y <= yPosition + height;
    }

    public void drawTextBox() {
        GlStateManager.disableLighting();
        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        drawBackground();
        fontRenderer.drawSplitString(text, xPosition, yPosition, width, 0xFFFFFF);
        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
        if (focused) {
            drawCursor();
        }
        GlStateManager.enableLighting();
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (!focused) return;
        if (!enabled) return;
        Pattern p = Pattern.compile("[^a-z0-9 ^!-+ ^`~ ^_= - , ? !]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("" + typedChar);
        boolean b = m.find();
        if (b && keyCode != Keyboard.KEY_BACK) return;
        if (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT) {
            cursorPos = keyCode == Keyboard.KEY_LEFT && cursorPos - 1 > -1 ? cursorPos - 1 : keyCode == Keyboard.KEY_RIGHT && cursorPos + 1 < text.length() + 1 ? cursorPos + 1 : cursorPos;
            return;
        }
        try {
            StringBuilder builder = new StringBuilder(text);
            if (keyCode != Keyboard.KEY_BACK) {
                if (!ignoreHeight) {
                    if (unicodeFlag) fontRenderer.setUnicodeFlag(true);
                    int lines = fontRenderer.listFormattedStringToWidth(text + typedChar, width).size();
                    if (lines * fontRenderer.FONT_HEIGHT >= height) {
                        if (unicodeFlag) fontRenderer.setUnicodeFlag(false);
                        return;
                    }
                    if (unicodeFlag) fontRenderer.setUnicodeFlag(false);
                }
                builder.insert(cursorPos++, typedChar);
            } else if (!text.isEmpty())
                builder.deleteCharAt(--cursorPos);
            text = builder.toString();
        } catch (Exception e) {
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {
        if (!enabled) return;
        setFocused(contains(mouseX, mouseY));
        if (!focused) return;
        if (text.isEmpty()) {
            cursorPos = 0;
            return;
        }

        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(true);

        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, width);
        int line = (mouseY - yPosition) / fontRenderer.FONT_HEIGHT;
        line = line > formattedStringList.size() - 1 ? formattedStringList.size() - 1 : line;
        cursorPos = 0;
        for (int i = 0; i < line; i++) {
            cursorPos += formattedStringList.get(i).length();
            if (text.charAt(cursorPos) == ' ') cursorPos += 1;
        }
        int x = mouseX - xPosition;
        x = x > width ? width : x < 0 ? 0 : x;
        cursorPos += fontRenderer.trimStringToWidth(formattedStringList.get(line), x).length() + 1;
        cursorPos = cursorPos > text.length() ? text.length() : cursorPos < 0 ? 0 : cursorPos;
        if (!unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
    }

    public void update() {
        ++this.cursorCounter;
    }

    private void drawCursor() {
        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, width);

        try {
            int cursor = 0;
            int line = 0;
            for (int i = 0; i < formattedStringList.size(); i++) {
                cursor += formattedStringList.get(i).length();
                if (cursor == text.length()) {
                    line = i;
                    break;
                }

                if (text.charAt(cursor) == ' ') cursor += 1;

                if (cursor > cursorPos) {
                    line = i;
                    break;
                }
            }

            int count = 0;
            for (int i = 0; i < line; i++) {
                count += formattedStringList.get(i).length();
                if (text.charAt(count) == ' ') count += 1;
            }

            int strWidth = fontRenderer.getStringWidth(formattedStringList.get(line).substring(0, cursorPos - count));
            int x = xPosition + strWidth;
            int y = yPosition + (line * fontRenderer.FONT_HEIGHT);
            boolean drawCursor = this.focused && this.cursorCounter / 6 % 2 == 0;
            if (drawCursor) {
                GlStateManager.color(0, 0, 1, 1);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
                GL11.glLogicOp(GL11.GL_OR_REVERSE);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos((double) x, (double) y + 1 + this.fontRenderer.FONT_HEIGHT, 0.0D);
                bufferbuilder.pos((double) x + 1, (double) y + this.fontRenderer.FONT_HEIGHT, 0.0D);
                bufferbuilder.pos((double) x + 1, (double) y - 1, 0.0D);
                bufferbuilder.pos((double) x, (double) y - 1, 0.0D);
                tessellator.draw();
                GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            //            Gui.drawRect(x, y - 1, x + 1, y + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
        } catch (Exception e) {
            cursorPos -= 1;
            if (cursorPos < 0) cursorPos = 0;
        }

        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
    }

    public void drawBackground() {
        drawRect(xPosition - 2, yPosition - 2, xPosition + width + 2, yPosition + height + 2, -6250336);
        drawRect(xPosition - 1, yPosition - 1, xPosition + width + 1, yPosition + height + 1, -16777216);
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        this.cursorCounter = 0;
    }

    public int getCursorLine() {
        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, width);
        try {
            int cursor = 0;
            int line = 0;
            for (int i = 0; i < formattedStringList.size(); i++) {
                cursor += formattedStringList.get(i).length();
                if (cursor == text.length()) {
                    line = i;
                    break;
                }
                if (text.charAt(cursor) == ' ') cursor += 1;
                if (cursor > cursorPos) {
                    line = i;
                    break;
                }
            }
            return line;
        } catch (Exception e) {
        }
        if (unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
        return 0;
    }
}