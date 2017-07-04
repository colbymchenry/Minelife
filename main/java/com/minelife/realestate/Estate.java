package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.region.server.Region;
import com.minelife.region.server.RegionBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Estate {

    private static final List<Estate> ESTATES = Lists.newArrayList();

    private Region region;
    private UUID uuid, owner;

    private Estate() {}

    private Estate(UUID uuid) throws SQLException
    {
        this.uuid = uuid;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Estates WHERE uuid='" + uuid.toString() + "';");
        this.region = Region.getRegion(UUID.fromString(result.getString("region")));
        this.owner = result.getString("owner") != null && !result.getString("owner").isEmpty() ? UUID.fromString(result.getString("owner")) : null;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public Region getRegion()
    {
        return region;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public void setOwner(UUID player)
    {
        try {
            Minelife.SQLITE.query("UPDATE RealEstate_Estates SET owner='" + player.toString() + "' WHERE uuid='" + uuid.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @SideOnly(Side.SERVER)
    public static void initEstates() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Estates");
        while (result.next()) ESTATES.add(new Estate(UUID.fromString(result.getString("uuid"))));
    }

    @SideOnly(Side.SERVER)
    public static Estate createEstate(World world, Chunk chunk) throws Exception
    {
        int x = chunk.xPosition * 16;
        int z = chunk.zPosition * 16;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x, 0, z, x + 16, 256, z + 16);

        // check if we intersect with any regions
        Region intersectingRegion = Region.REGIONS.stream().filter(region -> region.getAxisAlignedBB().intersectsWith(bounds)).findFirst().orElse(null);
        if (intersectingRegion != null)
            throw new CustomMessageException(ModRealEstate.getMessage("Message_Intersects"));

        String worldName = world.getWorldInfo().getWorldName();
        int[] min = {x, 0, z};
        int[] max = {x + 16, 256, z + 16};

        Region region = Region.createRegion(worldName, min, max);
        UUID estateUUID = UUID.randomUUID();

        Minelife.SQLITE.query("INSERT INTO RealEstate_Estates (uuid, region) VALUES ('" + estateUUID.toString() + "', '" + region.getUUID().toString() + "');");

        Estate estate = new Estate(estateUUID);
        ESTATES.add(estate);

        return estate;
    }

    @SideOnly(Side.SERVER)
    public static Estate getEstate(World world, int x, int z)
    {
        return ESTATES.stream().filter(estate -> estate.getRegion().doesContain(x, 50, z)).findFirst().orElse(null);
    }

    @SideOnly(Side.SERVER)
    public static Estate getEstate(UUID uuid)
    {
        return ESTATES.stream().filter(estate -> estate.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, getUUID().toString());
        ByteBufUtils.writeUTF8String(buf, getOwner() != null ? getOwner().toString() : "");
        getRegion().toBytes(buf);
    }

    public static Estate fromBytes(ByteBuf buf)
    {
        Estate estate = new Estate();
        estate.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String owner = ByteBufUtils.readUTF8String(buf);
        if(!owner.isEmpty()) estate.owner = UUID.fromString(owner);
        estate.region = (Region) RegionBase.fromBytes(buf);
        return estate;
    }
}
