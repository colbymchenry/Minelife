package com.minelife.gun.item.guns;

import buildcraft.BuildCraftCore;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.client.guns.GunClientMagnum;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.parts.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GunMagnum extends ItemGun {
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
    public ItemAmmo getAmmoType() {
        return ItemAmmo.magnum;
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @Override
    public ItemGunClient getClientHandler() {
        return ItemGunClient.magnum;
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
