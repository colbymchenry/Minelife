package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.region.server.Region;
import com.minelife.util.ArrayUtil;
import com.minelife.util.ListToString;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Zone implements Comparable<Zone> {

    private static final Set<Zone> ZONES = new TreeSet<>();

    private Region region;
    private UUID owner;
    private Set<Member> members = new TreeSet<>();

    private boolean publicBreaking, publicPlacing, publicInteracting;
    private String intro, outro;

    private Zone(Region region) throws SQLException
    {
        this.region = region;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Zones WHERE region='" + region.getUniqueID().toString() + "'");
        owner = UUID.fromString(result.getString("owner"));
        for (String member : ArrayUtil.fromString(result.getString("members"))) {
            if (!member.isEmpty())
                members.add(new Member(this, UUID.fromString(member.split(",")[0])));
        }
        publicBreaking = result.getBoolean("publicBreaking");
        publicPlacing = result.getBoolean("publicPlacing");
        publicInteracting = result.getBoolean("publicInteracting");
        intro = result.getString("intro");
        outro = result.getString("outro");
    }

    private Zone()
    {
    }

    public UUID getOwner()
    {
        return owner;
    }

    public Region getRegion()
    {
        return region;
    }

    public String getIntro()
    {
        return intro;
    }

    public String getOutro()
    {
        return outro;
    }

    public boolean isPublicBreaking()
    {
        return publicBreaking;
    }

    public boolean isPublicPlacing()
    {
        return publicPlacing;
    }

    public boolean isPublicInteracting()
    {
        return publicInteracting;
    }

    public Set<Member> getMembers()
    {
        return members;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public void setPublicBreaking(boolean publicBreaking)
    {
        this.publicBreaking = publicBreaking;
    }

    public void setPublicPlacing(boolean publicPlacing)
    {
        this.publicPlacing = publicPlacing;
    }

    public void setPublicInteracting(boolean publicInteracting)
    {
        this.publicInteracting = publicInteracting;
    }

    public boolean isSubRegion()
    {
        return getRegion().isSubRegion();
    }

    public void save() throws SQLException
    {
        List<Member> memberList = Lists.newArrayList();
        memberList.addAll(getMembers());
        ListToString<Member> memberListToString = new ListToString<Member>(memberList) {
            @Override
            public String toString(Member o)
            {
                return o.toString();
            }
        };

        Minelife.SQLITE.query("UPDATE RealEstate_Zones SET " +
                "owner='" + (getOwner() == null ? "NULL" : getOwner().toString()) + "' AND " +
                "members='" + memberListToString.getString() + "' AND " +
                "publicBreaking='" + (isPublicBreaking() ? 1 : 0) + "' AND " +
                "publicPlacing='" + (isPublicPlacing() ? 1 : 0) + "' AND " +
                "publicInteracting='" + (isPublicInteracting() ? 1 : 0) + "' AND " +
                "intro='" + getIntro() + "' AND " +
                "outro='" + getOutro() + "'");
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, getOwner().toString());
        ByteBufUtils.writeUTF8String(buf, getIntro());
        ByteBufUtils.writeUTF8String(buf, getOutro());
        buf.writeBoolean(isPublicBreaking());
        buf.writeBoolean(isPublicPlacing());
        buf.writeBoolean(isPublicInteracting());
        getRegion().toBytes(buf);
        buf.writeInt(members.size());
        members.forEach(member -> member.toBytes(buf));
    }

    public static Zone fromBytes(ByteBuf buf)
    {
        Zone zone = new Zone();
        zone.owner = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        zone.intro = ByteBufUtils.readUTF8String(buf);
        zone.outro = ByteBufUtils.readUTF8String(buf);
        zone.publicBreaking = buf.readBoolean();
        zone.publicPlacing = buf.readBoolean();
        zone.publicInteracting = buf.readBoolean();
        zone.region = Region.fromBytes(buf);
        int members = buf.readInt();
        for (int i = 0; i < members; i++) zone.members.add(Member.fromBytes(buf));
        return zone;
    }

    public static Zone createZone(World world, Vec3 pos1, Vec3 pos2, UUID owner) throws Exception
    {
        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));

        Region region = Region.create(world.getWorldInfo().getWorldName(),
                AxisAlignedBB.getBoundingBox(cuboidRegion.getMinimumPoint().getBlockX(),cuboidRegion.getMinimumPoint().getBlockY(),cuboidRegion.getMinimumPoint().getBlockZ(),
                        cuboidRegion.getMaximumPoint().getBlockX(),cuboidRegion.getMaximumPoint().getBlockY(),cuboidRegion.getMaximumPoint().getBlockZ()));

        Minelife.SQLITE.query("INSERT INTO RealEstate_Zones (region, owner) VALUES ('" + region.getUniqueID().toString() + "', '" + owner.toString() + "')");

        Zone zone = new Zone(region);
        ZONES.add(zone);
        return zone;
    }

    public static Zone getZone(World world, Vec3 pos)
    {
        Region region = Region.getRegionAt(world.getWorldInfo().getWorldName(), pos);

        if(region == null) return null;

        return ZONES.stream().filter(zone -> zone.getRegion().equals(region)).findFirst().orElse(null);
    }

    public static Zone getZone(Region region)
    {
        return ZONES.stream().filter(zone -> zone.getRegion().equals(region)).findFirst().orElse(null);
    }

    public static void deleteZone(UUID regionUniqueID) throws SQLException
    {
        Minelife.SQLITE.query("DELETE FROM RealEstate_Zones WHERE region='" + regionUniqueID.toString() + "'");
        Zone toRemove = ZONES.stream().filter(zone -> zone.getRegion().getUniqueID().equals(regionUniqueID)).findFirst().orElse(null);
        if (toRemove != null)
            ZONES.remove(toRemove);
        Region.delete(regionUniqueID);
    }

    public static void initZones() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Zones");

        while (result.next()) {
            UUID uniqueID = UUID.fromString(result.getString("region"));

            if (Region.getRegionFromUUID(uniqueID) != null) {
                ZONES.add(new Zone(Region.getRegionFromUUID(uniqueID)));
            } else {
                deleteZone(uniqueID);
            }
        }
    }

    @Override
    public int compareTo(Zone o)
    {
        return o.getRegion().getUniqueID().equals(getRegion().getUniqueID()) ? 0 : 1;
    }
}
