package com.minelife.police.client;

import com.minelife.MLProxy;
import com.minelife.police.EntityCop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        RenderingRegistry.registerEntityRenderingHandler(EntityCop.class, RenderCop::new);
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
