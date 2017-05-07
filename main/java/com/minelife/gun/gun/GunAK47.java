package com.minelife.gun.gun;

import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammos.AmmoAK47;
import com.minelife.gun.gun.client.GunClientAK47;

public class GunAK47 extends BaseGun {

    @Override
    public String getName() {
        return "AK47";
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
        return 2500;
    }

    @Override
    public int getClipSize() {
        return 30;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoAK47.class);
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientAK47.class;
    }
}
