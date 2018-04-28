package com.minelife.gangs.network;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.client.ClientProxy;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Set;
import java.util.UUID;

public class PacketSendGangMembers implements IMessage {

    private Set<UUID> members;

    public PacketSendGangMembers() {
    }

    public PacketSendGangMembers(Set<UUID> members) {
        this.members = members;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int membersSize = buf.readInt();
        members = Sets.newTreeSet();
        for (int i = 0; i < membersSize; i++) {
            members.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(members.size());
        members.forEach(member -> ByteBufUtils.writeUTF8String(buf, member.toString()));
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
            Minelife.getNetwork().sendTo(new PacketSendGangMembers(Sets.newTreeSet()), player);
            return;
        }

        Set<UUID> members = Sets.newTreeSet();
        members.add(gang.getOwner());
        members.addAll(gang.getMembers().keySet());
        gang.getMembers().keySet().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketSendGangMembers(members), PlayerHelper.getPlayer(playerID));
            }
        });

        Minelife.getNetwork().sendTo(new PacketSendGangMembers(members), player);
    }


}
