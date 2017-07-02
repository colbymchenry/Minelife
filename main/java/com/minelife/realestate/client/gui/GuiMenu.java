package com.minelife.realestate.client.gui;

import com.minelife.Minelife;
import com.minelife.realestate.client.packet.PacketBuyChunk;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;

public class GuiMenu extends BaseGui {

    private int pricePerChunk;

    public GuiMenu(int pricePerChunk)
    {
        super(128, 130);
        this.pricePerChunk = pricePerChunk;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        super.drawScreen(mouseX, mouseY, f);

        if(((GuiButton)this.buttonList.get(4)).mousePressed(mc, mouseX, mouseY))
        {
            this.drawHint(mouseX, mouseY, EnumChatFormatting.GOLD + "Current Rate: " + EnumChatFormatting.RED + "$" + this.pricePerChunk);
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        switch (btn.id)
        {
            case 4: {
                Minelife.NETWORK.sendToServer(new PacketBuyChunk());
                break;
            }
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();

        int y = this.yPosition + 5;

        int btnWidth = 75;

        this.buttonList.add(new GuiButton(0, this.calcX(btnWidth), y, btnWidth, 20, "Sell"));
        this.buttonList.add(new GuiButton(1, this.calcX(btnWidth), y = y + 25, btnWidth, 20, "Listings"));
        this.buttonList.add(new GuiButton(2, this.calcX(btnWidth), y = y + 25, btnWidth, 20, "Bills"));
        this.buttonList.add(new GuiButton(3, this.calcX(btnWidth), y = y + 25, btnWidth, 20, "Info"));
        this.buttonList.add(new GuiButton(4, this.calcX(btnWidth), y = y + 25, btnWidth, 20, "Buy Chunk"));
    }

}
