package com.minelife;

import codechicken.lib.render.CCRenderEventHandler;
import com.minelife.util.client.render.AdjustPlayerModelEvent;
import com.minelife.util.client.render.RenderPlayerCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy extends MLProxy {

    RenderPlayerCustom renderPlayerCustom;
    float partialTicks;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
//        RenderingRegistry.registerEntityRenderingHandler(AbstractClientPlayer.class, manager -> new RenderPlayerCustom(manager, false));

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

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        partialTicks = event.renderTickTime;
    }

    // TODO: Try to get renderer to work for player arms correctly
    @SubscribeEvent
    public void pre(RenderPlayerEvent.Pre event) {
        if(renderPlayerCustom == null) {
            renderPlayerCustom = new RenderPlayerCustom(Minecraft.getMinecraft().getRenderManager(), false);
        }
        event.setCanceled(true);
        float entityYaw = event.getEntity().rotationYaw + (event.getEntity().prevRotationYaw - event.getEntity().rotationYaw) * partialTicks;
        renderPlayerCustom.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(), event.getY(), event.getZ(), entityYaw, event.getPartialRenderTick());
    }

}
