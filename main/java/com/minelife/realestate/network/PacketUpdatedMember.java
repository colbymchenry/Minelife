package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.client.gui.GuiMembers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketUpdatedMember implements IMessage {

    private UUID playerID;
    private Set<PlayerPermission> permissions;
    private boolean add;

    public PacketUpdatedMember() {
    }

    public PacketUpdatedMember(UUID playerID, boolean add, Set<PlayerPermission> permissions) {
        this.playerID = playerID;
        this.add = add;
        this.permissions = permissions;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.add = buf.readBoolean();
        permissions = Sets.newTreeSet();
        int permsSize = buf.readInt();
        for (int i = 0; i < permsSize; i++) permissions.add(PlayerPermission.values()[buf.readInt()]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
        buf.writeBoolean(this.add);
        buf.writeInt(permissions.size());
        permissions.forEach(playerPermission -> buf.writeInt(playerPermission.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketUpdatedMember, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdatedMember message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiMembers) {
                    GuiMembers guiMembers = (GuiMembers) Minecraft.getMinecraft().currentScreen;
                    Set<UUID> memberIDs = guiMembers.getEstate().getMemberIDs();
                    if (message.add) {
                        memberIDs.add(message.playerID);
                        guiMembers.getEstate().setMemberPermissions(message.playerID, message.permissions);
                    } else
                        memberIDs.remove(message.playerID);
                    guiMembers.getEstate().setMemberIDs(memberIDs);
                }
            });

            return null;
        }
    }

}
