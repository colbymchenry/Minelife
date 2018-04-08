package com.minelife.cape.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityTrack(PlayerEvent.StartTracking event) {
        if(!(event.getTarget() instanceof EntityPlayer)) return;
        boolean on = event.getTarget().getEntityData().hasKey("Cape") ? event.getTarget().getEntityData().getBoolean("Cape") : false;
        Minelife.getNetwork().sendTo(new PacketUpdateCapeStatus(event.getTarget().getEntityId(), on), (EntityPlayerMP) event.getEntityPlayer());
    }

    @SubscribeEvent
    public void onLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        boolean on = event.player.getEntityData().hasKey("Cape") ? event.player.getEntityData().getBoolean("Cape") : false;
        Minelife.getNetwork().sendToAll(new PacketUpdateCapeStatus(event.player.getEntityId(), on));
    }


    @SubscribeEvent
    public void deathEvent(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityCreeper) {
            if (event.getSource().getTrueSource() instanceof EntitySkeleton) {
                EntityCreeper creeper = (EntityCreeper) event.getEntityLiving();
                creeper.dropItem(ModCapes.itemCape, 1);

                ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                        new int[]{Color.RED.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.YELLOW.asRGB(), Color.ORANGE.asRGB()}).getStack(1);

                EntityFireworkRocket ent = new EntityFireworkRocket(creeper.getEntityWorld(), creeper.posX, creeper.posY + 2, creeper.posZ, fireworkStack);
                creeper.getEntityWorld().spawnEntity(ent);
                EntityFireworkRocket ent1 = new EntityFireworkRocket(creeper.getEntityWorld(), creeper.posX, creeper.posY + 2, creeper.posZ, fireworkStack);
                creeper.getEntityWorld().spawnEntity(ent1);
            }
        }
    }

}
