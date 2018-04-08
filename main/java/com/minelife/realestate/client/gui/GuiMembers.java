package com.minelife.realestate.client.gui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.network.PacketAddMember;
import com.minelife.realestate.network.PacketModifyMember;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.server.NameFetcher;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GuiMembers extends GuiScreen {

    private Estate estate;
    private GuiTextField addMemberField;
    private MembersList membersList;
    private int guiLeft, guiTop, guiWidth = 150, guiHeight = 75;
    private Set<PlayerPermission> permissionsAllowedToChange;

    public GuiMembers(Estate estate, Set<PlayerPermission> permissionsAllowedToChange) {
        this.estate = estate;
        this.permissionsAllowedToChange = permissionsAllowedToChange;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        membersList.draw(mouseX, mouseY, Mouse.getDWheel());
        addMemberField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        addMemberField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        addMemberField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Minelife.getNetwork().sendToServer(new PacketAddMember(estate.getUniqueID(), addMemberField.getText()));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;
        membersList = new MembersList(mc, guiLeft + 4, guiTop + 4, guiWidth - 4, guiHeight - 30);
        addMemberField = new GuiTextField(0, fontRenderer, membersList.x + membersList.width - 80, membersList.y + 5, 40, 20);
        buttonList.add(new GuiButton(0, addMemberField.x + addMemberField.width + 2, addMemberField.y, 30, 20, "Add"));
        buttonList.get(0).enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        addMemberField.updateCursorCounter();
        buttonList.get(0).enabled = !addMemberField.getText().isEmpty();
    }

    public Estate getEstate() {
        return estate;
    }

    /**
     * GUI MEMBER ------------------------
     */
    class GuiMember extends GuiScreen {

        private UUID memberID;
        private Set<PlayerPermission> permissions;
        private Map<PlayerPermission, GuiTickBox> tickBoxMap;
        private int guiLeft, guiTop, guiWidth = 100, guiHeight;

        public GuiMember(UUID memberID, Set<PlayerPermission> permissions) {
            this.memberID = memberID;
            this.permissions = permissions;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            tickBoxMap.forEach(((playerPermission, guiTickBox) -> {
                guiTickBox.drawTickBox();
                fontRenderer.drawStringWithShadow(WordUtils.capitalizeFully(playerPermission.name().replace("_", " ")), guiTickBox.xPosition - 50, guiTickBox.yPosition, 0xFFFFFF);
            }));
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if(keyCode == Keyboard.KEY_ESCAPE) {
                Set<PlayerPermission> playerPermissions = Sets.newTreeSet();
                tickBoxMap.forEach((playerPermission, guiTickBox) -> {
                    if(guiTickBox.isChecked()) playerPermissions.add(playerPermission);
                });
                Minelife.getNetwork().sendToServer(new PacketModifyMember(memberID, estate.getUniqueID(), playerPermissions));
                mc.displayGuiScreen(new GuiMembers(estate, permissionsAllowedToChange));
            }
        }

        @Override
        public void initGui() {
            super.initGui();
            buttonList.clear();
            tickBoxMap = Maps.newHashMap();
            guiHeight = PlayerPermission.values().length * (fontRenderer.FONT_HEIGHT + 4) + 20;
            guiLeft = (this.width - this.guiWidth) / 2;
            guiTop = (this.height - this.guiHeight) / 2;

            int y = 20;
            for (PlayerPermission playerPermission : permissionsAllowedToChange) {
                tickBoxMap.put(playerPermission, new GuiTickBox(mc, guiLeft + guiWidth - 30, guiTop + y, permissions.contains(playerPermission)));
                y += fontRenderer.FONT_HEIGHT + 4;
            }
        }
    }

    /**
     * GUI MembersList ------------------------
     */
    class MembersList extends GuiScrollableContent {

        public MembersList(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 15;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRenderer.drawString(NameFetcher.asyncFetchClient((UUID) estate.getMemberIDs().toArray()[index]), 2, 2, 0xFFFFFF);
        }

        @Override
        public int getSize() {
            return estate.getMemberIDs().size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if(doubleClick) {
                UUID memberID = (UUID) estate.getMemberIDs().toArray()[index];
                Set<PlayerPermission> permissions = estate.getMemberPermissions(memberID);
                mc.displayGuiScreen(new GuiMember(memberID, permissions));
            }
        }
    }
}
