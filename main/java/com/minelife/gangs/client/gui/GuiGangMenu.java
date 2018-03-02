package com.minelife.gangs.client.gui;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.network.PacketInviteToGang;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Map;
import java.util.UUID;

public class GuiGangMenu extends GuiGang implements INameReceiver {

    public GuiMembersList GuiMembersList;
    public GuiTextField txtFieldAddMember;
    public GuiButton btnAddMember;
    public Map<UUID, String> members = Maps.newHashMap();
    public GuiMember selectedMember;

    public GuiGangMenu(Gang gang) {
        super(gang);
        // TODO: show officers in list
        gang.getMembers().forEach(memberUUID -> NameFetcher.asyncFetchClient(memberUUID, this));
        gang.getOfficers().forEach(memberUUID -> NameFetcher.asyncFetchClient(memberUUID, this));
    }

    // TODO: Implement GuiMember.

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        mc.fontRenderer.setUnicodeFlag(true);
        GuiMembersList.draw(mouse_x, mouse_y, Mouse.getDWheel());
        mc.fontRenderer.drawString(EnumChatFormatting.UNDERLINE + "Members",
                GuiMembersList.xPosition + (GuiMembersList.width - mc.fontRenderer.getStringWidth("Members")) / 2,
                GuiMembersList.yPosition - 13, 0xFFFFFF);

        mc.fontRenderer.drawString(EnumChatFormatting.GREEN + "Gang Balance: " + EnumChatFormatting.WHITE + "$" + NumberConversions.formatter.format(Gang.getBalanceClient()),
                this.XPosition + 8,
                this.YPosition + Height - 12, 0xFFFFFF);
        mc.fontRenderer.setUnicodeFlag(false);

        txtFieldAddMember.drawTextBox();
        btnAddMember.drawButton(mc, mouse_x, mouse_y);

        if(selectedMember != null) {
            drawDefaultBackground();
            selectedMember.draw(mouse_x, mouse_y);
        }
    }

    @Override
    protected void keyTyped(char Char, int Code) {
        if(selectedMember == null) {
            GuiMembersList.keyTyped(Char, Code);
            txtFieldAddMember.textboxKeyTyped(Char, Code);
        } else {
            if(Code == Keyboard.KEY_ESCAPE) {
                selectedMember = null;
                return;
            }
        }

        super.keyTyped(Char, Code);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        if(selectedMember == null) {
            txtFieldAddMember.mouseClicked(mouse_x, mouse_y, mouse_btn);
            if (btnAddMember.mousePressed(mc, mouse_x, mouse_y)) {
                Minelife.NETWORK.sendToServer(new PacketInviteToGang(txtFieldAddMember.getText()));
            }
        } else {
            selectedMember.onMouseClicked(mouse_x, mouse_y);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiMembersList = new GuiMembersList(mc, XPosition + (Width / 2) - 5, YPosition + 20, Width / 2, Height / 2, Gang, members, this);
        txtFieldAddMember = new GuiTextField(fontRendererObj, GuiMembersList.xPosition + 1, GuiMembersList.yPosition + GuiMembersList.height + 4, 94, 20);
        btnAddMember = new GuiButton(0, txtFieldAddMember.xPosition + txtFieldAddMember.width + 3, txtFieldAddMember.yPosition, 30, 20, "Add");
        btnAddMember.enabled = false;
    }

    @Override
    public void updateScreen() {
        btnAddMember.enabled = !txtFieldAddMember.getText().isEmpty();
    }

    @Override
    public void nameReceived(UUID uuid, String name) {
        members.put(uuid, name);
    }

    public void setGuiMember(String playerName, UUID playerUUID, Gang gang) {
        selectedMember = new GuiMember(XPosition + ((Width - GuiMember.width) / 2), YPosition + ((Height - GuiMember.height) / 2), playerName, playerUUID, gang);
    }
}
