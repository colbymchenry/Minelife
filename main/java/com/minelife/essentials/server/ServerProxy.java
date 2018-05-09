package com.minelife.essentials.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Home;
import com.minelife.essentials.server.commands.Kit;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.util.MLConfig;
import com.minelife.util.StringHelper;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
        event.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(10);

        if (isNewPlayer(event.player.getUniqueID())) {
            if (Spawn.GetNewSpawn() != null) {
                Location spawn = Spawn.GetNewSpawn();
                ((EntityPlayerMP) event.player).connection.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
            } else if (Spawn.GetSpawn() != null) {
                Location spawn = Spawn.GetSpawn();
                ((EntityPlayerMP) event.player).connection.setPlayerLocation(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
            }

            if (Kit.getKit("default") != null) {
                Kit.giveKit((EntityPlayerMP) event.player, "default");
            }
        }

        if (!fileMOTD.exists()) fileMOTD.createNewFile();

        Scanner scanner = new Scanner(fileMOTD);
        while (scanner.hasNextLine())
            event.player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(scanner.nextLine(), '&')));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBreak(BlockEvent.BreakEvent event) {
        UUID playerUUID = Home.getHomeAtLoc(event.getPlayer().dimension, event.getPos());
        if (playerUUID != null) {
            Home.DelHome("default", playerUUID);
        } else {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.BED) {
                EnumFacing facing = event.getState().getValue(BlockBed.FACING);
                BlockPos pos = event.getPos();
                if (event.getState().getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD) {
                    if (facing == EnumFacing.EAST) pos = event.getPos().add(-1, 0, 0);
                    if (facing == EnumFacing.WEST) pos = event.getPos().add(1, 0, 0);
                    if (facing == EnumFacing.NORTH) pos = event.getPos().add(0, 0, 1);
                    if (facing == EnumFacing.SOUTH) pos = event.getPos().add(0, 0, -1);
                } else {
                    if (facing == EnumFacing.EAST) pos = event.getPos().add(1, 0, 0);
                    if (facing == EnumFacing.WEST) pos = event.getPos().add(-1, 0, 0);
                    if (facing == EnumFacing.NORTH) pos = event.getPos().add(0, 0, -1);
                    if (facing == EnumFacing.SOUTH) pos = event.getPos().add(0, 0, 1);
                }

                playerUUID = Home.getHomeAtLoc(event.getPlayer().dimension, pos);

                if (playerUUID != null)
                    Home.DelHome("default", playerUUID);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent.RightClickBlock event) throws SQLException {
        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        UUID playerUUID = Home.getHomeAtLoc(event.getEntityPlayer().dimension, event.getPos());

        if (blockState.getBlock() == Blocks.BED) {
            if (playerUUID != null && !playerUUID.equals(event.getEntityPlayer().getUniqueID())) {
                event.getEntityPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Someone already has a home there."));
                return;
            }

            Home.SetHome("default", new Location(event.getEntityPlayer().dimension, event.getPos().getX(), event.getPos().getY(),
                    event.getPos().getZ(), event.getEntityPlayer().rotationYaw, event.getEntityPlayer().rotationPitch), event.getEntityPlayer().getUniqueID());
            event.getEntityPlayer().sendMessage(new TextComponentString("Home set!"));
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(Home.getDefaultHome(event.player.getUniqueID()) != null) {
            Location location = Home.getDefaultHome(event.player.getUniqueID());
            event.player.setPositionAndUpdate(location.getX(), location.getY(), location.getZ());
        }
    }

    public static boolean isNewPlayer(UUID playerID) {
        String worldName = FMLServerHandler.instance().getServer().getWorld(0).getWorldInfo().getWorldName();
        File playerFile = new File(System.getProperty("user.dir") + File.separator + worldName + File.separator + "playerdata", playerID.toString() + ".dat");
        return !playerFile.exists();
    }
}
