package com.minelife.gangs.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.network.PacketSendGangMembers;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    // TODO: Implement things to give rep
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Gangs]", Minelife.getDirectory().getAbsolutePath(), "gangs");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS gangs (uuid VARCHAR(36), nbt TEXT)");
        Gang.populateGangs();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLogged(PlayerEvent.PlayerLoggedInEvent event) {
        if(Gang.getGangForPlayer(event.player.getUniqueID()) != null) {
            PacketSendGangMembers.sendMembers(Gang.getGangForPlayer(event.player.getUniqueID()), (EntityPlayerMP) event.player);
        }
    }

}
