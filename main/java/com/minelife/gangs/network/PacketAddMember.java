package com.minelife.gangs.network;

import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.GangRole;
import com.minelife.util.client.PacketPopup;
import com.minelife.util.server.UUIDFetcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketAddMember implements IMessage {

    private UUID gangID;
    private String name;

    public PacketAddMember() {
    }

    public PacketAddMember(UUID gangID, String name) {
        this.gangID = gangID;
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gangID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gangID.toString());
        ByteBufUtils.writeUTF8String(buf, name);
    }

    public static class Handler implements IMessageHandler<PacketAddMember, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketAddMember message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang gang = Gang.getGang(message.gangID);

            if(gang == null) {
                PacketPopup.sendPopup("Gang not found", player);
                return null;
            }

            if(gang.hasPermission(player.getUniqueID(), GangPermission.INVITE)) {
                UUID playerID = UUIDFetcher.get(message.name);
                System.out.println(message.name);
                if(playerID == null) {
                    PacketPopup.sendPopup("Player not found", player);
                    return null;
                }

                if(Gang.getGangForPlayer(playerID) != null) {
                    PacketPopup.sendPopup("Player already belongs to a gang", player);
                    return null;
                }

                Map<UUID, GangRole> members = gang.getMembers();
                members.put(playerID, GangRole.TEENIE);
                gang.setMembers(members);
                gang.writeToDatabase();
                Minelife.getNetwork().sendTo(new PacketOpenGangGui(gang), player);
            } else {
                PacketPopup.sendPopup("You do not have permission to invite players.", player);
            }
            return null;
        }
    }

}
