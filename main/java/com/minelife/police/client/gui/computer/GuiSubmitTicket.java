package com.minelife.police.client.gui.computer;

import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.police.client.gui.ticket.GuiChargeList;
import com.minelife.police.network.PacketWriteTicketToDB;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

public class GuiSubmitTicket extends GuiComputer implements INameReceiver {

    private int slot;
    private ItemStack ticketStack;
    private GuiFakeInventory fakeInventory;
    private List<Charge> chargeList;
    private String player, officer;
    private double timeToPay, totalJailTime, totalBail;
    public GuiChargeList guiChargeList;
    private int xPosition,yPosition, chargeListW, chargeListH, chargeListX, chargeListY;
    private static Color ticketBGColor = new Color(0, 63, 126, 255);

    @Override
    public void initGui()
    {
        super.initGui();


        this.xPosition = (this.width / 2) - 30;
        this.yPosition = (this.height / 2) - 100;
        this.chargeListX = xPosition + 43;
        this.chargeListY = yPosition + 40;
        this.chargeListW = 200 - 60;
        this.chargeListH = 235 - 100;

        int xOffset = 5;
        int yOffset = this.height - (16 * 5);

        fakeInventory = new GuiFakeInventory(mc) {
            @Override
            public void setupSlots()
            {
                // players hotbar
                for (int x = 0; x < 9; ++x) {
                    this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x], new Rectangle(xOffset + x * 18, yOffset + 58, 16, 16), x));
                }

                // players inventory
                for (int y = 0; y < 3; ++y) {
                    for (int x = 0; x < 9; ++x) {
                        this.slots.add(new ItemSlot(mc.thePlayer.inventory.mainInventory[x + y * 9 + 9], new Rectangle(xOffset + x * 18, yOffset + y * 18, 16, 16), x + y * 9 + 9));
                    }
                }
            }
        };

        fakeInventory.slotColor = new Color(50, 50, 200, 190);

        if(this.ticketStack != null && this.ticketStack.getItem() == MLItems.ticket) {
            guiChargeList = new GuiChargeList(mc,chargeListX, chargeListY, chargeListW, chargeListH, chargeList);
            guiChargeList.unicodeFlag = true;
        }

        this.buttonList.clear();
        this.buttonList.add(new ComputerButton(0, 5, height - 120, 80, 30, "Submit", 2));
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);

        int clickedSlot = fakeInventory.getClickedSlot(x, y);
        int oldTicketID = -1;

        if(this.ticketStack != null) oldTicketID = ItemTicket.getTicketID(this.ticketStack);

        if(clickedSlot != -1 && fakeInventory.slots.get(clickedSlot).stack != null && fakeInventory.slots.get(clickedSlot).stack.getItem() == MLItems.ticket) {
            this.slot = clickedSlot;
            this.ticketStack = fakeInventory.slots.get(clickedSlot).stack;
        }

        if(this.ticketStack != null) if(oldTicketID == ItemTicket.getTicketID(this.ticketStack)) return;

        if(this.ticketStack != null && this.ticketStack.getItem() == MLItems.ticket) {
            this.chargeList = Lists.newArrayList();
            this.player = NameFetcher.asyncFetchClient(ItemTicket.getPlayerForTicket(ticketStack), this);
            this.officer = NameFetcher.asyncFetchClient(ItemTicket.getOfficerForTicket(ticketStack), this);
            this.timeToPay = ItemTicket.getTimeToPay(ticketStack);
            this.chargeList.addAll(ItemTicket.getChargesForTicket(ticketStack));
            chargeList.forEach(charge -> totalBail += charge.bail);
            chargeList.forEach(charge -> totalJailTime += charge.jailTime);
            guiChargeList = new GuiChargeList(mc,chargeListX, chargeListY, chargeListW, chargeListH, chargeList);
            guiChargeList.unicodeFlag = true;
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        if(slot != -1 && ticketStack != null) {
            Minelife.NETWORK.sendToServer(new PacketWriteTicketToDB(slot));
            mc.displayGuiScreen(new GuiTicketSearch());
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiTicketSearch());
            return;
        }
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        drawBackground();
        super.drawScreen(x, y, f);
        fakeInventory.draw(x, y);

        if(this.ticketStack != null) {
            GuiUtil.drawDefaultBackground(chargeListX - 5, chargeListY - 40, chargeListW + 10, chargeListH + 80,  ticketBGColor);
            guiChargeList.draw(x, y, Mouse.getDWheel());
            fontRendererObj.setUnicodeFlag(true);
            fontRendererObj.drawString("Ticket #: " + ItemTicket.getTicketID(ticketStack), chargeListX, yPosition + 5, 0xFFFFFF);
            fontRendererObj.drawString("Player: " + player, chargeListX, yPosition + 15, 0xFFFFFF);
            fontRendererObj.drawString("Officer: " + officer, chargeListX, yPosition + 25, 0xFFFFFF);
            fontRendererObj.drawString("Total Bail: $" + NumberConversions.formatter.format(totalBail), chargeListX, guiChargeList.yPosition + guiChargeList.height + 6, 0xFFFFFF);
            fontRendererObj.drawString("Total Jail Time: " + (totalJailTime / 20.0D) + " mc days.", chargeListX, guiChargeList.yPosition + guiChargeList.height + 16, 0xFFFFFF);
            fontRendererObj.drawString("Time Left To Pay: " + (timeToPay / 20.0D) + " mc days.", chargeListX, guiChargeList.yPosition + guiChargeList.height + 26, 0xFFFFFF);
            fontRendererObj.setUnicodeFlag(false);
        }
    }

    @Override
    public void nameReceived(UUID uuid, String name)
    {
        if(uuid.equals(ItemTicket.getPlayerForTicket(ticketStack))) {
            player = name;
        } else if (uuid.equals(ItemTicket.getOfficerForTicket(ticketStack))) {
            officer = name;
        }
    }
}
