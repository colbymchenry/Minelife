package com.minelife.realestate;

import com.minelife.util.server.Callback;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Member implements Comparable<Member>, Callback {

    public Estate estate;
    public Set<EnumPermission> permissions;
    public UUID playerUUID;
    public String playerName;

    public Member(UUID playerUUID, String playerName, Estate estate) {
        this.estate = estate;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.permissions = new TreeSet<>();
    }

    private Member(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playerName = NameFetcher.asyncFetchServer(playerUUID, this);
    }

    public void toBytes(ByteBuf buf) {
        estate.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, playerName);
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        buf.writeInt(permissions.size());
        permissions.forEach(p -> buf.writeInt(p.ordinal()));
    }

    public static Member fromBytes(ByteBuf buf) {
        Estate estate = Estate.fromBytes(buf);
        String playerName = ByteBufUtils.readUTF8String(buf);
        UUID playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int permSize = buf.readInt();
        Set<EnumPermission> permissions = new TreeSet<>();
        if(permSize > 0)
            for(int i = 0; i< permSize; i++) permissions.add(EnumPermission.values()[buf.readInt()]);
        Member member = new Member(playerUUID, playerName, estate);
        member.permissions = permissions;
        return member;
    }

    public static Member fromString(String str) {
        Set<EnumPermission> permissions = new TreeSet<>();
        UUID playerUUID = UUID.fromString(str.split(";")[0]);
        String[] permsArray = str.split(";")[1].split(",");
        for (String s : permsArray)
            if(!s.isEmpty()) permissions.add(EnumPermission.values()[Integer.parseInt(s)]);
        Member member = new Member(playerUUID);
        member.permissions = permissions;
        return member;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        permissions.forEach(p -> builder.append(p.ordinal()).append(","));
        return playerUUID.toString() + ";" + permissions.toString();
    }

    @Override
    public int compareTo(Member o)
    {
        return o.playerUUID.equals(playerUUID) && o.estate.getRegion().getUniqueID().equals(estate.getRegion().getUniqueID()) ? 0 : -1;
    }

    @Override
    public void callback(Object... objects)
    {
        playerName = (String) objects[1];
    }
}
