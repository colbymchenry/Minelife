package com.minelife.realestate.client;

import com.google.common.collect.Maps;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.Member;
import com.minelife.realestate.Zone;
import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTextField;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class GuiZoneMembers extends AbstractZoneGui {

    private Content content;
    private Zone zone;
    private GuiTextField addField;

    public GuiZoneMembers(Zone zone)
    {
        super(200, 200);
        this.zone = zone;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f)
    {
        this.drawBackground();
        content.draw(mouseX, mouseY, Mouse.getDWheel());
        addField.drawTextBox();

        if (addField.isFocused()) {
            fontRendererObj.setUnicodeFlag(true);
            fontRendererObj.drawString("(Press Enter)",
                    addField.getBounds().getX() + addField.getBounds().getWidth() + 5, addField.getBounds().getY(), 0xFFFFFF);
            fontRendererObj.setUnicodeFlag(false);
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        content.keyTyped(c, keyCode);
        addField.textboxKeyTyped(c, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            Minelife.NETWORK.sendToServer(new PacketModifyMembers(addField.getText(), true));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        addField.mouseClicked(x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        content = new Content(this.xPosition, this.yPosition, this.bgWidth, this.bgHeight);
        addField = new GuiTextField(this.xPosition + 1, this.yPosition + this.bgWidth + 4, this.bgWidth - 55, 9);
    }

    @Override
    public void updateScreen()
    {
        addField.update();
    }

    private class Content extends GuiScrollList {

        private Map<Member, GuiTickBox> managerMap = Maps.newHashMap();
        private Map<Member, GuiTickBox> placeMap = Maps.newHashMap();
        private Map<Member, GuiTickBox> breakMap = Maps.newHashMap();
        private Map<Member, GuiTickBox> interactMap = Maps.newHashMap();
        private Map<Member, RemoveBtn> removeMap = Maps.newHashMap();

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);

            for (Member member : zone.getMembers()) {
                int y = 2;
                managerMap.put(member, new GuiTickBox(mc, 100, y += 20, member.isManager()));
                placeMap.put(member, new GuiTickBox(mc, 100, y += 20, member.isAllowPlacing()));
                breakMap.put(member, new GuiTickBox(mc, 100, y += 20, member.isAllowPlacing()));
                interactMap.put(member, new GuiTickBox(mc, 100, y += 20, member.isAllowPlacing()));
                removeMap.put(member, new RemoveBtn(bgWidth - 24, 2));
            }

            managerMap.forEach((member, guiTickBox) -> guiTickBox.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            placeMap.forEach((member, guiTickBox) -> guiTickBox.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            breakMap.forEach((member, guiTickBox) -> guiTickBox.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            interactMap.forEach((member, guiTickBox) -> guiTickBox.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
            removeMap.forEach((member, removeBtn) -> removeBtn.enabled = zone.hasManagerAuthority(Minecraft.getMinecraft().thePlayer));
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 100;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            Member member = (Member) zone.getMembers().toArray()[index];

            mc.fontRenderer.drawString(member.getName(), 10, 6, 0xFFFFFF);

            int x = 20;
            int textYOffset = ((18 - fontRendererObj.FONT_HEIGHT) / 2);

            mc.fontRenderer.drawString("Manager", x, managerMap.get(member).yPosition + textYOffset, 0xFFFFFF);
            mc.fontRenderer.drawString("Place", x, placeMap.get(member).yPosition + textYOffset, 0xFFFFFF);
            mc.fontRenderer.drawString("Break", x, breakMap.get(member).yPosition + textYOffset, 0xFFFFFF);
            mc.fontRenderer.drawString("Interact", x, interactMap.get(member).yPosition + textYOffset, 0xFFFFFF);
            managerMap.get(member).draw();
            placeMap.get(member).draw();
            breakMap.get(member).draw();
            interactMap.get(member).draw();
            removeMap.get(member).drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return zone.getMembers().size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            Member member = (Member) zone.getMembers().toArray()[index];
            if (removeMap.get(member).mousePressed(mc, mouseX, mouseY)) {
                Minelife.NETWORK.sendToServer(new PacketModifyMembers(member.getName(), false));
                return;
            }
            managerMap.get(member).mouseClicked(mouseX, mouseY);
            placeMap.get(member).mouseClicked(mouseX, mouseY);
            breakMap.get(member).mouseClicked(mouseX, mouseY);
            interactMap.get(member).mouseClicked(mouseX, mouseY);

            member.setManager(managerMap.get(member).getValue());
            member.setAllowPlacing(placeMap.get(member).getValue());
            member.setAllowBreaking(breakMap.get(member).getValue());
            member.setAllowInteracting(interactMap.get(member).getValue());

            Minelife.NETWORK.sendToServer(new PacketModifyMember(member));
        }

        @Override
        public void drawBackground()
        {
        }

        private class RemoveBtn extends GuiButton {

            private ResourceLocation texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x.png");

            public RemoveBtn(int x, int y)
            {
                super(0, x, y, 16, 16, "");
            }

            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY)
            {
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_BLEND);
                mc.getTextureManager().bindTexture(texture);
                float scale = 1f;

                if (mouseX >= xPosition && mouseX <= xPosition + 16 && mouseY >= yPosition && mouseY <= yPosition + 16) {
                    scale = 1.25f;
                }

                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(xPosition, yPosition, zLevel);
                    if (scale > 1f) {
                        GL11.glTranslatef(8, 8, 0);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glTranslatef(-8, -8, 0);
                    }
                    GuiUtil.drawImage(0, 0, 16, 16);
                }
                GL11.glPopMatrix();
            }
        }

    }

    public static class PacketModifyMembers implements IMessage {

        private String player;
        private boolean add;

        public PacketModifyMembers()
        {
        }

        public PacketModifyMembers(String player, boolean add)
        {
            this.player = player;
            this.add = add;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            player = ByteBufUtils.readUTF8String(buf);
            add = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, player);
            buf.writeBoolean(add);
        }

        public static class Handler implements IMessageHandler<PacketModifyMembers, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketModifyMembers message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Zone zone = Zone.getZone(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

                try {
                    if (zone == null) throw new CustomMessageException("There is no zone here.");

                    if (!zone.hasManagerAuthority(player))
                        throw new CustomMessageException("You do not have permission to modify members.");

                    UUID playerUUID = UUIDFetcher.get(message.player);

                    if (playerUUID == null) throw new CustomMessageException("Player not found.");

                    if (message.add)
                        zone.getMembers().add(new Member(zone, playerUUID));
                    else
                        zone.getMembers().remove(new Member(zone, playerUUID));

                    zone.save();

                    if (message.add)
                        player.addChatComponentMessage(new ChatComponentText("Player added as a new member!"));
                    else
                        player.addChatComponentMessage(new ChatComponentText("Member removed!"));

                    Minelife.NETWORK.sendTo(new PacketUpdateMembersList(zone), player);
                } catch (Exception e) {
                    if (e instanceof CustomMessageException)
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                    else {
                        e.printStackTrace();
                        Minelife.getLogger().log(Level.SEVERE, "", e);
                        player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                    }
                }

                return null;
            }
        }
    }

    public static class PacketModifyMember implements IMessage {

        private Member member;

        public PacketModifyMember()
        {
        }

        public PacketModifyMember(Member member)
        {
            this.member = member;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            member = Member.fromBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            member.toBytes(buf);
        }

        public static class Handler implements IMessageHandler<PacketModifyMember, IMessage> {

            @Override
            public IMessage onMessage(PacketModifyMember message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Zone zone = Zone.getZone(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

                try {
                    if (zone == null) throw new CustomMessageException("There is no zone here.");

                    if (!zone.hasManagerAuthority(player))
                        throw new CustomMessageException(EnumChatFormatting.RED + "You do not have permission to modify members.");

                    zone.getMembers().remove(message.member);
                    zone.getMembers().add(message.member);
                    zone.save();
                } catch (Exception e) {
                    if (e instanceof CustomMessageException)
                        player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                    else {
                        e.printStackTrace();
                        Minelife.getLogger().log(Level.SEVERE, "", e);
                        player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                    }
                }
                return null;
            }
        }
    }

    public static class PacketUpdateMembersList implements IMessage {

        private Zone zone;

        public PacketUpdateMembersList()
        {
        }

        public PacketUpdateMembersList(Zone zone)
        {
            this.zone = zone;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            zone = Zone.fromBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            zone.toBytes(buf);
        }

        public static class Handler implements IMessageHandler<PacketUpdateMembersList, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketUpdateMembersList message, MessageContext ctx)
            {
                if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiZoneMembers) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiZoneMembers(message.zone));
                }
                return null;
            }
        }
    }

}
