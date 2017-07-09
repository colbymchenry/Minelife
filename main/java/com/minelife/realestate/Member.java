package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.util.ArrayUtil;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Member implements Comparable<Member> {

    private Zone zone;
    private UUID memberUniqueID;
    private String memberName;
    private boolean allowPlacing, allowBreaking, allowInteracting;

    public Member(Zone zone, UUID memberUniqueID) throws SQLException
    {
        this.zone = zone;
        this.memberUniqueID = memberUniqueID;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Zones WHERE region='" + zone.getRegion().getUniqueID().toString() + "'");

        String[] members = ArrayUtil.fromString(result.getString("members"));

        for (String member : members) {
            String[] data = member.split(",");
            if(data[0].equalsIgnoreCase(memberUniqueID.toString())) {
                this.allowPlacing = Boolean.parseBoolean(data[1]);
                this.allowBreaking = Boolean.parseBoolean(data[2]);
                this.allowInteracting = Boolean.parseBoolean(data[3]);
                break;
            }
        }

        this.memberName = NameFetcher.get(memberUniqueID);
    }

    private Member() {}

    @Override
    public String toString() {
        return memberUniqueID.toString() + "," + allowPlacing + "," + allowBreaking + "," + allowInteracting;
    }

    public Zone getZone()
    {
        return zone;
    }

    public boolean isAllowPlacing()
    {
        return allowPlacing;
    }

    public boolean isAllowBreaking()
    {
        return allowBreaking;
    }

    public boolean isAllowInteracting()
    {
        return allowInteracting;
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

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberUniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, memberName);
        buf.writeBoolean(isAllowPlacing());
        buf.writeBoolean(isAllowBreaking());
        buf.writeBoolean(isAllowInteracting());
    }

    public static Member fromBytes(ByteBuf buf) {
        Member member = new Member();
        member.memberUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        member.memberName = ByteBufUtils.readUTF8String(buf);
        member.allowPlacing = buf.readBoolean();
        member.allowBreaking = buf.readBoolean();
        member.allowInteracting = buf.readBoolean();
        return member;
    }
}
