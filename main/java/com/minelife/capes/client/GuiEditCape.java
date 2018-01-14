package com.minelife.capes.client;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketEditCape;
import com.minelife.util.client.GuiPixelArt;
import com.minelife.util.client.Pixel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class GuiEditCape extends GuiCreateCape {

    private String pixels;

    public GuiEditCape(ItemStack cape) {
        this.pixels = MLItems.cape.getPixels(cape);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        String text = "";
        for (Pixel pixel : GuiPixelArt.Pixels) text += pixel.x + "," + pixel.y + "," + pixel.color + ";";

        Minelife.NETWORK.sendToServer(new PacketEditCape(text));

    }

    @Override
    public void initGui() {
        super.initGui();

        for (String p : pixels.split("\\;")) {
            if(!p.isEmpty()) {
                String[] data = p.split("\\,");
                GuiPixelArt.Pixels.add(new Pixel(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        }

        buttonList.clear();
        buttonList.add(new GuiButton(0, GuiPixelArt.ColorPicker().xPosition + GuiPixelArt.ColorPicker().width + 20, GuiPixelArt.ColorPicker().yPosition, 40, 20, "Edit"));
    }
}
