package com.minelife.realestate.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.realestate.Permission;
import com.minelife.realestate.client.gui.GuiMembers;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketSendMembers implements IMessage {

    private Map<UUID, Set<Permission>> members;
    private Set<Permission> playerPermissions;
    private int estateID;

    public PacketSendMembers() {
    }

    public PacketSendMembers(Map<UUID, Set<Permission>> members, Set<Permission> playerPermissions, int estateID) {
        this.members = members;
        this.playerPermissions = playerPermissions;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerPermissions = Sets.newTreeSet();
        int playerPermsSize = buf.readInt();
        for (int i = 0; i < playerPermsSize; i++) playerPermissions.add(Permission.values()[buf.readInt()]);
        members = Maps.newHashMap();
        int membersSize = buf.readInt();
        for (int i = 0; i < membersSize; i++) {
            UUID uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            Set<Permission> perms = Sets.newTreeSet();
            int permsSize = buf.readInt();
            for (int i1 = 0; i1 < permsSize; i1++) perms.add(Permission.values()[buf.readInt()]);
            members.put(uuid, perms);
        }

        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerPermissions.size());
        playerPermissions.forEach(p -> buf.writeInt(p.ordinal()));
        buf.writeInt(members.size());
        members.forEach((uuid, perms) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            buf.writeInt(perms.size());
            perms.forEach(p -> buf.writeInt(p.ordinal()));
        });

        buf.writeInt(estateID);
    }

    public static class Handler implements IMessageHandler<PacketSendMembers, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendMembers message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMembers(message.members, message.playerPermissions, message.estateID));
            return null;
        }
    }
}
