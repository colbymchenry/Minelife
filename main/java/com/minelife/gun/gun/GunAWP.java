package com.minelife.gun.gun;

import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammos.AmmoAWP;
import com.minelife.gun.gun.client.GunClientAWP;

public class GunAWP extends BaseGun {
    @Override
    public String getName() {
        return "AWP";
    }

    @Override
    public int getFireRate() {
        return 1200;
    }

    @Override
    public int getDamage() {
        return 100;
    }

    @Override
    public int getReloadTime() {
        return 0;
    }

    @Override
    public int getClipSize() {
        return 5;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoAWP.class);
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientAWP.class;
    }
}
