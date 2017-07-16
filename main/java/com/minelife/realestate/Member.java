package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.util.ArrayUtil;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Member implements Comparable<Member> {

    private Zone zone;
    private UUID memberUniqueID;
    private String memberName;
    private Set<ZonePermission> permissions = new TreeSet<>();

    public Member(Zone zone, UUID memberUniqueID) throws SQLException
    {
        this.zone = zone;
        this.memberUniqueID = memberUniqueID;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Zones WHERE region='" + zone.getRegion().getUniqueID().toString() + "'");

        String[] members = ArrayUtil.fromString(result.getString("members"));

        for (String member : members) {
            String[] data = member.split(";");
            if (data[0].equalsIgnoreCase(memberUniqueID.toString())) {
                if (data[1].contains(",")) {
                    for (String s : data[1].split(",")) {
                        if (!s.isEmpty()) permissions.add(ZonePermission.valueOf(s));
                    }
                }
                break;
            }
        }

        this.memberName = NameFetcher.get(memberUniqueID);
    }

    private Member()
    {
    }

    @Override
    public String toString()
    {
        StringBuilder permissions = new StringBuilder();
        this.permissions.forEach(permission -> permissions.append(permission.name()).append(","));
        if (permissions.toString().contains(","))
            permissions.deleteCharAt(permissions.length() - 1);
        return memberUniqueID.toString() + ";" + permissions.toString();
    }

    public static Member fromString(String s) {
        Member member = new Member();
        String[] data = s.split(";");
        member.memberUniqueID = UUID.fromString(data[0]);
        String[] perms = data[1].split(",");
        for (String perm : perms) {
            member.permissions.add(ZonePermission.valueOf(perm));
        }
        return member;
    }

    public Zone getZone()
    {
        return zone;
    }

    public boolean canMember(ZonePermission permission)
    {
        return permissions.contains(permission);
    }

    public void setPermission(ZonePermission permission, boolean value)
    {
        if (value)
            permissions.add(permission);
        else
            permissions.remove(permission);
    }

    public String getName()
    {
        return memberName;
    }

    public UUID getUniqueID()
    {
        return memberUniqueID;
    }

    @Override
    public int compareTo(Member o)
    {
        return o.memberUniqueID.equals(memberUniqueID) ? 0 : 1;
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, memberUniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, memberName);
        buf.writeInt(permissions.size());
        permissions.forEach(permission -> ByteBufUtils.writeUTF8String(buf, permission.name()));
    }

    public static Member fromBytes(ByteBuf buf)
    {
        Member member = new Member();
        member.memberUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        member.memberName = ByteBufUtils.readUTF8String(buf);
        int permCount = buf.readInt();
        for (int i = 0; i < permCount; i++) {
            member.permissions.add(ZonePermission.valueOf(ByteBufUtils.readUTF8String(buf)));
        }
        return member;
    }
}
