package com.minelife.realestate.client;

import com.google.common.collect.Maps;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.Member;
import com.minelife.realestate.Zone;
import com.minelife.util.client.GuiScrollList;
import com.minelife.util.client.GuiTextField;
import com.minelife.util.client.GuiTickBox;
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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class GuiZoneMembers extends AbstractZoneGui {

    private Content content;
    private Zone zone;
    private GuiTextField addField;
    private CustomZoneBtn addBtn;

    // TODO: Need to test
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
        addBtn.drawButton(mc, mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char c, int keyCode)
    {
        super.keyTyped(c, keyCode);
        content.keyTyped(c, keyCode);
        addField.textboxKeyTyped(c, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn)
    {
        addField.mouseClicked(x, y);
        if (addBtn.mousePressed(mc, x, y)) {
            Minelife.NETWORK.sendToServer(new PacketModifyMembers(addField.getText(), true));
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        content = new Content(this.xPosition, this.yPosition, this.bgWidth, this.bgHeight);
        addField = new GuiTextField(this.xPosition, this.yPosition + this.bgHeight, this.bgWidth - 55, 20);
        addBtn = new CustomZoneBtn(0, this.addField.getBounds().getX() + this.addField.getBounds().getWidth() + 5, this.addField.getBounds().getY(), 50, 20, "Add");
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
                int y = 14;
                managerMap.put(member, new GuiTickBox(mc, 100, y += 12, member.isManager()));
                placeMap.put(member, new GuiTickBox(mc, 100, y += 12, member.isAllowPlacing()));
                breakMap.put(member, new GuiTickBox(mc, 100, y += 12, member.isAllowPlacing()));
                interactMap.put(member, new GuiTickBox(mc, 100, y += 12, member.isAllowPlacing()));
                removeMap.put(member, new RemoveBtn(bgWidth - 65, 2));
            }
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 50;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            Member member = (Member) zone.getMembers().toArray()[index];

            mc.fontRenderer.drawString(member.getName(), 10, 2, 0xFFFFFF);
            removeMap.get(member).drawButton(mc, mouseX, mouseY);
            int x = 20;
            mc.fontRenderer.drawString("Manager", x, managerMap.get(member).yPosition, 0xFFFFFF);
            mc.fontRenderer.drawString("Place", x, placeMap.get(member).yPosition, 0xFFFFFF);
            mc.fontRenderer.drawString("Break", x, breakMap.get(member).yPosition, 0xFFFFFF);
            mc.fontRenderer.drawString("Interact", x, interactMap.get(member).yPosition, 0xFFFFFF);
            managerMap.get(member).draw();
            placeMap.get(member).draw();
            breakMap.get(member).draw();
            interactMap.get(member).draw();
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
                this.drawTexturedModalRect(xPosition, yPosition, 0, 0, 16, 16);
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

                    boolean isManager = false;

                    for (Member member : zone.getMembers())
                        if (member.getUniqueID().equals(player.getUniqueID()) && member.isManager())
                            isManager = true;

                    if (!zone.getOwner().equals(player.getUniqueID()) && !isManager)
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

                    boolean isManager = false;

                    for (Member member : zone.getMembers())
                        if (member.getUniqueID().equals(player.getUniqueID()) && member.isManager())
                            isManager = true;

                    if (!zone.getOwner().equals(player.getUniqueID()) && !isManager)
                        throw new CustomMessageException("You do not have permission to modify members.");

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

}
