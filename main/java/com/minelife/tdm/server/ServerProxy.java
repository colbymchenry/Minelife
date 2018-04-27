package com.minelife.tdm.server;

import com.minelife.MLProxy;
import com.minelife.essentials.Location;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.tdm.Arena;
import com.minelife.tdm.Match;
import com.minelife.tdm.SavedInventory;
import com.minelife.util.PlayerHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.IOException;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Arena.initArenas();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Match.MatchTicker());
        MinecraftForge.EVENT_BUS.register(new Arena.ArenaTicker());
    }

    // TODO: We've gotten the players in the game and the round to start, now to disable friendly fire, and implement spectating etc.
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(SavedInventory.hasSavedInventory(event.player.getUniqueID())) {
            try {
                SavedInventory savedInventory = new SavedInventory(event.player.getUniqueID());
                event.player.inventory.clear();
                savedInventory.getItems().forEach((slot, stack) -> event.player.inventory.setInventorySlotContents(slot, stack));
                event.player.inventoryContainer.detectAndSendChanges();
                savedInventory.delete();
                TeleportHandler.teleport((EntityPlayerMP) event.player, Spawn.GetSpawn(), 0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        Estate estateAtPlayer = ModRealEstate.getEstateAt(event.player.getEntityWorld(), event.player.getPosition());
        if (estateAtPlayer != null) {
            Arena arena = Arena.ARENAS.stream().filter(a -> a.getEstate().equals(estateAtPlayer)).findFirst().orElse(null);
            if (arena != null) {
                if (PlayerHelper.isOp((EntityPlayerMP) event.player)) return;
                if (arena.getCurrentMatch() == null || (arena.getCurrentMatch().getTeam1().contains(event.player.getUniqueID()) && arena.getCurrentMatch().getTeam2().contains(event.player.getUniqueID())))
                    TeleportHandler.teleport((EntityPlayerMP) event.player, new Location(estateAtPlayer.getWorld().provider.getDimension(), arena.getExitSpawn().getX(), arena.getExitSpawn().getY(), arena.getExitSpawn().getZ()), 0);
            }
        }
    }
}
