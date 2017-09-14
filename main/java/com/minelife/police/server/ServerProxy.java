package com.minelife.police.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.region.server.Region;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.UUID;

public class ServerProxy extends CommonProxy {

    public MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policeofficers (playerUUID TEXT, xp INT)");
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policetickets (ticketID INT, playerUUID VARCHAR(36), officerUUID VARCHAR(36), ticketNBT TEXT)");

        config = new MLConfig("police");
        config.addDefault("prison_yard.region_uuid", "");
        config.save();
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
