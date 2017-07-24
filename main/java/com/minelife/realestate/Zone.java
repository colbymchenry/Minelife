package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.region.server.Region;
import com.minelife.util.ArrayUtil;
import com.minelife.util.ListToString;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Zone implements Comparable<Zone> {

    private static final Set<Zone> ZONES = new TreeSet<>();

    private Region region;
    private UUID owner;
    private Set<Member> members = new TreeSet<>();
    private Set<ZonePermission> publicPermissions = new TreeSet<>();
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

    public UUID getUniqueID()
    {
        return region.getUniqueID();
    }

    public String getIntro()
    {
        return intro;
    }

    public String getOutro()
    {
        return outro;
    }

    public boolean canPublic(ZonePermission permission)
    {
        return this.publicPermissions.contains(permission);
    }

    public Set<Member> getMembers()
    {
        return members;
    }

    public Member getMember(UUID playerUUID)
    {
        return getMembers().stream().filter(member -> member.getUniqueID().equals(playerUUID)).findFirst().orElse(null);
    }

    public Member getMember(EntityPlayer player)
    {
        return getMember(player.getUniqueID());
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public void setPublicPermission(ZonePermission permission, boolean value)
    {
        if (value)
            this.publicPermissions.add(permission);
        else
            this.publicPermissions.remove(permission);
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
    }

    public void setOutro(String outro)
    {
        this.outro = outro;
    }

    public boolean hasManagerAuthority(EntityPlayer player)
    {
        if (player.getUniqueID().equals(getOwner())) return true;
        Member member = getMember(player);
        return member != null && member.canMember(ZonePermission.MANAGER);
    }

    public boolean isSubRegion()
    {
        return getRegion().isSubRegion();
    }

    public boolean canPlayer(ZonePermission permission, EntityPlayer player)
    {
        if(getOwner().equals(player.getUniqueID()) || (getMember(player) != null && getMember(player).canMember(permission)))
            return true;

        if(getForSaleSign(Side.SERVER) != null) {
            TileEntityForSaleSign forSaleSign = getForSaleSign(Side.SERVER);
            return (forSaleSign.getRenter() != null && forSaleSign.getRenter().equals(player.getUniqueID())) || (forSaleSign.getMember(player) != null && forSaleSign.getMember(player).canMember(permission));
        }
        return canPublic(permission);
    }

    public boolean hasForSaleSign(Side side)
    {
        World world = side == Side.CLIENT ? region.getEntityWorldClient() : region.getEntityWorld();
        for (Object o : world.loadedTileEntityList) {
            TileEntity tileEntity = (TileEntity) o;
            if(tileEntity instanceof TileEntityForSaleSign) {
                if(region.contains(world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) return true;
            }
        }
        return false;
    }

    public TileEntityForSaleSign getForSaleSign(Side side) {
        World world = side == Side.CLIENT ? region.getEntityWorldClient() : region.getEntityWorld();
        for (Object o : world.loadedTileEntityList) {
            TileEntity tileEntity = (TileEntity) o;
            if(tileEntity instanceof TileEntityForSaleSign) {
                if(region.contains(world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) return (TileEntityForSaleSign) tileEntity;
            }
        }
        return null;
    }

    public boolean isForSale(Side side) {
        TileEntityForSaleSign tileEntityForSaleSign = getForSaleSign(side);
        return tileEntityForSaleSign != null && !tileEntityForSaleSign.isOccupied();
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
        final StringBuilder permissions = new StringBuilder();
        this.publicPermissions.forEach(permission -> permissions.append(permission.name()).append(","));
        if (permissions.toString().contains(","))
            permissions.deleteCharAt(permissions.length() - 1);
        Minelife.SQLITE.query("UPDATE RealEstate_Zones SET " +
                "owner='" + (getOwner() == null ? "NULL" : getOwner().toString()) + "', " +
                "members='" + memberListToString.getString() + "', " +
                "publicBreaking='" + permissions.toString() + "', " +
                "intro='" + getIntro() + "', " +
                "outro='" + getOutro() + "' " +
                "WHERE region='" + getRegion().getUniqueID().toString() + "'");
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, getOwner().toString());
        ByteBufUtils.writeUTF8String(buf, getIntro());
        ByteBufUtils.writeUTF8String(buf, getOutro());
        buf.writeInt(publicPermissions.size());
        publicPermissions.forEach(permission -> ByteBufUtils.writeUTF8String(buf, permission.name()));
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

        int permCount = buf.readInt();
        for (int i = 0; i < permCount; i++)
            zone.publicPermissions.add(ZonePermission.valueOf(ByteBufUtils.readUTF8String(buf)));

        zone.region = Region.fromBytes(buf);
        int members = buf.readInt();
        for (int i = 0; i < members; i++) zone.members.add(Member.fromBytes(buf));
        return zone;
    }

    public static Zone createZone(World world, Vec3 pos1, Vec3 pos2, UUID owner) throws Exception
    {
        CuboidRegion cuboidRegion = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));

        Region region = Region.create(world.getWorldInfo().getWorldName(),
                AxisAlignedBB.getBoundingBox(cuboidRegion.getMinimumPoint().getBlockX(), cuboidRegion.getMinimumPoint().getBlockY(), cuboidRegion.getMinimumPoint().getBlockZ(),
                        cuboidRegion.getMaximumPoint().getBlockX(), cuboidRegion.getMaximumPoint().getBlockY(), cuboidRegion.getMaximumPoint().getBlockZ()));

        Minelife.SQLITE.query("INSERT INTO RealEstate_Zones (region, owner) VALUES ('" + region.getUniqueID().toString() + "', '" + owner.toString() + "')");

        Zone zone = new Zone(region);
        ZONES.add(zone);
        return zone;
    }

    public static Zone getZone(World world, Vec3 pos)
    {
        Region region = Region.getRegionAt(world.getWorldInfo().getWorldName(), pos);

        if (region == null) {
            return null;
        }

        final Region fRegion = region;

        return ZONES.stream().filter(zone -> zone.getRegion().equals(fRegion)).findFirst().orElse(null);
    }

    public static Zone getZone(Region region)
    {
        return ZONES.stream().filter(zone -> zone.getRegion().equals(region)).findFirst().orElse(null);
    }

    public static void deleteZone(UUID regionUniqueID) throws SQLException
    {
        Minelife.SQLITE.query("DELETE FROM RealEstate_Zones WHERE region='" + regionUniqueID.toString() + "'");
        Zone toRemove = ZONES.stream().filter(zone -> zone.getRegion().getUniqueID().equals(regionUniqueID)).findFirst().orElse(null);
        if (toRemove != null) {
            ZONES.remove(toRemove);
        }
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
