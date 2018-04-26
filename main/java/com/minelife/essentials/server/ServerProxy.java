package com.minelife.essentials.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Kit;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.util.MLConfig;
import com.minelife.util.StringHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static MLConfig CONFIG;
    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        CONFIG = new MLConfig("essentials");
        ModEssentials.getConfig().addDefault("teleport_warmup", 5);
        ModEssentials.getConfig().addDefault("teleport_cooldown", 10);
        ModEssentials.getConfig().save();

        MinecraftForge.EVENT_BUS.register(new TeleportHandler());
        MinecraftForge.EVENT_BUS.register(this);

        String prefix = "[MinelifeEssentials]";
        String dbName = "essentials";
        DB = new SQLite(Logger.getLogger("Minecraft"), prefix, Minelife.getDirectory().getAbsolutePath(), dbName);
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS mute (playerID VARCHAR(36), muted TEXT)");
    }


    private static final File fileMOTD = new File(Minelife.getDirectory(), "motd.txt");

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) throws SQLException, IOException {
        if (isNewPlayer(event.player.getUniqueID())) {
            if (Spawn.GetNewSpawn() != null) {
                Location spawn = Spawn.GetNewSpawn();
                ((EntityPlayerMP) event.player).connection.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
            } else if (Spawn.GetSpawn() != null) {
                Location spawn = Spawn.GetSpawn();
                ((EntityPlayerMP) event.player).connection.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
            }

            if(Kit.getKit("default") != null) {
                Kit.giveKit((EntityPlayerMP) event.player, "default");
            }
        }

        if (!fileMOTD.exists()) fileMOTD.createNewFile();

        Scanner scanner = new Scanner(fileMOTD);
        while (scanner.hasNextLine())
            event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(scanner.nextLine(), '&')));
    }

    public static boolean isNewPlayer(UUID playerID) {
        String worldName = FMLServerHandler.instance().getServer().getWorld(0).getWorldInfo().getWorldName();
        File playerFile = new File(System.getProperty("user.dir") + File.separator + worldName + File.separator + "playerdata", playerID.toString() + ".dat");
        return !playerFile.exists();
    }
}
