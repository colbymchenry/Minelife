package com.minelife.emt;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.entity.EntityEMT;
import com.minelife.emt.entity.RenderEntityEMT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Set;
import java.util.UUID;

public class ClientProxy extends MLProxy {

    public static Set<UUID> EMT_SET = Sets.newTreeSet();
    public static long timeToHeal = 0;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        RenderingRegistry.registerEntityRenderingHandler(EntityEMT.class, RenderEntityEMT::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Minelife.getNetwork().sendToServer(new PacketRequestEMTStatus(player.getUniqueID()));
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Pre event) {

    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if ((System.currentTimeMillis() - timeToHeal) / 1000L <= 0 && timeToHeal != 0) {
            timeToHeal = 0;
        }
    }

}
