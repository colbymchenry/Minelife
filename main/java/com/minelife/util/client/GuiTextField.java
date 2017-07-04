package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiTextField extends Gui {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final FontRenderer fontRenderer = mc.fontRenderer;
    private final Rectangle bounds;
    private String text = "";
    private int cursorPos = 0;
    private boolean focused = false;
    private boolean unicodeFlag = false;
    private boolean enabled = true;

    public GuiTextField(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(int x, int y, int width, int height) {
        bounds.setBounds(x, y, width, height);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUnicodeFlag(boolean unicodeFlag) {
        this.unicodeFlag = unicodeFlag;
    }

    public boolean isUnicodeFlagEnabled() {
        return unicodeFlag;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void drawTextBox() {
        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        drawBackground();
        fontRenderer.drawSplitString(text, bounds.getX(), bounds.getY(), bounds.getWidth(), 0xFFFFFF);

        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(false);

        if(isFocused()) {
            drawCursor();
        }

    }

    public void textboxKeyTyped(char typedChar, int keyCode) {
        if(!isFocused()) return;
        if(!isEnabled()) return;

        Pattern p = Pattern.compile("[^a-z0-9 ^!-+ ^`~ ^_= - , ? !]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("" + typedChar);
        boolean b = m.find();

        if(b && keyCode != Keyboard.KEY_BACK) return;

        if(keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT) {
            cursorPos = keyCode == Keyboard.KEY_LEFT && cursorPos - 1 > -1 ? cursorPos - 1 : keyCode == Keyboard.KEY_RIGHT && cursorPos + 1 < text.length() + 1 ? cursorPos + 1 : cursorPos;
            return;
        }

        try {
            StringBuilder builder = new StringBuilder(text);
            if (keyCode != Keyboard.KEY_BACK)
                builder.insert(cursorPos++, typedChar);
            else if (!text.isEmpty())
                builder.deleteCharAt(--cursorPos);

            text = builder.toString();
        } catch (Exception e) {
        }
    }

    public void mouseClicked(int mouseX, int mouseY) {

        if(!isEnabled()) return;

        setFocused(bounds.contains(mouseX, mouseY));

        if(!isFocused()) return;

        if(text.isEmpty()) {
            cursorPos = 0;
            return;
        }

        if(unicodeFlag)
         fontRenderer.setUnicodeFlag(true);

        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, bounds.getWidth());

        int line = (mouseY - bounds.getY()) / fontRenderer.FONT_HEIGHT;
        line = line > formattedStringList.size() - 1 ? formattedStringList.size() - 1 : line;

        cursorPos = 0;

        for (int i = 0; i < line; i++) {
            cursorPos += formattedStringList.get(i).length();
            if (text.charAt(cursorPos) == ' ') cursorPos += 1;
        }

        int x = mouseX - bounds.getX();
        x = x > bounds.getWidth() ? bounds.getWidth() : x < 0 ? 0 : x;
        cursorPos += fontRenderer.trimStringToWidth(formattedStringList.get(line), x).length() + 1;
        cursorPos = cursorPos > text.length() ? text.length() : cursorPos < 0 ? 0 : cursorPos;

        if(!unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
    }

    public void update() {

    }

    public void initGui() {

    }

    private void drawCursor() {
        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, bounds.getWidth());

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

            int x = bounds.getX() + strWidth;
            int y = bounds.getY() + (line * fontRenderer.FONT_HEIGHT);
            Gui.drawRect(x, y - 1, x + 1, y + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
        } catch (Exception e) {
        }

        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(false);
    }

    public void drawBackground() {
        drawRect(bounds.getX() - 2, bounds.getY() - 2, bounds.getX() + bounds.getWidth() + 2, bounds.getY() + bounds.getHeight() + 2, -6250336);
        drawRect(bounds.getX() - 1, bounds.getY() - 1, bounds.getX() + bounds.getWidth() + 1, bounds.getY() + bounds.getHeight() + 1, -16777216);
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public int getCursorLine() {
        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(true);
        List<String> formattedStringList = fontRenderer.listFormattedStringToWidth(text, bounds.getWidth());

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

        if(unicodeFlag)
            fontRenderer.setUnicodeFlag(false);

        return 0;
    }

}
