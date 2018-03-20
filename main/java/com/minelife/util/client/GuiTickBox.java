package com.minelife.util.client;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiTickBox extends Gui {

    private static final ResourceLocation switchOn = new ResourceLocation(Minelife.MOD_ID, "textures/gui/switch_on.png");
    private static final ResourceLocation switchOff = new ResourceLocation(Minelife.MOD_ID, "textures/gui/switch_off.png");

    public static final int WIDTH = 36, HEIGHT = 18;

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
        GlStateManager.enableTexture2D();
        GL11.glColor4f(1f, 1f, 1f, !enabled ? 155f/255f : 1f);
        mc.getTextureManager().bindTexture(value ? switchOn : switchOff);
        Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
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
