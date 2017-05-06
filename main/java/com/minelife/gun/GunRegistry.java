package com.minelife.gun;

import com.google.common.collect.Maps;
import com.minelife.gun.client.RenderGun;
import com.minelife.gun.gun.GunAWP;
import com.minelife.gun.gun.GunBarrett;
import com.minelife.gun.gun.GunM4A4;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.Map;

/**
 * Created by Colby McHenry on 5/5/2017.
 */
public class GunRegistry {

    private static final Map<Class<? extends BaseGun>, BaseGun> GUNS = Maps.newHashMap();

    protected static final void registerGuns(FMLPreInitializationEvent event) {
        registerGun(GunM4A4.class, event);
        registerGun(GunBarrett.class, event);
        registerGun(GunAWP.class, event);
    }

    private static final void registerGun(Class<? extends BaseGun> gunClass, FMLPreInitializationEvent event) {
        try {
            BaseGun gunInstance = gunClass.newInstance();
            GameRegistry.registerItem(gunInstance, "gun." + gunInstance.getName());

            if(event.getSide() == Side.CLIENT) {
                try {
                    gunInstance.clientHandler = gunInstance.getClientHandlerClass().getDeclaredConstructor(BaseGun.class).newInstance(gunInstance);
                    MinecraftForgeClient.registerItemRenderer(gunInstance, new RenderGun(gunInstance));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            GUNS.put(gunClass, gunInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final BaseGun get(Class gunClass) {
        return GUNS.get(gunClass);
    }


}
