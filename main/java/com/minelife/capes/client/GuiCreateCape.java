package com.minelife.capes.client;

import com.minelife.Minelife;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.util.client.GuiColorPicker;
import com.minelife.util.client.GuiPixelArt;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.Pixel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCreateCape extends GuiScreen {

    private static ResourceLocation template = new ResourceLocation(Minelife.MOD_ID, "textures/capes/template.png");
    protected GuiPixelArt GuiPixelArt;

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(GuiPixelArt.X(), GuiPixelArt.Y(), zLevel);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glScalef(GuiPixelArt.Scale(), GuiPixelArt.Scale(), GuiPixelArt.Scale());
            mc.getTextureManager().bindTexture(template);
            GuiUtil.drawImage(0, 0, GuiPixelArt.Width(), GuiPixelArt.Height());
        }
        GL11.glPopMatrix();

        GuiPixelArt.drawScreen(mouse_x, mouse_y);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        GuiPixelArt.mouseClicked(mouse_x, mouse_y);
    }

    @Override
    protected void mouseClickMove(int mouse_x, int mouse_y, int p_146273_3_, long p_146273_4_) {
        super.mouseClickMove(mouse_x, mouse_y, p_146273_3_, p_146273_4_);
        GuiPixelArt.mouseClickMove(mouse_x, mouse_y);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);

        String text = "";
        for (Pixel pixel : GuiPixelArt.Pixels) text += pixel.x + "," + pixel.y + "," + pixel.color + ";";

        Minelife.NETWORK.sendToServer(new PacketCreateCape(text));
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution scaledResolution = new ScaledResolution(mc, width, height);
        int scale = scaledResolution.getScaleFactor() + 5;
        int canvasHeight = 17 * scale;
        int x = ((this.width - (22 * scale)) / 2);
        int y = ((height - canvasHeight) / 2) - ((this.height / 2) / 2);
        GuiColorPicker ColorPicker = new GuiColorPicker((width - 100) / 2, ((this.height - 80) / 2) + ((this.height / 2) / 2), 100, 80, 0, 0, 0);
        GuiPixelArt = new GuiPixelArt(mc, x, y, 22, 17, scale, ColorPicker);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, ColorPicker.xPosition + ColorPicker.width + 20, ColorPicker.yPosition, 40, 20, "Create"));
    }
}
