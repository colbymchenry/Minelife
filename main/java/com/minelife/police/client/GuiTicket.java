package com.minelife.police.client;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.police.ModPolice;
import com.minelife.police.network.PacketCreateTicket;
import com.minelife.police.network.PacketOpenTicketInventory;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.client.PacketRequestName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import scala.actors.threadpool.Arrays;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiTicket extends GuiScreen implements INameReceiver {

    private ItemStack ticketStack;
    private int ticketSlot;
    private Color bgColor = new Color(0, 63, 126, 255);
    private GuiChargeList guiChargeList;
    private List<Charge> chargeList;
    private int xPosition, yPosition;
    private int width = 200, height = 235;
    private double totalBail, totalJailTime;
    private String player, officer;

    public GuiTicket(int ticketSlot) {
        this.ticketSlot = ticketSlot;
        this.ticketStack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(ticketSlot);
        this.chargeList = Lists.newArrayList();
        this.player = PacketRequestName.requestName(ItemTicket.getPlayerForTicket(ticketStack), this);
        this.officer = PacketRequestName.requestName(ItemTicket.getOfficerForTicket(ticketStack), this);
        chargeList.addAll(ItemTicket.getChargesForTicket(ticketStack));
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, width, height, bgColor);
        guiChargeList.draw(x, y, Mouse.getDWheel());

        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.drawString("Ticket #: " + ItemTicket.getTicketID(ticketStack), xPosition + 5, yPosition + 5, 0xFFFFFF);
        fontRendererObj.drawString("Player: " + player, xPosition + 5, yPosition + 15, 0xFFFFFF);
        fontRendererObj.drawString("Officer: " + officer, xPosition + 5, yPosition + 25, 0xFFFFFF);
        fontRendererObj.drawString("Total Bail: $" + NumberConversions.formatter.format(totalBail), xPosition + 5, guiChargeList.yPosition + guiChargeList.height + 6, 0xFFFFFF);
        fontRendererObj.drawString("Total Jail Time: " + (totalJailTime / 20.0D) + " mc days.", xPosition + 5, guiChargeList.yPosition + guiChargeList.height + 16, 0xFFFFFF);
        fontRendererObj.setUnicodeFlag(false);

        super.drawScreen(x, y, f);
    }

    // TODO: Figure out why GUI closes when clicking chest or add charge
    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if(button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAddCharge2(this));
        } else if (button.id == 1) {
            if(chargeList.get(guiChargeList.getSelected()).counts == 1) {
                chargeList.remove(guiChargeList.getSelected());
            } else {
                chargeList.get(guiChargeList.getSelected()).counts -= 1;
            }
        } else if (button.id == 2) {
            Minelife.NETWORK.sendToServer(new PacketOpenTicketInventory(ticketSlot));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Minelife.NETWORK.sendToServer(new PacketCreateTicket(ticketSlot, chargeList, player));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiButton) buttonList.get(1)).enabled = guiChargeList.getSelected() != -1;
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (super.width - width) / 2;
        yPosition = (super.height - height) / 2;
        buttonList.clear();
        buttonList.add(getCustomButton(0, xPosition + width + 2, yPosition + 5, 16, 16, "+", true));
        buttonList.add(getCustomButton(1, xPosition + width + 2, yPosition + 25, 16, 16, "-", true));
        buttonList.add(getChestButton(2, xPosition + width + 2, yPosition + 45, 16, 16));
        ((GuiButton) buttonList.get(1)).enabled = false;
        guiChargeList = new GuiChargeList(xPosition + 5, yPosition + 40, width - 10, height - 80, chargeList);

        chargeList.forEach(charge -> totalBail += charge.bail);
        chargeList.forEach(charge -> totalJailTime += charge.jailTime);
    }

    public GuiButton getCustomButton(int id, int x, int y, int width, int height, String text, boolean unicode) {
        return new GuiButton(id, x, y, width, height, text) {
            Color c1 = new Color(0, 127, 220, 128);
            Color c2 = new Color(0, 40, 81, 184);

            @Override
            public void drawButton(Minecraft mc, int x, int y) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, c1.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition, this.yPosition, this.width, this.height);
                GL11.glColor4f(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, c2.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition + 2, this.yPosition + 2, this.width - 4, this.height - 4);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(1, 1, 1, 1);
                boolean hovered = this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                int l = !this.enabled ? 10526880 : hovered ? 16777120 : 14737632;
                if (unicode) fontRendererObj.setUnicodeFlag(true);
                GL11.glPushMatrix();
                GL11.glTranslatef(this.xPosition + 0.5F + this.width / 2, this.yPosition + (this.height - 8) / 2, zLevel);
                this.drawCenteredString(fontRendererObj, this.displayString, 0, 0, l);
                GL11.glPopMatrix();
                if (unicode) fontRendererObj.setUnicodeFlag(false);
            }
        };
    }

    public GuiButton getChestButton(int id, int x, int y, int width, int height) {
        return new GuiButton(id, x, y, width, height, "") {
            Color c1 = new Color(0, 127, 220, 128);
            Color c2 = new Color(0, 40, 81, 184);
            ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/chest.png");
            List<String> displayText = new ArrayList<>(Arrays.asList(new Object[]{"Inventory"}));

            @Override
            public void drawButton(Minecraft mc, int x, int y) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, c1.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition, this.yPosition, this.width, this.height);
                GL11.glColor4f(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, c2.getAlpha() / 255f);
                GuiUtil.drawImage(this.xPosition + 2, this.yPosition + 2, this.width - 4, this.height - 4);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(1, 1, 1, 1);
                boolean hovered = this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
                int l = !this.enabled ? 10526880 : hovered ? 16777120 : 14737632;
                Color color = new Color(l);
                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
                mc.getTextureManager().bindTexture(icon);
                GuiUtil.drawImage(xPosition + ((width - (width - 8)) / 2), yPosition + ((height - (height - 8)) / 2), width - 8, height - 8);
                if (hovered) drawHoveringText(displayText, x, y, fontRendererObj);
            }
        };
    }

    class GuiAddCharge2 extends GuiAddCharge {

        private GuiTicket guiTicket;

        public GuiAddCharge2(GuiTicket guiTicket) {
            super(null);
            this.guiTicket = guiTicket;
        }

        @Override
        protected void keyTyped(char keyChar, int keyCode) {
            if(keyCode == Keyboard.KEY_ESCAPE) {
                Minecraft.getMinecraft().displayGuiScreen(guiTicket);
            } else {
                super.keyTyped(keyChar, keyCode);
            }
        }

        @Override
        protected void actionPerformed(GuiButton guiButton) {
            if (guiButton.id == 1) {
                guiTicket.chargeList.add(new Charge(Integer.parseInt(countsField.getText()),
                        Integer.parseInt(bailField.getText()), Integer.parseInt(jailTime.getText()), descriptionField.getText()));
            }

            Minecraft.getMinecraft().displayGuiScreen(guiTicket);
        }
    }

    @Override
    public void nameReceived(UUID uuid, String name) {
        if(uuid.equals(ItemTicket.getPlayerForTicket(ticketStack))) {
            player = name;
        } else if (uuid.equals(ItemTicket.getOfficerForTicket(ticketStack))) {
            officer = name;
        }
    }
}
