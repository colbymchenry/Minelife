package com.minelife.police.client;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.emt.PacketRequestEMTStatus;
import com.minelife.police.EntityCop;
import com.minelife.police.ModPolice;
import com.minelife.police.PacketRequestCopStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class ClientProxy extends MLProxy {

    public static Set<UUID> POLICE_SET = Sets.newTreeSet();

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
        RenderingRegistry.registerEntityRenderingHandler(EntityCop.class, RenderCop::new);
        ModPolice.itemHandcuff.registerModel();
        ModPolice.itemHandcuffKey.registerModel();
    }

    @Override
    public void init(FMLInitializationEvent event) throws Exception {

    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Minelife.getNetwork().sendToServer(new PacketRequestCopStatus(player.getUniqueID()));
        }
    }

    public static void setSleeping(EntityPlayer player, boolean value) {
        Field field = ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS");
        field.setAccessible(true);
        try {
            field.set(player, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
