package com.minelife.realestate.client;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.Zone;
import com.minelife.realestate.ZonePermission;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

import java.util.logging.Level;

public class GuiZoneInfo extends AbstractZoneGui {

    private Content content;
    private Zone zone;

    public GuiZoneInfo(Zone zone)
    {
        super(200, 200);
        this.zone = zone;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        content.draw(mouseX, mouseY, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        content.keyTyped(c, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        content = new Content(this.xPosition, this.yPosition, this.bgWidth, this.bgHeight);
    }

    @Override
    public void updateScreen()
    {
    }

    private class Content extends GuiScrollableContent {

        private GuiTextField introField, outroField;
        private GuiTickBox allowPlacement, allowBreaking, allowInteracting;
        private CustomZoneBtn saveBtn, membersBtn, boundsBtn;

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            this.introField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, 30, 175, 20);
            this.introField.setMaxStringLength(this.introField.getMaxStringLength() + 2);
            this.outroField = new GuiTextField(mc.fontRenderer, calcX(175) - this.xPosition, this.introField.yPosition + 60, 175, 20);
            this.outroField.setMaxStringLength(this.outroField.getMaxStringLength() + 2);

            this.introField.setText(zone.getIntro());
            this.outroField.setText(zone.getOutro());

            int tickboxPosX = width - 50;
            this.allowPlacement = new GuiTickBox(mc, tickboxPosX, this.outroField.yPosition + 70, zone.canPublic(ZonePermission.PLACE));
            this.allowBreaking = new GuiTickBox(mc, tickboxPosX, this.allowPlacement.yPosition + 30, zone.canPublic(ZonePermission.BREAK));
            this.allowInteracting = new GuiTickBox(mc, tickboxPosX, this.allowBreaking.yPosition + 30, zone.canPublic(ZonePermission.INTERACT));

            this.saveBtn = new CustomZoneBtn(0, calcX(mc.fontRenderer.getStringWidth("Save") + 15) - this.xPosition, getObjectHeight(0) - 30, mc.fontRenderer.getStringWidth("Save") + 20, 20, "Save");
            this.membersBtn = new CustomZoneBtn(0, this.bounds.getWidth() - 75, this.allowInteracting.yPosition + 50, mc.fontRenderer.getStringWidth("Members") + 20, 20, "Members");
            this.boundsBtn = new CustomZoneBtn(0, 10, this.membersBtn.yPosition, mc.fontRenderer.getStringWidth("View Bounds") + 20, 20, "View Bounds");

            this.introField.setEnabled(zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            this.outroField.setEnabled(zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            this.allowPlacement.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer);
            this.allowBreaking.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer);
            this.allowInteracting.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer);
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 350;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            mc.fontRenderer.drawString("Intro Message", calcX(mc.fontRenderer.getStringWidth("Intro Message")) - this.xPosition, this.introField.yPosition - 15, 0xFFFFFF);
            this.introField.drawTextBox();
            mc.fontRenderer.drawString("Outro Message", calcX(mc.fontRenderer.getStringWidth("Outro Message")) - this.xPosition, this.outroField.yPosition - 15, 0xFFFFFF);
            this.outroField.drawTextBox();

            mc.fontRenderer.drawString(EnumChatFormatting.UNDERLINE + "Public Permissions", calcX(mc.fontRenderer.getStringWidth("Public Permissions")) - this.xPosition, this.allowPlacement.yPosition - 20, 0xFFFFFF);
            mc.fontRenderer.drawString("Allow Placement", 10, this.allowPlacement.yPosition + 5, 0xFFFFFF);
            this.allowPlacement.drawTickBox();
            mc.fontRenderer.drawString("Allow Breaking", 10, this.allowBreaking.yPosition + 5, 0xFFFFFF);
            this.allowBreaking.drawTickBox();
            mc.fontRenderer.drawString("Allow Interacting", 10, this.allowInteracting.yPosition + 5, 0xFFFFFF);
            this.allowInteracting.drawTickBox();

            this.saveBtn.drawButton(mc, mouseX, mouseY);
            this.membersBtn.drawButton(mc, mouseX, mouseY);
            this.boundsBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            this.introField.mouseClicked(mouseX, mouseY, 0);
            this.outroField.mouseClicked(mouseX, mouseY, 0);

            this.allowPlacement.mouseClicked(mouseX, mouseY);
            this.allowBreaking.mouseClicked(mouseX, mouseY);
            this.allowInteracting.mouseClicked(mouseX, mouseY);

            if (this.membersBtn.mousePressed(mc, mouseX, mouseY)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiZoneMembers(zone));
                return;
            }

            if (this.saveBtn.mousePressed(mc, mouseX, mouseY)) {
                Minelife.NETWORK.sendToServer(new PacketModifyZone(this.introField.getText(), this.outroField.getText(),
                        this.allowPlacement.isChecked(), this.allowBreaking.isChecked(), this.allowInteracting.isChecked()));
            }
        }

        @Override
        public void keyTyped(char keycode, int keynum)
        {
            super.keyTyped(keycode, keynum);
            this.introField.textboxKeyTyped(keycode, keynum);
            this.outroField.textboxKeyTyped(keycode, keynum);
        }

        @Override
        public void drawBackground()
        {
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {
        }

    }

    public static class PacketModifyZone implements IMessage {

        private String intro, outro;
        private boolean allowPlacement, allowBreaking, allowInteracting;

        public PacketModifyZone()
        {
        }

        public PacketModifyZone(String intro, String outro, boolean allowPlacement, boolean allowBreaking, boolean allowInteracting)
        {
            this.intro = intro;
            this.outro = outro;
            this.allowPlacement = allowPlacement;
            this.allowBreaking = allowBreaking;
            this.allowInteracting = allowInteracting;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            allowPlacement = buf.readBoolean();
            allowBreaking = buf.readBoolean();
            allowInteracting = buf.readBoolean();
            intro = ByteBufUtils.readUTF8String(buf);
            outro = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(allowPlacement);
            buf.writeBoolean(allowBreaking);
            buf.writeBoolean(allowInteracting);
            ByteBufUtils.writeUTF8String(buf, intro);
            ByteBufUtils.writeUTF8String(buf, outro);
        }

        public static class Handler implements IMessageHandler<PacketModifyZone, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketModifyZone message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Zone zone = Zone.getZone(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

                try {
                    if (zone == null) throw new CustomMessageException("Zone not found at your location.");

                    if (!zone.hasManagerAuthority(player))
                        throw new CustomMessageException(EnumChatFormatting.RED + "You do not have permission to modify this zone.");

                    zone.setPublicPermission(ZonePermission.BREAK, message.allowBreaking);
                    zone.setPublicPermission(ZonePermission.INTERACT, message.allowInteracting);
                    zone.setPublicPermission(ZonePermission.PLACE, message.allowPlacement);
                    zone.setIntro(message.intro);
                    zone.setOutro(message.outro);
                    zone.save();

                    player.addChatComponentMessage(new ChatComponentText("Zone updated!"));
                } catch (Exception e) {
                    if (e instanceof CustomMessageException) {
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                    } else {
                        e.printStackTrace();
                        Minelife.getLogger().log(Level.SEVERE, "", e);
                        player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                    }
                }
                return null;
            }

        }
    }

}
