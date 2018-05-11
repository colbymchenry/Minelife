package com.minelife.resourcefulness.quarry;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuarryListener {

    public static Map<Quarry, Long> QUARRY_QUE = Maps.newHashMap();

    public QuarryListener() throws IOException, InvalidConfigurationException {
        initQuarries();
    }

    int tick;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        tick++;
        if(tick > 100) {
            QUARRY_QUE.forEach(((quarry, resetTime) -> {
                int secondsTillReset = (int) ((resetTime - System.currentTimeMillis()) / 1000L);
                if (secondsTillReset <= 0) {
                    try {
                        List<EntityLivingBase> entities = quarry.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(quarry.getMin(), quarry.getMax()));
                        entities.forEach(entity -> entity.setPositionAndUpdate(quarry.getExit().getX(), quarry.getExit().getY(), quarry.getExit().getZ()));
                        quarry.generate();
                        QUARRY_QUE.put(quarry, System.currentTimeMillis() + quarry.getSecondsBetweenReset() * 1000L);
                    }catch(Exception e) {}
                }
            }));
        }
    }

    private void initQuarries() throws IOException, InvalidConfigurationException {
        File file = new File(Minelife.getDirectory() + "/resourcefulness/quarries");
        if(file.listFiles() != null) {
            for (File file1 : file.listFiles()) {
                QUARRY_QUE.put(new Quarry(UUID.fromString(file1.getName().replace(".yml", ""))), System.currentTimeMillis());
            }
        }
    }

}