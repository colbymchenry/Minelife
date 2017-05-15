package com.minelife.gun;

import com.google.common.collect.Maps;
import com.minelife.gun.ammo.*;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.Map;

/**
 * Created by Colby McHenry on 5/5/2017.
 */
public class AmmoRegistry {

    private static final Map<Class<? extends BaseAmmo>, BaseAmmo> AMMOS = Maps.newHashMap();

    protected static final void registerAmmos() {
        registerAmmo(AmmoM4A4.class);
        registerAmmo(AmmoBarrett.class);
        registerAmmo(AmmoAWP.class);
        registerAmmo(AmmoAK47.class);
        registerAmmo(AmmoDesertEagle.class);
        registerAmmo(AmmoMagnum.class);
    }

    private static final void registerAmmo(Class<? extends BaseAmmo> ammoClass) {
        try {
            BaseAmmo ammoInstance = ammoClass.newInstance();
            GameRegistry.registerItem(ammoInstance, "ammo." + ammoInstance.getName());
            AMMOS.put(ammoClass, ammoInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final BaseAmmo get(Class ammoClass) {
        return AMMOS.get(ammoClass);
    }


}
