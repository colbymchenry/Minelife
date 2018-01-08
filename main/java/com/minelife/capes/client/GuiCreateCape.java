package com.minelife.capes.client;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.util.Set;

public class GuiCreateCape extends GuiScreen {

    private static ResourceLocation eraser = new ResourceLocation(Minelife.MOD_ID, "textures/gui/eraser.png");
    private static ResourceLocation template = new ResourceLocation(Minelife.MOD_ID, "textures/capes/template.png");

    private boolean erasing = false;
    protected GuiColorPicker colorPicker;
    private Rectangle canvasBounds;
    protected Set<Pixel> pixelList = Sets.newTreeSet();
    private int scale = 10;
    private GuiButton eraseBtn;


    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(canvasBounds.getX(), canvasBounds.getY(), zLevel);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glScalef(scale, scale, scale);
            mc.getTextureManager().bindTexture(template);
            GuiUtil.drawImage(0, 0, canvasBounds.getWidth(), canvasBounds.getHeight());

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            for (Pixel p : pixelList) {
                int rgb = p.color;
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                GL11.glColor4f(red / 255f, green / 255f, blue / 255f, 1f);
                GuiUtil.drawImage(p.x, p.y, 1, 1);
            }
        }
        GL11.glPopMatrix();

        colorPicker.drawScreen(mouseX, mouseY, f);

        GL11.glColor4f(1, 1, 1, 1);


        if (erasing) {
            eraseBtn.drawButton(mc, eraseBtn.xPosition + 2, eraseBtn.yPosition + 2);
            mc.getTextureManager().bindTexture(eraser);
            GL11.glColor4f(1, 1, 1, 1);
            GuiUtil.drawImage(eraseBtn.xPosition + 2, eraseBtn.yPosition + 2, 16, 16);
            GuiUtil.drawImage(mouseX, mouseY, 16, 16);
        } else {
            eraseBtn.drawButton(mc, mouseX, mouseY);
            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(eraser);
            GuiUtil.drawImage(eraseBtn.xPosition + 2, eraseBtn.yPosition + 2, 16, 16);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        int r = (colorPicker.getColor()>>16)&0xFF;
        int g = (colorPicker.getColor()>>8)&0xFF;
        int b = (colorPicker.getColor()>>0)&0xFF;
        GL11.glColor4f(r / 255f, g / 255f, b / 255f, 1f);
        GuiUtil.drawImage(colorPicker.xPosition, colorPicker.yPosition, colorPicker.width, 10);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        super.mouseClicked(mouseX, mouseY, mouseBtn);

        if(eraseBtn.mousePressed(mc, mouseX, mouseY)) erasing = !erasing;

        if (mouseX >= canvasBounds.getX() && mouseX <= canvasBounds.getX() + (canvasBounds.getWidth() * scale) - 1 &&
                mouseY >= canvasBounds.getY() && mouseY <= canvasBounds.getY() + (canvasBounds.getHeight() * scale) - 1) {
            Pixel p = new Pixel((mouseX - canvasBounds.getX()) / scale, (mouseY - canvasBounds.getY()) / scale, colorPicker.getColor());
            System.out.println(p.x + "," + p.y);
            if (erasing) {
                pixelList.remove(p);
            } else {
                pixelList.remove(p);
                pixelList.add(p);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseBtn, long timeSinceMouseClick) {
        if (mouseX >= canvasBounds.getX() && mouseX <= canvasBounds.getX() + (canvasBounds.getWidth() * scale) - 1 &&
                mouseY >= canvasBounds.getY() && mouseY <= canvasBounds.getY() + (canvasBounds.getHeight() * scale) - 1) {
            Pixel p = new Pixel((mouseX - canvasBounds.getX()) / scale, (mouseY - canvasBounds.getY()) / scale, colorPicker.getColor());

            if (erasing) {
                pixelList.remove(p);
            } else {
                pixelList.remove(p);
                pixelList.add(p);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);

        String text = "";
        for (Pixel pixel : pixelList) text += pixel.x + "," + pixel.y + "," + pixel.color + ";";

        Minelife.NETWORK.sendToServer(new PacketCreateCape(text));
    }

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution scaledResolution = new ScaledResolution(mc, width, height);
        scale = scaledResolution.getScaleFactor() + 5;
        int canvasHeight = 17 * scale;
        canvasBounds = new Rectangle(((this.width - (22 * scale)) / 2), ((height - canvasHeight) / 2) - ((this.height / 2) / 2), 22, 17);

        colorPicker = new GuiColorPicker((width - 100) / 2, ((this.height - 80) / 2) + ((this.height / 2) / 2), 100, 80, 0, 0, 0);

        buttonList.clear();
        buttonList.add(new GuiButton(0, colorPicker.xPosition + colorPicker.width + 20, colorPicker.yPosition, 40, 20, "Create"));
        eraseBtn = new GuiButton(1, colorPicker.xPosition + colorPicker.width + 20, colorPicker.yPosition + 30, 20, 20, "");

    }

    public class Pixel implements Comparable<Pixel> {
        int x, y, color;

        public Pixel(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public int compareTo(Pixel o) {
            int result = o.x - x;
            // on the same x
            if(result == 0) {
                result = o.y - y;
                // on the same y
                if(result == 0) {
                    return 0;
                }
            }

            return result;
        }
    }

}
