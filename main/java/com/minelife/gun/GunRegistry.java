package com.minelife.gun;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.gun.client.RenderGun;
import com.minelife.gun.gun.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Colby McHenry on 5/5/2017.
 */
public class GunRegistry {

    private static final Map<Class<? extends BaseGun>, BaseGun> GUNS = Maps.newHashMap();

    protected static final void registerGuns(FMLPreInitializationEvent event) {
        registerGun(GunM4A4.class, event);
        registerGun(GunBarrett.class, event);
        registerGun(GunAWP.class, event);
        registerGun(GunAK47.class, event);
        registerGun(GunDesertEagle.class, event);
        registerGun(GunMagnum.class, event);
    }

    private static final void registerGun(Class<? extends BaseGun> gunClass, FMLPreInitializationEvent event) {
        try {
            BaseGun gunInstance = gunClass.newInstance();
            GameRegistry.registerItem(gunInstance, "gun." + gunInstance.getName());
            gunInstance.registerRecipe();

            if(event.getSide() == Side.CLIENT) {
                try {
                    gunInstance.clientHandler = gunInstance.getClientHandlerClass().getDeclaredConstructor(BaseGun.class).newInstance(gunInstance);
                    MinecraftForgeClient.registerItemRenderer(gunInstance, new RenderGun(gunInstance));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            GUNS.put(gunClass, gunInstance);
            System.out.println(gunInstance.getName() + " registered!");
        } catch (Exception e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }

    public static final BaseGun get(Class gunClass) {
        return GUNS.get(gunClass);
    }


}
