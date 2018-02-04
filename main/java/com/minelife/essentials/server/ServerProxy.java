package com.minelife.essentials.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.util.Location;
import com.minelife.util.MLConfig;
import com.minelife.util.StringHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModEssentials.config = new MLConfig("essentials");
        ModEssentials.config.addDefault("teleport_warmup", 5);
        ModEssentials.config.addDefault("teleport_cooldown", 10);
        ModEssentials.config.save();

        FMLCommonHandler.instance().bus().register(new TeleportHandler());
        FMLCommonHandler.instance().bus().register(this);

        String prefix = "[MinelifeEssentials]";
        String directory = Minelife.getConfigDirectory().getAbsolutePath() + File.separator + "essentials";
        String dbName = "storage";
        ModEssentials.db = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        ModEssentials.db.open();
    }


    private static final File fileMOTD = new File(Minelife.getConfigDirectory(), "motd.txt");

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) throws SQLException, IOException {
        if(!MoneyHandler.hasATM(event.player.getUniqueID())) {
            if(Spawn.GetSpawn() != null) {
                Location spawn = Spawn.GetSpawn();
                ((EntityPlayerMP) event.player).playerNetServerHandler.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
            }
        }

        if(!fileMOTD.exists()) {
            fileMOTD.createNewFile();
        }

        Scanner scanner = new Scanner(fileMOTD);
        while(scanner.hasNextLine()) {
            event.player.addChatMessage(new ChatComponentText(StringHelper.ParseFormatting(scanner.nextLine(), '&')));
        }
    }
}
