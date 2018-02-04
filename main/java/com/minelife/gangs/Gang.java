package com.minelife.gangs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.economy.MoneyHandler;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Selection;
import com.minelife.util.Location;
import com.minelife.util.MLConfig;
import com.minelife.util.Vector;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Gang implements Comparable<Gang> {

    private UUID uuid;
    private MLConfig config;
    private String name;
    private Location home;
    private UUID leader;
    private Set<UUID> officers;
    private Set<UUID> members;
    private Map<UUID, String> titles;

    private int balanceClient;

    public Gang(UUID uuid) throws IOException, InvalidConfigurationException {
        this.uuid = uuid;
        config = new MLConfig(new File(Minelife.getConfigDirectory(), "gangs"), uuid.toString());

        if (config.contains("home.world") && config.contains("home.x") && config.contains("home.y") && config.contains("home.z"))
            home = new Location(config.getString("home.world"), config.getDouble("home.x"), config.getDouble("home.y"), config.getDouble("home.z"));

        leader = config.getUUID("leader", null);

        officers = Sets.newTreeSet();
        config.getStringList("officers").forEach(officer -> officers.add(UUID.fromString(officer)));

        members = Sets.newTreeSet();
        config.getStringList("members").forEach(member -> members.add(UUID.fromString(member)));

        titles = Maps.newHashMap();
        config.getStringList("titles").forEach(title -> {
            String[] data = title.split("\\=");
            titles.put(UUID.fromString(data[0]), title.replaceFirst(data[0] + "=", ""));
        });

        name = config.getString("name");
    }

    public Gang(String name, UUID leader) throws IOException, InvalidConfigurationException {
        this.name = name;
        this.leader = leader;
        this.officers = Sets.newTreeSet();
        this.members = Sets.newTreeSet();
        titles = Maps.newHashMap();
        UUID uuid = UUID.randomUUID();
        config = new MLConfig(new File(Minelife.getConfigDirectory(), "gangs"), uuid.toString());
        this.uuid = uuid;
        config.set("name", name);
        config.set("uuid", uuid.toString());
        config.set("leader", leader);
        config.addDefault("officers", Lists.newArrayList());
        config.addDefault("members", Lists.newArrayList());
        config.addDefault("titles", Lists.newArrayList());
        config.save();
    }

    public String getName() {
        return name;
    }

    public UUID getGangID() {
        return uuid;
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

    // TODO: Implement claiming land needing money for estates and gangs. ATM accesses money from your personal vault. Or you can pull some out of your gangs vault if you are an officer via the ATM
    public int getBalance() {
        return getLeader() == null ? 0 : MoneyHandler.getBalanceVault(getLeader());
    }

    public int getBalanceClient() {
        return balanceClient;
    }

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

    @Override
    public int compareTo(Gang o) {
        return o.getName().compareTo(getName());
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(home != null);
        if (home != null) home.toBytes(buf);
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

        buf.writeInt(getBalance());
    }

    private Gang() {
    }

    public static Gang fromBytes(ByteBuf buf) {
        String name = ByteBufUtils.readUTF8String(buf);
        Location home = null;
        if (buf.readBoolean()) home = Location.fromBytes(buf);
        UUID leader = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int officersSize = buf.readInt();
        Set<UUID> officers = Sets.newTreeSet();
        for (int i = 0; i < officersSize; i++) officers.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        int membersSize = buf.readInt();
        Set<UUID> members = Sets.newTreeSet();
        for (int i = 0; i < membersSize; i++) members.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        int titlesSize = buf.readInt();
        Map<UUID, String> titles = Maps.newHashMap();
        for (int i = 0; i < titlesSize; i++)
            titles.put(UUID.fromString(ByteBufUtils.readUTF8String(buf)), ByteBufUtils.readUTF8String(buf));

        Gang gang = new Gang();
        gang.balanceClient = buf.readInt();
        gang.name = name;
        gang.home = home;
        gang.leader = leader;
        gang.officers = officers;
        gang.members = members;
        gang.titles = titles;
        return gang;
    }
}
