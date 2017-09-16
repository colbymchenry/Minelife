package com.minelife.police.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.police.arresting.ArrestingHandler;
import com.minelife.region.server.Region;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class ServerProxy extends CommonProxy {

    public MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policeofficers (playerUUID TEXT, xp INT)");
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policetickets (ticketID INT, playerUUID VARCHAR(36), officerUUID VARCHAR(36), ticketNBT TEXT)");
        MinecraftForge.EVENT_BUS.register(this);

        config = new MLConfig("police");
        config.addDefault("prison_yard.region_uuid", "");
        config.save();
    }

    /**
        Prevent arrested players from dismounting an officer
     */
    @SubscribeEvent
    public void onDismount(EntityDismountEvent event) {
        if(event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if(ArrestingHandler.isArrested(player)) event.setCanceled(true);
        }
    }

    public void setPrisonYard(Region region) {
        config.set("prison_yard.region_uuid", region == null ? "" : region.getUniqueID().toString());
        config.save();
    }

    public Region getPrisonYard() {
        if(config.getString("prison_yard.region_uuid").isEmpty()) return null;
        return Region.getRegionFromUUID(UUID.fromString(config.getString("prison_yard.region_uuid")));
    }

}
