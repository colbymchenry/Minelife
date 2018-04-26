package com.minelife.locks;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ModLocks extends MLMod {

    public static ItemLock itemLock;
    public static ItemLockpick itemLockpick;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItem(itemLock = new ItemLock());
        registerItem(itemLockpick = new ItemLockpick());
        itemLockpick.registerRecipe();
        itemLock.registerRecipes();
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return ClientProxy.class;
    }

    public static LockType getLock(World world, BlockPos pos) {
        try {
            ResultSet result = getDatabase().query("SELECT * FROM locks WHERE dimension='" + world.provider.getDimension() + "' " +
                    "AND x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");
            if (result.next()) {
                return LockType.valueOf(result.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UUID getLockPlacer(World world, BlockPos pos) {
        try {
            ResultSet result = getDatabase().query("SELECT * FROM locks WHERE dimension='" + world.provider.getDimension() + "' " +
                    "AND x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");
            if (result.next()) {
                return UUID.fromString(result.getString("placer"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean addLock(World world, BlockPos pos, LockType type, UUID player) {
        if (getLock(world, pos) == null) {
            try {
                getDatabase().query("INSERT INTO locks (dimension, x, y, z, type, blockName, placer) VALUES ('" + world.provider.getDimension() + "', " +
                        "'" + pos.getX() + "', '" + pos.getY() + "', '" + pos.getZ() + "', '" + type.name() + "', '" + world.getBlockState(pos).getBlock().getRegistryName() + "', '" + player.toString() + "')");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    public static void deleteLock(World world, BlockPos pos) {
        try {
            getDatabase().query("DELETE FROM locks WHERE dimension='" + world.provider.getDimension() + "' AND " +
                    "x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
