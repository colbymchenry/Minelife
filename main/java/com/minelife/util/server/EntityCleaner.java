package com.minelife.util.server;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.logging.Level;

public class EntityCleaner {

    @SubscribeEvent
    public void OOBCatcher(EntityEvent.EnteringChunk event)
    {

        // EntityCleaner.log.logInfo("Chunk enter event detected.");

        int i = MathHelper.floor_double(event.entity.posX / 16.0D);
        int j = MathHelper.floor_double(event.entity.posZ / 16.0D);

        if (i != event.newChunkX || j != event.newChunkZ)
        {

            Minelife.getLogger().log(Level.INFO,"Errant entity detected.");

            if(event.entity instanceof EntityArrow)
            {
                if( ((EntityArrow) event.entity).shootingEntity != null)
                {
                    Minelife.getLogger().log(Level.INFO,"Parent entity: " + ((EntityArrow) event.entity).shootingEntity);
                }
                else
                {
                    Minelife.getLogger().log(Level.INFO,"Parent entity unknown.");
                }

                Minelife.getLogger().log(Level.INFO,"Attempting to use setDead() on the entity.");
                event.entity.setDead();

            } else {
                event.entity.setDead();
                event.entity.worldObj.removeEntity(event.entity);
            }
        }
    }


}
