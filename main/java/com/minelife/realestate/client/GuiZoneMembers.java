package com.minelife.realestate.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.realestate.Member;
import com.minelife.realestate.Zone;
import com.minelife.realestate.ZonePermission;
import com.minelife.util.client.*;
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

import java.util.*;
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

        if (keyCode != Keyboard.KEY_SPACE)
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

    private class Content extends GuiScrollableContent {

        private Map<Member, List<GuiTickBox>> permissionsMap = Maps.newHashMap();
        private Map<Member, GuiRemoveBtn> removeMap = Maps.newHashMap();
        private Set<Member> members = new TreeSet<>();

        public Content(int xPosition, int yPosition, int width, int height)
        {
            super(xPosition, yPosition, width, height);


            members = zone.getOwner().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()) ? zone.getMembers() : zone.isForSale(Side.CLIENT) && !zone.getForSaleSign(Side.CLIENT).isOccupied() && zone.getForSaleSign(Side.CLIENT).getRenter().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()) ? zone.getForSaleSign(Side.CLIENT).getMembers() : zone.getMembers();

            for (Member member : members) {
                int y = 2;
                List<GuiTickBox> tickBoxes = Lists.newArrayList();
                for(ZonePermission permission : ZonePermission.values())
                    tickBoxes.add(new GuiTickBox(mc, 100, y+=20, member.canMember(permission), permission.name()));
                tickBoxes.forEach(tickBox -> tickBox.enabled = member.canMember(ZonePermission.MANAGER));
                permissionsMap.put(member, tickBoxes);
                removeMap.put(member, new GuiRemoveBtn(bgWidth - 24, 2));
            }
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 100;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            Member member = (Member) members.toArray()[index];

            mc.fontRenderer.drawString(member.getName(), 10, 6, 0xFFFFFF);

            int x = 20;
            int textYOffset = ((18 - fontRendererObj.FONT_HEIGHT) / 2);

            permissionsMap.get(member).forEach(guiTickBox -> mc.fontRenderer.drawString(guiTickBox.key, x, guiTickBox.yPosition + textYOffset, 0xFFFFFF));
            permissionsMap.get(member).forEach(GuiTickBox::drawTickBox);
            removeMap.get(member).drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return members.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            Member member = (Member) members.toArray()[index];
            if (removeMap.get(member).mousePressed(mc, mouseX, mouseY)) {
                Minelife.NETWORK.sendToServer(new PacketModifyMembers(member.getName(), false));
            } else {
                permissionsMap.get(member).forEach(guiTickBox -> guiTickBox.mouseClicked(mouseX, mouseY));
                permissionsMap.get(member).forEach(guiTickBox -> member.setPermission(ZonePermission.valueOf(guiTickBox.key), guiTickBox.isChecked()));
                Minelife.NETWORK.sendToServer(new PacketModifyMember(member));
            }
        }

        @Override
        public void drawBackground()
        {
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
            player = ByteBufUtils.readUTF8String(buf).trim();
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
                    else {
                        // a lot of if's here to prevent a manger from removing another manager
                        if(zone.getMember(playerUUID) != null) {
                            if(zone.getMember(player) != null) {
                                if(zone.getMember(player).canMember(ZonePermission.MANAGER)) {
                                    if(zone.getMember(playerUUID).canMember(ZonePermission.MANAGER)) {
                                        throw new CustomMessageException("Manager cannot remove a manager.");
                                    }
                                }
                            }
                        }
                        zone.getMembers().remove(zone.getMember(playerUUID));
                    }

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

                    if(zone.getMember(player) != null && zone.getMember(player).canMember(ZonePermission.MANAGER)) {
                        if(zone.getMember(message.member.getUniqueID()).canMember(ZonePermission.MANAGER))
                            throw new CustomMessageException("You cannot modify another managers permissions.");
                    }

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
