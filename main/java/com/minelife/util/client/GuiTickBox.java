package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTickBox extends Gui {

    private static final ResourceLocation switchOn = new ResourceLocation(Minelife.MOD_ID, "textures/gui/switchOn.png");
    private static final ResourceLocation switchOff = new ResourceLocation(Minelife.MOD_ID, "textures/gui/switchOff.png");

    private static final int WIDTH = 36, HEIGHT = 18;

    public int xPosition, yPosition;
    private boolean value;
    private Minecraft mc;

    public boolean enabled = true;
    public String key;

    public GuiTickBox(Minecraft mc, int xPosition, int yPosition, boolean value) {
        this.mc = mc;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.value = value;
    }

    public GuiTickBox(Minecraft mc, int xPosition, int yPosition, boolean value, String key) {
        this(mc, xPosition, yPosition, value);
        this.key = key;
    }

    public void drawTickBox() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, !enabled ? 155f/255f : 1f);
        GL11.glEnable(GL11.GL_BLEND);

        mc.getTextureManager().bindTexture(value ? switchOn : switchOff);
        GuiUtil.drawImage(xPosition, yPosition, WIDTH, HEIGHT);
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (enabled && mouseX >= xPosition && mouseX <= xPosition + WIDTH && mouseY >= yPosition && mouseY <= yPosition + HEIGHT) {
            value = !value;
            return true;
        }

        return false;
    }

    public boolean isChecked() {
        return value;
    }

    public void setChecked(boolean value) {
        this.value = value;
    }

}
