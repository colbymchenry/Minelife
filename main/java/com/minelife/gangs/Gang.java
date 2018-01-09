package com.minelife.gangs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.util.Location;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
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
    }

    public Gang(String name, UUID leader) throws IOException, InvalidConfigurationException {
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

    public boolean setName(String name) {
        if(ModGangs.getGang(name) != null) return false;
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
        config.set("home", "");
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
}
