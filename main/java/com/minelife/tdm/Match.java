package com.minelife.tdm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Match implements Comparable<Match> {

    public static final Set<Match> ACTIVE_MATCHES = Sets.newTreeSet();

    private Arena arena;
    private int countdownStart, countdownBetweenRounds;
    private Set<UUID> team1, team2;
    private Map<UUID, List<ItemStack>> playerLoadouts = Maps.newHashMap();
    private Map<UUID, List<Object[]>> previousInventory = Maps.newHashMap();
    private int rounds, team1Wins, team2Wins;

    private Match() {
    }

    public static Match builder() {
        return new Match();
    }

    public Match setArena(Arena arena) {
        this.arena = arena;
        return this;
    }

    public Match setTeam1(UUID... uuids) {
        this.team1 = Sets.newTreeSet();
        this.team1.addAll(Lists.newArrayList(uuids));
        return this;
    }

    public Match setTeam2(UUID... uuids) {
        this.team2 = Sets.newTreeSet();
        this.team2.addAll(Lists.newArrayList(uuids));
        return this;
    }

    public Match setStartCountdown(int countdown) {
        this.countdownStart = countdown;
        return this;
    }

    public Match setCountdownBetweenRounds(int countdown) {
        this.countdownBetweenRounds = countdown;
        return this;
    }

    public Match setRounds(int rounds) {
        this.rounds = rounds;
        return this;
    }

    public Match build() {
        if (this.rounds < 3) return null;
        if (this.team1 == null) return null;
        if (this.team2 == null) return null;
        if (this.arena == null) return null;
        return this;
    }

    public void setLoadout(UUID player, ItemStack... items) {
        playerLoadouts.put(player, Lists.newArrayList(items));
    }

    public void setPreviousInventory(UUID player, Object[]... items) {
        previousInventory.put(player, Lists.newArrayList(items));
    }

    public void setTeam1Wins(int wins) {
        this.team1Wins = wins;
    }

    public void setTeam2Wins(int wins) {
        this.team2Wins = wins;
    }

    public boolean canBegin() {
        if(team1.size() > 0 && team2.size() > 0 && team1.size() - team2.size() < 3) {
            int notReady = 0;
            for (UUID uuid : team1) {
                if(!playerLoadouts.containsKey(uuid)) notReady++;
            }
            for (UUID uuid : team2) {
                if(!playerLoadouts.containsKey(uuid)) notReady++;
            }

            if(((team1.size() + team2.size()) - notReady) / (team1.size() + team2.size()) >= 0.9) return true;
        }
        return false;
    }

    @Override
    public int compareTo(Match o) {
        return arena.getName().compareTo(o.arena.getName());
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, arena.getName());
        buf.writeInt(countdownStart);
        buf.writeInt(countdownBetweenRounds);
        buf.writeInt(team1.size());
        team1.forEach(uuid -> ByteBufUtils.writeUTF8String(buf, uuid.toString()));
        buf.writeInt(team2.size());
        team2.forEach(uuid -> ByteBufUtils.writeUTF8String(buf, uuid.toString()));
        buf.writeInt(playerLoadouts.size());
        playerLoadouts.forEach(((uuid, itemStacks) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            buf.writeInt(itemStacks.size());
            itemStacks.forEach(itemStack -> ByteBufUtils.writeItemStack(buf, itemStack));
        }));

        buf.writeInt(rounds);
        buf.writeInt(team1Wins);
        buf.writeInt(team2Wins);

        buf.writeInt(previousInventory.size());
        previousInventory.forEach(((uuid, objects) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            buf.writeInt(objects.size());
            objects.forEach(slot -> {
                buf.writeInt((Integer) slot[0]);
                ByteBufUtils.writeItemStack(buf, (ItemStack) slot[1]);
            });
        }));
    }

    public static Match fromBytes(ByteBuf buf) {
        String arenaName = ByteBufUtils.readUTF8String(buf);
        int countDownStart = buf.readInt();
        int countdownBetweenRounds = buf.readInt();
        Set<UUID> team1 = Sets.newTreeSet();
        int team1Size = buf.readInt();
        for (int i = 0; i < team1Size; i++) team1.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        Set<UUID> team2 = Sets.newTreeSet();
        int team2Size = buf.readInt();
        for (int i = 0; i < team2Size; i++) team2.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        Map<UUID, List<ItemStack>> playerLoadouts = Maps.newHashMap();
        int playerLoadoutSize = buf.readInt();
        for (int i = 0; i < playerLoadoutSize; i++) {
            UUID playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            List<ItemStack> items = Lists.newArrayList();
            int itemsSize = buf.readInt();
            for (int i1 = 0; i1 < itemsSize; i1++) items.add(ByteBufUtils.readItemStack(buf));
            playerLoadouts.put(playerID, items);
        }
        int rounds = buf.readInt();
        int team1wins = buf.readInt();
        int team2wins = buf.readInt();
        return null;
    }
}
