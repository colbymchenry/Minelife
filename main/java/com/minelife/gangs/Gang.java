package com.minelife.gangs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Selection;
import com.minelife.util.Location;
import com.minelife.util.MLConfig;
import com.minelife.util.Vector;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Gang implements Comparable<Gang> {

    private MLConfig config;
    private String name;
    private Location home;
    private UUID leader;
    private Set<UUID> officers;
    private Set<UUID> members;
    private Map<UUID, String> titles;
    private double balance;
    private Selection vaultBounds;
    private int vaultDimension;

    public Gang(String name) throws IOException, InvalidConfigurationException {
        this.name = name;
        config = new MLConfig(new File(Minelife.getConfigDirectory(), "gangs"), name);

        if (config.contains("home.world") && config.contains("home.x") && config.contains("home.y") && config.contains("home.z"))
            home = new Location(config.getString("home.world"), config.getDouble("home.x"), config.getDouble("home.y"), config.getDouble("home.z"));

        leader = config.getUUID("leader", null);
        balance = config.getDouble("balance", 0);

        officers = Sets.newTreeSet();
        config.getStringList("officers").forEach(officer -> officers.add(UUID.fromString(officer)));

        members = Sets.newTreeSet();
        config.getStringList("members").forEach(member -> members.add(UUID.fromString(member)));

        titles = Maps.newHashMap();
        config.getStringList("titles").forEach(title -> {
            String[] data = title.split("\\=");
            titles.put(UUID.fromString(data[0]), title.replaceFirst(data[0] + "=", ""));
        });

        if(config.contains("vault")) {
            vaultBounds = new Selection();
            vaultBounds.setPos1(config.getInt("vault.minX"), config.getInt("vault.minY"), config.getInt("vault.minZ"));
            vaultBounds.setPos2(config.getInt("vault.maxX"), config.getInt("vault.maxY"), config.getInt("vault.maxZ"));
            vaultDimension = config.getInt("vault.dimension");
        }
    }

    public Gang(String name, UUID leader) throws IOException, InvalidConfigurationException {
        this.name = name;
        this.leader = leader;
        this.balance = 0.0;
        this.officers = Sets.newTreeSet();
        this.members = Sets.newTreeSet();
        titles = Maps.newHashMap();
        config = new MLConfig(new File(Minelife.getConfigDirectory(), "gangs"), name);
        config.set("name", name);
        config.set("leader", leader);
        config.set("balance", 0.0);
        config.addDefault("officers", Lists.newArrayList());
        config.addDefault("members", Lists.newArrayList());
        config.addDefault("titles", Lists.newArrayList());
        config.save();
    }

    public String getName() {
        return name;
    }

    public Location getHome() {
        return home;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getOfficers() {
        return officers;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Map<UUID, String> getTitles() {
        return titles;
    }

    public double getBalance() {
        return balance;
    }

    // TODO: Finish up the vault. How? They make a selection that defines where the vault is.
    // TODO: The server will look for stacked cash via the CashBlock (NOT MADE YET).
    // TODO: The cash block will change height based on its contents. Max amount of cash in one block is $10K.
    // TODO: Make the cash block and the cash item in the Economy mod
    // TODO: When player's die they drop their cash.
    // TODO: The CashBlock will need a tileentity to store the cash data. Each piece of cash is worth $5
    // TODO: Need a way to put the cash into their account/wallet. Maybe they can open their wallet with a GUI and drag the cash into the wallet.
    // TODO: If they want to pull the cash back out into items they will need to go to a banker.7

    public boolean setName(String name) {
        if (ModGangs.getGang(name) != null) return false;
        this.name = name;
        config.set("name", name);
        config.save();
        return true;
    }

    public boolean setHome(World world, double x, double y, double z) {
        config.set("home.world", world.getWorldInfo().getWorldName());
        config.set("home.x", x);
        config.set("home.y", y);
        config.set("home.z", z);
        config.save();
        this.home = new Location(world.getWorldInfo().getWorldName(), x, y, z);
        return true;
    }

    public boolean removeHome() {
        config.removeSection("home");
        config.save();
        home = null;
        return true;
    }

    public boolean setLeader(UUID id) {
        config.set("leader", id.toString());
        config.save();
        this.leader = id;
        return true;
    }

    public boolean setOfficers(Set<UUID> officers) {
        List<String> officersStringList = Lists.newArrayList();
        officers.forEach(officer -> officersStringList.add(officer.toString()));
        config.set("officers", officersStringList);
        config.save();
        this.officers = officers;
        return true;
    }

    public boolean setMembers(Set<UUID> members) {
        List<String> membersStringList = Lists.newArrayList();
        members.forEach(member -> membersStringList.add(member.toString()));
        config.set("members", membersStringList);
        config.save();
        this.members = members;
        return true;
    }

    public boolean setTitles(Map<UUID, String> titles) {
        List<String> titlesStringList = Lists.newArrayList();
        titles.forEach((uuid, title) -> titlesStringList.add(uuid.toString() + "=" + title));
        config.set("titles", titlesStringList);
        config.save();
        this.titles = titles;
        return true;
    }

    public boolean setBalance(double balance) {
        this.balance = balance;
        config.set("balance", balance);
        config.save();
        return true;
    }

    @Override
    public int compareTo(Gang o) {
        return o.getName().compareTo(getName());
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(home != null);
        if(home != null) home.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, leader.toString());
        buf.writeInt(officers.size());
        officers.forEach(uuid -> ByteBufUtils.writeUTF8String(buf, uuid.toString()));
        buf.writeInt(members.size());
        members.forEach(uuid -> ByteBufUtils.writeUTF8String(buf, uuid.toString()));
        buf.writeInt(titles.size());
        titles.forEach((uuid, name) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            ByteBufUtils.writeUTF8String(buf, name);
        });
        buf.writeDouble(balance);
    }

    private Gang(){}

    public static Gang fromBytes(ByteBuf buf) {
        String name = ByteBufUtils.readUTF8String(buf);
        Location home = null;
        if(buf.readBoolean()) home = Location.fromBytes(buf);
        UUID leader = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int officersSize = buf.readInt();
        Set<UUID> officers = Sets.newTreeSet();
        for (int i = 0; i < officersSize; i++) officers.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        int membersSize = buf.readInt();
        Set<UUID> members = Sets.newTreeSet();
        for (int i = 0; i < membersSize; i++) members.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        int titlesSize = buf.readInt();
        Map<UUID, String> titles = Maps.newHashMap();
        for (int i = 0; i < titlesSize; i++) titles.put(UUID.fromString(ByteBufUtils.readUTF8String(buf)), ByteBufUtils.readUTF8String(buf));
        double balance= buf.readDouble();
        Gang gang = new Gang();
        gang.name = name;
        gang.home = home;
        gang.leader = leader;
        gang.officers = officers;
        gang.members = members;
        gang.titles = titles;
        gang.balance = balance;
        return gang;
    }
}
