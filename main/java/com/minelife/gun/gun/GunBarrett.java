package com.minelife.gun.gun;

import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoBarrett;
import com.minelife.gun.gun.client.GunClientBarrett;

public class GunBarrett extends BaseGun {

    @Override
    public String getName() {
        return "Barrett";
    }

    @Override
    public int getFireRate() {
        return 400;
    }

    @Override
    public int getDamage() {
        return 100;
    }

    @Override
    public int getReloadTime() {
        return 20;
    }

    @Override
    public int getClipSize() {
        return 5;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoBarrett.class);
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientBarrett.class;
    }

    @Override
    public void registerRecipe() {

    }


}
