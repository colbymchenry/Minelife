package com.minelife.gun.gun;

import buildcraft.BuildCraftCore;
import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoDesertEagle;
import com.minelife.gun.gun.client.GunClientDesertEagle;
import com.minelife.gun.item.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GunDesertEagle extends BaseGun {
    @Override
    public String getName() {
        return "DesertEagle";
    }

    @Override
    public int getFireRate() {
        return 50;
    }

    @Override
    public int getDamage() {
        return 20;
    }

    @Override
    public int getReloadTime() {
        return 21;
    }

    @Override
    public int getClipSize() {
        return 10;
    }

    @Override
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoDesertEagle.class);
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientDesertEagle.class;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Items.iron_ingot,
                'S', BuildCraftCore.ironGearItem,
                'F', ItemGunPart.pistolFrame,
                'T', Items.iron_ingot);
    }


}
