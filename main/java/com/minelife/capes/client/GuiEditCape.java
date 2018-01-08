package com.minelife.capes.client;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketEditCape;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class GuiEditCape extends GuiCreateCape {

    private String pixels;

    public GuiEditCape(ItemStack cape) {
        this.pixels = MLItems.cape.getPixels(cape);
        for (String p : pixels.split("\\;")) {
            if(!p.isEmpty()) {
                String[] data = p.split("\\,");
                pixelList.add(new Pixel(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        String text = "";
        for (Pixel pixel : pixelList) text += pixel.x + "," + pixel.y + "," + pixel.color + ";";

        Minelife.NETWORK.sendToServer(new PacketEditCape(text));

    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButton(0, colorPicker.xPosition + colorPicker.width + 20, colorPicker.yPosition, 40, 20, "Edit"));
    }
}
