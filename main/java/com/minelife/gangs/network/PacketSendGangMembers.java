package com.minelife.gangs.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangRole;
import com.minelife.gangs.client.ClientProxy;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketSendGangMembers implements IMessage {

    private Map<UUID, GangRole> members;

    public PacketSendGangMembers() {
    }

    public PacketSendGangMembers(Map<UUID, GangRole> members) {
        this.members = members;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int membersSize = buf.readInt();
        members = Maps.newHashMap();
        for (int i = 0; i < membersSize; i++) {
            members.put(UUID.fromString(ByteBufUtils.readUTF8String(buf)), GangRole.values()[buf.readInt()]);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(members.size());
        members.forEach((playerID, role) -> {
            ByteBufUtils.writeUTF8String(buf, playerID.toString());
            buf.writeInt(role.ordinal());
        });
    }

    public static class Handler implements IMessageHandler<PacketSendGangMembers, IMessage> {

        @Override
        public IMessage onMessage(PacketSendGangMembers message, MessageContext ctx) {
            ClientProxy.gangMembers = message.members;
            return null;
        }
    }

    public static void sendMembers(Gang gang, EntityPlayerMP player) {
        if (gang == null) {
            Minelife.getNetwork().sendTo(new PacketSendGangMembers(Maps.newHashMap()), player);
            return;
        }

        Map<UUID, GangRole> members = gang.getMembers();
        members.put(gang.getOwner(), GangRole.OWNER);

        gang.getMembers().keySet().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketSendGangMembers(members), PlayerHelper.getPlayer(playerID));
            }
        });

        Minelife.getNetwork().sendTo(new PacketSendGangMembers(members), player);
    }


}
