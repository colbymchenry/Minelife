package com.minelife.tdm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.drugs.XRayEffect;
import com.minelife.drugs.item.ItemJoint;
import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Heal;
import com.minelife.tdm.network.PacketSendSecondsTillStart;
import com.minelife.tdm.network.PacketUpdateLobby;
import com.minelife.util.PlayerHelper;
import com.minelife.util.SoundTrack;
import com.minelife.util.configuration.InvalidConfigurationException;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Match implements Comparable<Match> {

    public static final Set<Match> ACTIVE_MATCHES = Sets.newTreeSet();

    private Arena arena;
    private int countdownStart, countdownBetweenRounds, team1MaxSize, team2MaxSize;
    private long startTime, startRoundTime;
    private Set<UUID> team1, team2;
    private Map<UUID, List<ItemStack>> playerLoadouts = Maps.newHashMap();
    private Map<UUID, SavedInventory> previousInventory = Maps.newHashMap();
    private int rounds, team1Wins, team2Wins;

    private Match() {
    }

    public static Match getMatch(EntityPlayer player) {
        return ACTIVE_MATCHES.stream().filter(match -> match.getTeam1().contains(player.getUniqueID()) || match.getTeam2().contains(player.getUniqueID())).findFirst().orElse(null);
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

    public Match setTeam1MaxSize(int size) {
        this.team1MaxSize = size;
        return this;
    }


    public Match setTeam2MaxSize(int size) {
        this.team2MaxSize = size;
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

    public boolean started() {
        return System.currentTimeMillis() > getStartTime();
    }

    public void setLoadout(UUID player, ItemStack... items) {
        playerLoadouts.put(player, Lists.newArrayList(items));
    }

    public void setPreviousInventory(EntityPlayerMP player) {
        SavedInventory savedInventory = null;
        try {
            savedInventory = new SavedInventory(player.getUniqueID());
            savedInventory.setItems(player.inventory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        previousInventory.put(player.getUniqueID(), savedInventory);
    }

    public void setTeam1Wins(int wins) {
        this.team1Wins = wins;
    }

    public void setTeam2Wins(int wins) {
        this.team2Wins = wins;
    }

    public boolean canBegin() {
        if (team1.size() > 0 && team2.size() > 0 && team1.size() - team2.size() < 3) {
            int notReady = 0;
            for (UUID uuid : team1) {
                if (!playerLoadouts.containsKey(uuid)) notReady++;
            }
            for (UUID uuid : team2) {
                if (!playerLoadouts.containsKey(uuid)) notReady++;
            }

            if (((team1.size() + team2.size()) - notReady) / (team1.size() + team2.size()) >= 0.9) return true;
        }
        return false;
    }

    public Arena getArena() {
        return arena;
    }

    public int getCountdownStart() {
        return countdownStart;
    }

    public void setCountdownStart(int countdownStart) {
        this.countdownStart = countdownStart;
    }

    public int getCountdownBetweenRounds() {
        return countdownBetweenRounds;
    }

    public Set<UUID> getTeam1() {
        return team1;
    }

    public void setTeam1(Set<UUID> team1) {
        this.team1 = team1;
    }

    public Set<UUID> getTeam2() {
        return team2;
    }

    public void setTeam2(Set<UUID> team2) {
        this.team2 = team2;
    }

    public List<ItemStack> getPlayerLoadout(UUID playerID) {
        return playerLoadouts.get(playerID);
    }

    public SavedInventory getPreviousInventory(UUID playerID) {
        return previousInventory.get(playerID);
    }

    public int getRounds() {
        return rounds;
    }

    public int getTeam1Wins() {
        return team1Wins;
    }

    public int getTeam2Wins() {
        return team2Wins;
    }

    public int getTeam1MaxSize() {
        return team1MaxSize;
    }

    public int getTeam2MaxSize() {
        return team2MaxSize;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void addTeam1(UUID player) {
        team1.add(player);
    }

    public void addTeam2(UUID player) {
        team2.add(player);
    }

    public void end() {
        getTeam1().forEach(playerID -> kickPlayer(PlayerHelper.getPlayer(playerID)));
        getTeam2().forEach(playerID -> kickPlayer(PlayerHelper.getPlayer(playerID)));
        ACTIVE_MATCHES.remove(this);
    }

    public void start() {
//        if(!canBegin()) {
//            end();
//            return;
//        }
        startRoundTime = getCountdownBetweenRounds();
        team1.forEach(playerID -> {
            EntityPlayerMP player = PlayerHelper.getPlayer(playerID);
            if (getPlayerLoadout(playerID) != null) {
                player.inventory.clear();
                player.closeScreen();
                TeleportHandler.teleport(player, new Location(arena.getEstate().getWorld().provider.getDimension(), arena.getTeam1Spawn().getX(), arena.getTeam1Spawn().getY() + 0.3, arena.getTeam1Spawn().getZ(), player.rotationYaw, player.rotationPitch), 0);
                for (int i = 0; i < getPlayerLoadout(playerID).size(); i++)
                    player.inventory.setInventorySlotContents(i, getPlayerLoadout(playerID).get(i));
            } else {
                kickPlayer(player);
            }
        });
        team2.forEach(playerID -> {
            EntityPlayerMP player = PlayerHelper.getPlayer(playerID);
            if (getPlayerLoadout(playerID) != null) {
                player.inventory.clear();
                player.closeScreen();
                TeleportHandler.teleport(player, new Location(arena.getEstate().getWorld().provider.getDimension(), arena.getTeam2Spawn().getX(), arena.getTeam2Spawn().getY() + 0.3, arena.getTeam2Spawn().getZ(), player.rotationYaw, player.rotationPitch), 0);
                for (int i = 0; i < getPlayerLoadout(playerID).size(); i++)
                    player.inventory.setInventorySlotContents(i, getPlayerLoadout(playerID).get(i));
            } else {
                kickPlayer(player);
            }
        });
    }

    public boolean roundStarted() {
        return startRoundTime <= 0;
    }

    public void kickPlayer(EntityPlayerMP player) {
        if (player != null) {
            TeleportHandler.teleport(player, new Location(arena.getEstate().getWorld().provider.getDimension(),
                    arena.getExitSpawn().getX(), arena.getExitSpawn().getY(), arena.getExitSpawn().getZ(),
                    player.rotationYaw, player.rotationPitch), 0);
            player.inventory.clear();
            getPreviousInventory(player.getUniqueID()).getItems().forEach((slot, stack) -> player.inventory.setInventorySlotContents(slot, stack));
            Heal.healPlayer(player);
            getPreviousInventory(player.getUniqueID()).delete();
            previousInventory.remove(player.getUniqueID());
            team1.remove(player.getUniqueID());
            team2.remove(player.getUniqueID());
            playerLoadouts.remove(player.getUniqueID());
            sendUpdatesToLobby();
        }
    }

    public void sendUpdatesToLobby() {
        getTeam1().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketUpdateLobby(getTeam1(), getTeam2()), PlayerHelper.getPlayer(playerID));
            }
        });
        getTeam2().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketUpdateLobby(getTeam1(), getTeam2()), PlayerHelper.getPlayer(playerID));
            }
        });
    }

    public void sendSecondsTillStart() {
        getTeam1().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketSendSecondsTillStart(getSecondsTillStart()), PlayerHelper.getPlayer(playerID));
            }
        });
        getTeam2().forEach(playerID -> {
            if (PlayerHelper.getPlayer(playerID) != null) {
                Minelife.getNetwork().sendTo(new PacketSendSecondsTillStart(getSecondsTillStart()), PlayerHelper.getPlayer(playerID));
            }
        });
    }

    public int getSecondsTillStart() {
        return (int) ((getStartTime() - System.currentTimeMillis()) / 1000L);
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
        buf.writeInt(rounds);
        buf.writeInt(team1Wins);
        buf.writeInt(team2Wins);
        buf.writeLong(startTime);
        buf.writeInt(team1MaxSize);
        buf.writeInt(team2MaxSize);
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
        int rounds = buf.readInt();
        int team1wins = buf.readInt();
        int team2wins = buf.readInt();
        Match match = null;
        try {
            match = Match.builder().setArena(new Arena(arenaName, false)).setStartCountdown(countDownStart).setCountdownBetweenRounds(countdownBetweenRounds);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setRounds(rounds);
        match.setTeam1Wins(team1wins);
        match.setTeam2Wins(team2wins);
        match.startTime = buf.readLong();
        match.setTeam1MaxSize(buf.readInt());
        match.setTeam2MaxSize(buf.readInt());
        return match;
    }

    // TODO: For syncing startTime etc may need to either sync via ping or when player connects to a server go ahead and sync the
    // TODO: time by immediately finding the difference between their System.currentTimeMillis.
    public static class MatchTicker {
        long lastSec = 0;

        @SubscribeEvent
        public void serverTick(TickEvent.ServerTickEvent event) {
            long sec = System.currentTimeMillis() / 1000;
            if (sec == lastSec) return;
            lastSec = sec;

            for (Match match : Match.ACTIVE_MATCHES) {
                if (!match.started()) {
                    if (match.getSecondsTillStart() < 1) {
//                        if(match.canBegin()) {
                        match.start();
//                        } else {
//
//                        }
                    } else {
                        match.sendSecondsTillStart();
                    }
                } else {
//                    if (match.team1.isEmpty() || match.team2.isEmpty()) match.end();
                    if (!match.roundStarted()) {
                        Set<UUID> players = match.team1;
                        players.addAll(match.team2);

                        for (UUID playerID : players) {
                            EntityPlayerMP player = PlayerHelper.getPlayer(playerID);
                            if (player != null) {
                                player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) match.startRoundTime * 20, 10, false, false));
                                ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + match.startRoundTime, null, 1, player);
                                SoundTrack soundTrack = new SoundTrack();
                                if (match.startRoundTime == 1) {
                                    soundTrack.addPart("minecraft:block.note.pling", 0L, 1, 1.5F);
                                } else {
                                    soundTrack.addPart("minecraft:block.note.pling", 0L, 1, 1);
                                }
                                soundTrack.play(player);
                            }
                        }

                        match.startRoundTime--;
                    }
                }
            }
        }

    }
}
