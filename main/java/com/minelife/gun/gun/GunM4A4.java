package com.minelife.gun.gun;

import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoM4A4;
import com.minelife.gun.gun.client.GunClientM4A4;

public class GunM4A4 extends BaseGun {

    @Override
    public String getName() {
        return "M4A4";
    }

    @Override
    public int getFireRate() {
        return 100;
    }

    @Override
    public int getDamage() {
        return 33;
    }

    @Override
    public int getReloadTime() {
        return 5000;
    }

    @Override
    public int getClipSize() {
        return 30;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoM4A4.class);
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientM4A4.class;
    }

    @Override
    public void registerRecipe() {

    }

}
