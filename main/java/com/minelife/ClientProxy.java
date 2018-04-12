package com.minelife;

import codechicken.lib.render.CCRenderEventHandler;
import com.google.common.collect.Sets;
import com.minelife.util.client.render.AdjustPlayerModelEvent;
import com.minelife.util.client.render.RenderPlayerCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        OBJLoader.INSTANCE.addDomain(Minelife.MOD_ID);

        Minelife.getModList().forEach(mod -> {
            try {
                mod.clientProxy = mod.getClientProxyClass().newInstance();
                mod.clientProxy.preInit(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException ignored) {
                ignored.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        CCRenderEventHandler.init();

        Minelife.getModList().forEach(mod -> {
            try {
                mod.clientProxy.init(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static Set<UUID> rendering = Sets.newTreeSet();
    private static RenderPlayerCustom renderPlayerCustom;

    @SubscribeEvent
    public void preRender(RenderPlayerEvent.Pre event) {
        if (renderPlayerCustom == null) {
            renderPlayerCustom = new RenderPlayerCustom(Minecraft.getMinecraft().getRenderManager());
//            try {
//                Field field = RenderManager.class.getDeclaredField("skinMap");
//                field.setAccessible(true);
//                Map<String, RenderPlayer> refMap = (Map<String, RenderPlayer>) field.get(Minecraft.getMinecraft().getRenderManager());
//                refMap.put("default", renderPlayerCustom);
//                refMap.put("slim", renderPlayerCustom);
//
//                refMap.values().forEach(key -> System.out.println(key));
//
//                field = RenderManager.class.getDeclaredField("playerRenderer");
//                field.setAccessible(true);
//                field.set(Minecraft.getMinecraft().getRenderManager(), renderPlayerCustom);
//                System.out.println(field.get(Minecraft.getMinecraft().getRenderManager()));
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        }

        if (!rendering.contains(event.getEntityPlayer().getUniqueID())) {
            rendering.add(event.getEntityPlayer().getUniqueID());
            event.setCanceled(true);
            float entityYaw = event.getEntity().rotationYaw + (event.getEntity().prevRotationYaw - event.getEntity().rotationYaw) * event.getPartialRenderTick();
            renderPlayerCustom.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(), event.getY(), event.getZ(), entityYaw, event.getPartialRenderTick());
        }
    }


    @SubscribeEvent
    public void postRender(RenderPlayerEvent.Post event) {
        rendering.clear();
    }

}
