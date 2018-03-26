package com.minelife;

import com.minelife.util.client.render.RenderPlayerCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientProxy extends MLProxy {

    RenderPlayerCustom renderPlayerCustom;
    float partialTicks;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

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

    @SubscribeEvent
    public void pre(RenderPlayerEvent.Pre event) {
        if(renderPlayerCustom == null) {
            renderPlayerCustom = new RenderPlayerCustom(Minecraft.getMinecraft().getRenderManager());
        }
        event.setCanceled(true);
        float entityYaw = event.getEntity().rotationYaw + (event.getEntity().prevRotationYaw - event.getEntity().rotationYaw) * partialTicks;
        renderPlayerCustom.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(), event.getY(), event.getZ(), entityYaw, event.getPartialRenderTick());
    }

}
