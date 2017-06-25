package com.minelife.gun.gun;

import buildcraft.BuildCraftCore;
import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoMagnum;
import com.minelife.gun.gun.client.GunClientMagnum;
import com.minelife.gun.item.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GunMagnum extends BaseGun{
    @Override
    public String getName() {
        return "Magnum";
    }

    @Override
    public int getFireRate() {
        return 100;
    }

    @Override
    public int getDamage() {
        return 20;
    }

    @Override
    public int getReloadTime() {
        return 20;
    }

    @Override
    public int getClipSize() {
        return 6;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoMagnum.class);
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientMagnum.class;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "SFS",
                "TTT",
                'S', BuildCraftCore.ironGearItem,
                'F', ItemGunPart.pistolFrame,
                'T', Items.iron_ingot);
    }

}
