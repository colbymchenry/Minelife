package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.realestate.Permission;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketSendMembers implements IMessage {

    private int estateID;
    private Map<UUID, List<Permission>> members;

    public PacketSendMembers() {
    }

    public PacketSendMembers(int estateID, Map<UUID, List<Permission>> members) {
        this.estateID = estateID;
        this.members = members;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateID = buf.readInt();
        int membersSize = buf.readInt();
        members = Maps.newHashMap();
        for (int i = 0; i < membersSize; i++) {
            UUID uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            int permsSize = buf.readInt();
            members.put(uuid, Lists.newArrayList());
            for (int i1 = 0; i1 < permsSize; i1++) {
//                members.put(uuid, members.get(uuid))
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
        buf.writeInt(members.size());
        members.forEach((uuid, list) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            buf.writeInt(list.size());
            list.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        });
    }

}
