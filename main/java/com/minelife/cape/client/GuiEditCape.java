package com.minelife.cape.client;

import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.network.PacketSetPixels;
import com.minelife.util.client.GuiColorPicker;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiPixelArt;
import com.minelife.util.client.Pixel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiEditCape extends GuiScreen {

    private static ResourceLocation template = new ResourceLocation(Minelife.MOD_ID, "textures/capes/template.png");
    protected GuiPixelArt GuiPixelArt;
    private int inventorySlot;
    private ItemStack capeStack;

    public GuiEditCape(ItemStack capeStack, int slot) {
        this.capeStack = capeStack;
        this.inventorySlot = slot;
    }

    //TODO: On Auto Scale it's too big... Cuts off on top of screen
    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(GuiPixelArt.X(), GuiPixelArt.Y(), zLevel);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glScalef(GuiPixelArt.Scale(), GuiPixelArt.Scale(), GuiPixelArt.Scale());
            mc.getTextureManager().bindTexture(template);
            GuiHelper.drawImage(0, 0, GuiPixelArt.Width(), GuiPixelArt.Height(), template);
        }
        GL11.glPopMatrix();

        GuiPixelArt.drawScreen(mouse_x, mouse_y);
    }

    @Override
    protected void actionPerformed(GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        String text = "";
        for (Pixel pixel : GuiPixelArt.Pixels) text += pixel.x + "," + pixel.y + "," + pixel.color + ";";

        Minelife.getNetwork().sendToServer(new PacketSetPixels(this.inventorySlot, text));
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scale = scaledResolution.getScaleFactor() + 5;
        int canvasHeight = 17 * scale;
        int x = ((this.width - (22 * scale)) / 2);
        int y = ((height - canvasHeight) / 2) - ((this.height / 2) / 2);
        GuiColorPicker ColorPicker = new GuiColorPicker((width - 100) / 2, ((this.height - 80) / 2) + ((this.height / 2) / 2), 100, 80, 0, 0, 0);
        GuiPixelArt = new GuiPixelArt(mc, x, y, 22, 17, scale, ColorPicker);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, ColorPicker.xPosition + ColorPicker.width + 20, ColorPicker.yPosition, 40, 20, "Create"));

        if(ModCapes.itemCape.getPixels(capeStack) != null) {
            for (String p : ModCapes.itemCape.getPixels(capeStack).split("\\;")) {
                if (!p.isEmpty()) {
                    String[] data = p.split("\\,");
                    GuiPixelArt.Pixels.add(new Pixel(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                }
            }
        }

    }
}