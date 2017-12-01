package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.police.Ticket;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.Vec3;

import java.io.File;
import java.util.List;

public class ServerProxy extends MLProxy {

    private static File ticketsDir = new File(Minelife.getConfigDirectory() + File.separator + "tickets");
    private static List<Ticket> tickets = Lists.newArrayList();
    private static MLConfig mainConfig;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        mainConfig = new MLConfig("police");
        mainConfig.save();
        // make the tickets directory
        ticketsDir.mkdir();
        // initiate all tickets
        for (File file : ticketsDir.listFiles()) {
            if(file.getName().endsWith(".yml")) tickets.add(new Ticket(new MLConfig(ticketsDir, file.getName())));
        }
    }

    public void setPrisonEstate(Estate estate) {
        mainConfig.set("prison.estateID", estate.getID());
        mainConfig.save();
    }

    public Estate getPrisonEstate() {
        int estateID = mainConfig.getInt("prison.estateID", -1);
        return estateID < 0 ? null : EstateHandler.getEstate(estateID);
    }

    public void setPrisonSpawn(double x, double y, double z) {
        mainConfig.set("prison.spawn.x", x);
        mainConfig.set("prison.spawn.y", y);
        mainConfig.set("prison.spawn.z", z);
        mainConfig.save();
    }

    public void setPrisonExit(double x, double y, double z) {
        mainConfig.set("prison.exit.x", x);
        mainConfig.set("prison.exit.y", y);
        mainConfig.set("prison.exit.z", z);
        mainConfig.save();
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

}
