package com.minelife.resourcefulness.forest;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ForestListener {

    public static Map<Forest, Long> FOREST_QUE = Maps.newHashMap();

    public ForestListener() throws IOException, InvalidConfigurationException {
        initForests();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        FOREST_QUE.forEach(((forest, resetTime) -> {
            int secondsTillReset = (int) ((resetTime - System.currentTimeMillis()) / 1000L);
            if (secondsTillReset <= 0) {
                List<EntityLivingBase> entities = forest.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(forest.getMin(), forest.getMax()));
                entities.forEach(entity -> entity.setPositionAndUpdate(forest.getExit().getX(), forest.getExit().getY(), forest.getExit().getZ()));
                try {
                    forest.generate();
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                }
                FOREST_QUE.put(forest, System.currentTimeMillis() + forest.getSecondsBetweenReset() * 1000L);
            }
        }));
    }

    private void initForests() throws IOException, InvalidConfigurationException {
        File file = new File(Minelife.getDirectory() + "/resourcefulness/forests");
        for (File file1 : file.listFiles()) {
            FOREST_QUE.put(new Forest(UUID.fromString(file1.getName().replace(".yml", ""))), System.currentTimeMillis());
        }
    }

}
