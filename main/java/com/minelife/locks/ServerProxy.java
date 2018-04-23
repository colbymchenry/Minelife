package com.minelife.locks;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    private static Set<Integer> updatedWorlds = Sets.newTreeSet();

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Lock]", Minelife.getDirectory().getAbsolutePath(), "locks");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS locks (dimension INT, x INT, y INT, z INT, type VARCHAR(36), blockName VARCHAR(100))");
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if(updatedWorlds.contains(event.world.provider.getDimension())) return;

        updatedWorlds.add(event.world.provider.getDimension());

        try {
            ResultSet result = DB.query("SELECT * FROM locks WHERE dimension='" + event.world.provider.getDimension() + "'");
            while(result.next()) {
                IBlockState blockState = event.world.getBlockState(new BlockPos(result.getInt("x"), result.getInt("y"), result.getInt("z")));
                if(!blockState.getBlock().getRegistryName().toString().equalsIgnoreCase(result.getString("blockName"))) {
                    DB.query("DELETE FROM locks WHERE dimension='" + event.world.provider.getDimension() + "' AND " +
                            "x='" + result.getInt("x") + "' AND y='" + result.getInt("y") + "' AND z='" + result.getInt("z") + "'");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
