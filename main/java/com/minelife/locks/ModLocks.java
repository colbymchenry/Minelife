package com.minelife.locks;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.tileentity.TileEntityChest;
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
            } else {
                if (world.getBlockState(pos).getBlock() == Blocks.CHEST) {
                    TileEntityChest chestTile = (TileEntityChest) world.getTileEntity(pos);
                    if (chestTile.adjacentChestZNeg != null || chestTile.adjacentChestZPos != null ||
                            chestTile.adjacentChestXNeg != null || chestTile.adjacentChestXPos != null) {

                        if (chestTile.adjacentChestZPos != null) pos = pos.add(0, 0, 1);
                        if (chestTile.adjacentChestXPos != null) pos = pos.add(1, 0, 0);
                        if (chestTile.adjacentChestZNeg != null) pos = pos.add(0, 0, -1);
                        if (chestTile.adjacentChestXNeg != null) pos = pos.add(-1, 0, 0);

                        result = getDatabase().query("SELECT * FROM locks WHERE dimension='" + world.provider.getDimension() + "' " +
                                "AND x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");
                        if (result.next()) {
                            return LockType.valueOf(result.getString("type"));
                        }
                    }
                } else if (world.getBlockState(pos).getBlock().getRegistryName().toString().contains("_door")) {
                    boolean topDoor = (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) & 8) > 0;
                    if(topDoor) pos = pos.add(0, -1, 0);
                    else pos = pos.add(0, 1, 0);

                    result = getDatabase().query("SELECT * FROM locks WHERE dimension='" + world.provider.getDimension() + "' " +
                            "AND x='" + pos.getX() + "' AND y='" + pos.getY() + "' AND z='" + pos.getZ() + "'");

                    if (result.next()) {
                        return LockType.valueOf(result.getString("type"));
                    }
                }
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

    public static void cancelDoorOpen(World world, IBlockState blockState, BlockPos pos) {
        boolean topDoor = (world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)) & 8) > 0;
        if(topDoor) blockState = world.getBlockState(pos.add(0, -1, 0));
        world.setBlockState(topDoor ? pos.add(0, -1, 0) : pos, Blocks.AIR.getDefaultState());
        world.setBlockState(topDoor ? pos : pos.add(0, 1, 0), Blocks.AIR.getDefaultState());
        ItemDoor.placeDoor(world, topDoor ? pos.add(0, -1, 0) : pos, blockState.getValue(BlockDoor.FACING), blockState.getBlock(), blockState.getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT);
    }
}
