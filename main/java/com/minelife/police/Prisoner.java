package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.server.Prison;
import com.minelife.tdm.SavedInventory;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Prisoner {

    private UUID playerID;
    private List<ChargeType> charges;
    private long timeServed;

    public Prisoner() {}

    public Prisoner(ResultSet result) throws SQLException {
        this.playerID = UUID.fromString(result.getString("uuid"));
        this.charges = Lists.newArrayList();
        for (String s : result.getString("charges").split(","))
            if (!s.isEmpty()) charges.add(ChargeType.valueOf(s));
    }

    public Prisoner(UUID playerID, List<ChargeType> charges) {
        this.playerID = playerID;
        this.charges = charges;
        this.timeServed = 0L;
    }

    public static Prisoner getPrisoner(UUID playerID) {
        try {
            ResultSet result = ModPolice.getDatabase().query("SELECT * FROM prisoners WHERE uuid='" + playerID.toString() + "'");
            if (result.next()) return new Prisoner(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSavedInventory(IInventory inventory) {
        SavedInventory savedInventory = null;
        try {
            savedInventory = new SavedInventory(playerID);
            savedInventory.setItems(inventory);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public SavedInventory getSavedInventory() {
        SavedInventory savedInventory = null;
        try {
            savedInventory = new SavedInventory(playerID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return savedInventory;
    }

    public long getTimeServed() {
        return timeServed;
    }

    public void setTimeServed(long timeServed) {
        this.timeServed = timeServed;
    }

    public List<ChargeType> getCharges() {
        return charges;
    }

    public void setCharges(List<ChargeType> charges) {
        this.charges = charges;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public EntityPlayer getPlayer() {
        return PlayerHelper.getPlayer(playerID);
    }

    public int getTotalSentenceTime() {
        return getTotalBailAmount() / 5;
    }

    public int getTotalBailAmount() {
        return charges.stream().mapToInt(charge -> charge.chargeAmount).sum();
    }

    public void save() throws SQLException {
        ResultSet result = ModPolice.getDatabase().query("SELECT * FROM prisoners WHERE uuid='" + playerID.toString() + "'");
        StringBuilder chargesBuilder = new StringBuilder();

        charges.forEach(chargeType -> chargesBuilder.append(chargeType.name()).append(","));

        if (result.next())
            ModPolice.getDatabase().query("UPDATE prisoners SET charges='" + chargesBuilder.toString() + "', timeServed='" + timeServed + "' WHERE uuid='" + playerID.toString() + "'");
        else
            ModPolice.getDatabase().query("INSERT INTO prisoners (uuid, charges, timeServed) VALUES ('" + playerID.toString() + "', '" + chargesBuilder.toString() + "', '" + timeServed + "')");
    }

    public void delete() throws SQLException {
        ModPolice.getDatabase().query("DELETE FROM prisoners WHERE uuid='" + playerID.toString() + "'");
    }

    public static boolean isPrisoner(UUID playerID) {
        try {
            ResultSet result = ModPolice.getDatabase().query("SELECT * FROM prisoners WHERE uuid='" + playerID.toString() + "'");
            if (result.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    static long lastSec = 0;

// TODO: maybe don't want to save all the time lol

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!isPrisoner(event.player.getUniqueID())) return;

        Prisoner prisoner = getPrisoner(event.player.getUniqueID());
        if(prisoner == null) return;

        Prison prison = Prison.getPrison(event.player.getPosition());

        try {
            if (prison == null && prisoner.timeServed > 10) {
                prisoner.delete();
                if(prisoner.getSavedInventory() != null) prisoner.getSavedInventory().delete();
            } else {
                long sec = System.currentTimeMillis() / 1000;

                if(sec != lastSec) {
                    lastSec = sec;
                    prisoner.timeServed++;

                    if(prisoner.timeServed >= prisoner.getTotalSentenceTime()) {
                        prisoner.delete();
                        event.player.inventory.clear();

                        if(prisoner.getSavedInventory() != null) {
                            prisoner.getSavedInventory().getItems().forEach((slot, stack) -> event.player.inventory.setInventorySlotContents(slot, stack));
                            event.player.inventoryContainer.detectAndSendChanges();
                            prisoner.getSavedInventory().delete();
                        }

                        event.player.setPositionAndUpdate(Spawn.GetSpawn().getX(), Spawn.GetSpawn().getY(), Spawn.GetSpawn().getZ());
                    } else {
                        prisoner.save();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!isPrisoner(event.player.getUniqueID())) return;
        Prison prison = Prison.getClosestPrison(event.player.getPosition());

        if(prison == null) return;

        Prisoner prisoner = Prisoner.getPrisoner(event.player.getUniqueID());

        BlockPos dropOff = prison.getDropOffPos();
        event.player.setPositionAndUpdate(dropOff.getX() + 0.5, dropOff.getY() + 0.5, dropOff.getZ() + 0.5);
        prisoner.setSavedInventory(event.player.inventory);
        event.player.inventory.clear();
        event.player.inventoryContainer.detectAndSendChanges();
        event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&4----------------------------------", '&')));
        event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6You are jailed for: &c" + ((prisoner.getTotalSentenceTime() / 60)) + " &6minutes.", '&')));
        event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Total for bail: &c$" + NumberConversions.format((prisoner.getTotalBailAmount())) + "&6.", '&')));
        event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&4----------------------------------", '&')));
    }

}
