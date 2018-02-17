package com.minelife.gangs.client.gui;

import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.network.PacketModifyPlayer;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import java.util.UUID;

public class GuiMember extends Gui {

    private static final int width = 100, height = 50;

    private Minecraft mc;
    private int xPosition, yPosition;
    private String playerName;
    private UUID playerUUID;
    private Gang gang;
    private GuiButton btnOfficer, btnLeader, btnMember, btnKick;

    public GuiMember(int xPosition, int yPosition, String playerName, UUID playerUUID, Gang gang) {
        this.mc = Minecraft.getMinecraft();
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.gang = gang;
        this.btnOfficer = new GuiButton(0, xPosition, yPosition, 50, 20, "Officer");
        this.btnMember = new GuiButton(0, btnOfficer.xPosition, btnOfficer.yPosition + 22, 50, 20, "Member");
        this.btnKick = new GuiButton(0, btnOfficer.xPosition, btnOfficer.yPosition + 44, 50, 20, "Kick");
        this.btnLeader = new GuiButton(0, btnOfficer.xPosition, btnOfficer.yPosition + 66, 50, 20, "Leader");

        this.btnOfficer.enabled = !gang.getOfficers().contains(playerUUID);
    }

    public void draw(int mouseX, int mouseY) {
        GuiUtil.drawDefaultBackground(xPosition, yPosition, width, height, GuiGang.BackgroundColor);
        btnOfficer.drawButton(mc, mouseX, mouseY);
        btnLeader.drawButton(mc, mouseX, mouseY);
        btnMember.drawButton(mc, mouseX, mouseY);
        btnKick.drawButton(mc, mouseX, mouseY);
    }

    public void onMouseClicked(int mouseX, int mouseY) {
            Minelife.NETWORK.sendToServer(new PacketModifyPlayer(gang, playerUUID, btnKick.mousePressed(mc, mouseX, mouseY),
                    btnMember.mousePressed(mc, mouseX, mouseY), btnOfficer.mousePressed(mc, mouseX, mouseY),
                    btnLeader.mousePressed(mc, mouseX, mouseY)));
    }

}
