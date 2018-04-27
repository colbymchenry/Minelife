package com.minelife.locks;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.sk89q.worldedit.util.command.binding.Text;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.text.WordUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    private static Set<Integer> updatedWorlds = Sets.newTreeSet();

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Lock]", Minelife.getDirectory().getAbsolutePath(), "locks");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS locks (dimension INT, x INT, y INT, z INT, type VARCHAR(36), blockName VARCHAR(100), placer VARCHAR(36))");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (updatedWorlds.contains(event.world.provider.getDimension())) return;

        updatedWorlds.add(event.world.provider.getDimension());

        try {
            ResultSet result = DB.query("SELECT * FROM locks WHERE dimension='" + event.world.provider.getDimension() + "'");
            while (result.next()) {
                IBlockState blockState = event.world.getBlockState(new BlockPos(result.getInt("x"), result.getInt("y"), result.getInt("z")));
                if (!blockState.getBlock().getRegistryName().toString().equalsIgnoreCase(result.getString("blockName"))) {
                    DB.query("DELETE FROM locks WHERE dimension='" + event.world.provider.getDimension() + "' AND " +
                            "x='" + result.getInt("x") + "' AND y='" + result.getInt("y") + "' AND z='" + result.getInt("z") + "'");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: Can open two block doors and probably two block chests
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        World world = event.getWorld();
        IBlockState blockState = world.getBlockState(event.getPos());
        LockType lock = ModLocks.getLock(world, event.getPos());

        if (!ModPermission.hasPermission(player.getUniqueID(), "locks.override")
                && (estate == null || !estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.OPEN_LOCKS))
                && !Objects.equals(ModLocks.getLockPlacer(world, event.getPos()), player.getUniqueID()) && lock != null) {
            if (player.getHeldItemMainhand().getItem() == ModLocks.itemLockpick) {
                ModLocks.itemLockpick.onItemUse(player, world, event.getPos(), EnumHand.MAIN_HAND, event.getFace(), 0.5F, 0.5F, 0.5F);
                event.setCanceled(true);
                if(blockState.getBlock().getRegistryName().toString().contains("_door"))
                    ModLocks.cancelDoorOpen(world, blockState, event.getPos());
            } else {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString(TextFormatting.RED + "This block is locked with an " + TextFormatting.DARK_RED + WordUtils.capitalizeFully(ModLocks.getLock(world, event.getPos()).name()) + TextFormatting.RED + " lock."));
                if(blockState.getBlock().getRegistryName().toString().contains("_door"))
                    ModLocks.cancelDoorOpen(world, blockState, event.getPos());
            }
        }

        if(player.getHeldItem(event.getHand()).getItem() == ModLocks.itemLock) {
            event.setCanceled(true);
            if(blockState.getBlock().getRegistryName().toString().contains("_door") && lock != null) {
                ModLocks.cancelDoorOpen(world, blockState, event.getPos());
                player.sendMessage(new TextComponentString(TextFormatting.RED + "There is already a lock on that block."));
            } else {
                ModLocks.itemLock.onItemUse(player, world, event.getPos(), event.getHand(), event.getFace(), 0.5f, 0.5f, 0.5f);
            }
            System.out.println(blockState.getBlock().getMetaFromState(blockState));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(BlockEvent.BreakEvent event) {
        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());

        if (!ModPermission.hasPermission(event.getPlayer().getUniqueID(), "locks.override")
                && (estate == null || (!estate.getPlayerPermissions(event.getPlayer().getUniqueID()).contains(PlayerPermission.BREAK_LOCKS)
                && !estate.getPlayerPermissions(event.getPlayer().getUniqueID()).contains(PlayerPermission.BREAK)))
                && !Objects.equals(ModLocks.getLockPlacer(event.getWorld(), event.getPos()), event.getPlayer().getUniqueID())
                && ModLocks.getLock(event.getWorld(), event.getPos()) != null) {
            event.setCanceled(true);
            event.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "This block is locked with an " + TextFormatting.DARK_RED + WordUtils.capitalizeFully(ModLocks.getLock(event.getWorld(), event.getPos()).name()) + TextFormatting.RED + " lock."));
        } else {
            if(ModLocks.getLock(event.getWorld(), event.getPos()) != null) {
                ItemStack stack = new ItemStack(ModLocks.itemLock, 1, ModLocks.getLock(event.getWorld(), event.getPos()).ordinal());
                event.getPlayer().dropItem(stack, false);
                ModLocks.deleteLock(event.getWorld(), event.getPos());
            }
        }
    }
}
