package com.minelife.gangs;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.essentials.Location;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.NBTHelper;
import com.minelife.util.NumberConversions;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Gang implements Comparable<Gang> {

    private static final Set<Gang> GANGS = Sets.newTreeSet();
    private UUID uniqueID;
    private NBTTagCompound nbtTag;

    private Gang(UUID uniqueID, NBTTagCompound nbtTag) {
        this.uniqueID = uniqueID;
        this.nbtTag = nbtTag;
    }

    public static Gang createGang(String name, UUID owner) {
        Gang gang = new Gang(UUID.randomUUID(), new NBTTagCompound());
        gang.setName(name);
        gang.setOwner(owner);
        gang.writeToDatabase();
        GANGS.add(gang);
        return gang;
    }

    public static Gang getGang(UUID gangID) {
        return GANGS.stream().filter(gang -> gang.getUniqueID().equals(gangID)).findFirst().orElse(null);
    }

    public static Gang getGang(String name) {
        return GANGS.stream().filter(gang -> gang.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Gang getGangForPlayer(UUID playerID) {
        return GANGS.stream().filter(gang -> gang.getMembers().containsKey(playerID) || gang.getOwner().equals(playerID)).findFirst().orElse(null);
    }

    public static void populateGangs() {
        try {
            ResultSet result = ModGangs.getDatabase().query("SELECT * FROM gangs");
            while (result.next()) {
                GANGS.add(new Gang(UUID.fromString(result.getString("uuid")), NBTHelper.fromString(result.getString("nbt"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disband() {
        GANGS.remove(this);
        try {
            ModGangs.getDatabase().query("DELETE FROM gangs WHERE uuid='" + uniqueID.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return nbtTag.getString("Name");
    }

    public void setName(String name) {
        nbtTag.setString("Name", name);
    }

    public void setOwner(UUID ownerID) {
        nbtTag.setString("Owner", ownerID.toString());
    }

    public UUID getOwner() {
        return nbtTag.hasKey("Owner") ? UUID.fromString(nbtTag.getString("Owner")) : null;
    }

    @SideOnly(Side.SERVER)
    public Estate getBank() {
        if(!nbtTag.hasKey("Bank")) return null;
        return ModRealEstate.getEstate(UUID.fromString(nbtTag.getString("Bank")));
    }

    public void setBank(Estate estate) {
        nbtTag.setString("Bank", estate.getUniqueID().toString());
    }

    @SideOnly(Side.SERVER)
    public long getBalance() {
        if(getBank() == null) return -1;
        return ModEconomy.getBalanceCashPiles(TileEntityCash.getCashPiles(getBank()));
    }

    @SideOnly(Side.SERVER)
    public int deposit(int amount) {
        if(getBank() == null) return -1;
        return ModEconomy.depositCashPiles(TileEntityCash.getCashPiles(getBank()), amount);
    }

    @SideOnly(Side.SERVER)
    public int withdraw(int amount) {
        if(getBank() == null) return -1;
        return ModEconomy.withdrawCashPiles(TileEntityCash.getCashPiles(getBank()), amount);
    }

    @SideOnly(Side.SERVER)
    public Set<Estate> getClaimedEstates() {
        Set<Estate> estates = Sets.newTreeSet();

        if (!nbtTag.hasKey("Estates")) return estates;

        String str = nbtTag.getString("Estates");
        for (String s : str.split(",")) {
            if (!s.isEmpty()) {
                if (ModRealEstate.getEstate(UUID.fromString(s)) != null)
                    estates.add(ModRealEstate.getEstate(UUID.fromString(s)));
            }
        }
        return estates;
    }

    public void setClaimedEstates(Set<Estate> claimedEstates) {
        StringBuilder builder = new StringBuilder();
        claimedEstates.forEach(estate -> builder.append(estate.getUniqueID().toString()).append(","));
        nbtTag.setString("Estates", builder.toString());
    }

    @SideOnly(Side.SERVER)
    public Set<Gang> getAlliances() {
        Set<Gang> gangs = Sets.newTreeSet();

        if (!nbtTag.hasKey("Alliances")) return gangs;

        String str = nbtTag.getString("Alliances");
        for (String s : str.split(",")) {
            if (!s.isEmpty()) {
                if (getGang(UUID.fromString(s)) != null)
                    gangs.add(getGang(UUID.fromString(s)));
            }
        }
        return gangs;
    }

    public void setAlliances(Set<Gang> alliances) {
        StringBuilder builder = new StringBuilder();
        alliances.forEach(gang -> builder.append(gang.getUniqueID().toString()).append(","));
        nbtTag.setString("Alliances", builder.toString());
    }

    public Map<UUID, GangRole> getMembers() {
        Map<UUID, GangRole> members = Maps.newHashMap();

        if (!nbtTag.hasKey("Members")) return members;

        for (String member : nbtTag.getString("Members").split(";")) {
            if (!member.isEmpty() && member.contains(",")) {
                members.put(UUID.fromString(member.split(",")[0]), GangRole.valueOf(member.split(",")[1]));
            }
        }
        return members;
    }

    public void setMembers(Map<UUID, GangRole> members) {
        StringBuilder builder = new StringBuilder();
        members.forEach((uuid, role) -> builder.append(uuid.toString()).append(",").append(role.name()).append(";"));
        nbtTag.setString("Members", builder.toString());
    }

    public Map<UUID, Long> getKills() {
        Map<UUID, Long> kills = Maps.newHashMap();

        if (!nbtTag.hasKey("Kills")) return kills;

        for (String member : nbtTag.getString("Kills").split(";")) {
            if (!member.isEmpty() && member.contains(",")) {
                kills.put(UUID.fromString(member.split(",")[0]), NumberConversions.toLong(member.split(",")[1]));
            }
        }
        return kills;
    }

    public void setKills(Map<UUID, Long> kills) {
        StringBuilder builder = new StringBuilder();
        kills.forEach((uuid, killCount) -> builder.append(uuid.toString()).append(",").append(killCount).append(";"));
        nbtTag.setString("Kills", builder.toString());
    }

    public Map<UUID, Long> getDeaths() {
        Map<UUID, Long> deaths = Maps.newHashMap();

        if (!nbtTag.hasKey("Deaths")) return deaths;

        for (String member : nbtTag.getString("Deaths").split(";")) {
            if (!member.isEmpty() && member.contains(",")) {
                deaths.put(UUID.fromString(member.split(",")[0]), NumberConversions.toLong(member.split(",")[1]));
            }
        }
        return deaths;
    }

    public void setDeaths(Map<UUID, Long> deaths) {
        StringBuilder builder = new StringBuilder();
        deaths.forEach((uuid, deathCount) -> builder.append(uuid.toString()).append(",").append(deathCount).append(";"));
        nbtTag.setString("Deaths", builder.toString());
    }

    public Map<UUID, Long> getRep() {
        Map<UUID, Long> reps = Maps.newHashMap();

        if (!nbtTag.hasKey("Rep")) return reps;

        for (String member : nbtTag.getString("Rep").split(";")) {
            if (!member.isEmpty() && member.contains(",")) {
                reps.put(UUID.fromString(member.split(",")[0]), NumberConversions.toLong(member.split(",")[1]));
            }
        }
        return reps;
    }

    public void setRep(Map<UUID, Long> reps) {
        StringBuilder builder = new StringBuilder();
        reps.forEach((uuid, rep) -> builder.append(uuid.toString()).append(",").append(rep).append(";"));
        nbtTag.setString("Rep", builder.toString());
    }


    public int getFightsWon() {
        return nbtTag.hasKey("FightsWon") ? nbtTag.getInteger("FightsWon") : 0;
    }

    public void setFightsWon(int fightsWon) {
        nbtTag.setInteger("FightsWon", fightsWon);
    }

    public int getFightsLost() {
        return nbtTag.hasKey("FightsLost") ? nbtTag.getInteger("FightsLost") : 0;
    }

    public void setFightsLost(int fightsLost) {
        nbtTag.setInteger("FightsLost", fightsLost);
    }

    public Map<String, Location> getHomes() {
        Map<String, Location> homes = Maps.newHashMap();

        if (!nbtTag.hasKey("Homes")) return homes;

        for (String home : nbtTag.getString("Homes").split(";")) {
            if (!home.isEmpty() && home.contains(",")) {
                String[] data = home.split(",");
                String name = data[0];
                int dimension = NumberConversions.toInt(data[0]);
                int x = NumberConversions.toInt(data[1]);
                int y = NumberConversions.toInt(data[2]);
                int z = NumberConversions.toInt(data[3]);
                float yaw = NumberConversions.toFloat(data[4]);
                float pitch = NumberConversions.toFloat(data[5]);
                homes.put(name, new Location(dimension, x, y, z, yaw, pitch));
            }
        }
        return homes;
    }

    public void setHomes(Map<String, Location> homes) {
        StringBuilder builder = new StringBuilder();
        homes.forEach((name, home) -> builder.append(name).append(",").append(home.getDimension()).append(",")
                .append(home.getX()).append(",").append(home.getY()).append(",").append(home.getZ()).append(",")
                .append(home.getYaw()).append(",").append(home.getPitch()).append(";"));
        nbtTag.setString("Homes", builder.toString());
    }

    public boolean hasPermission(UUID playerID, GangPermission permission) {
        return getOwner().equals(playerID) || (getMembers().containsKey(playerID) && getMembers().get(playerID).permissions.contains(permission));
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public void writeToDatabase() {
        try {
            ResultSet result = ModGangs.getDatabase().query("SELECT * FROM gangs WHERE uuid='" + uniqueID.toString() + "'");
            if (result.next()) {
                ModGangs.getDatabase().query("UPDATE gangs SET nbt='" + nbtTag.toString() + "' WHERE uuid='" + uniqueID.toString() + "'");
            } else {
                ModGangs.getDatabase().query("INSERT INTO gangs (uuid, nbt) VALUES ('" + uniqueID.toString() + "', '" + nbtTag.toString() + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int compareTo(Gang o) {
        return o.uniqueID.compareTo(uniqueID);
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, uniqueID.toString());
        ByteBufUtils.writeTag(buf, nbtTag);
    }

    public static Gang fromBytes(ByteBuf buf) {
        UUID uniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        return new Gang(uniqueID, tagCompound);
    }
}
