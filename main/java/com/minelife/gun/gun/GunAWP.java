package com.minelife.gun.gun;

import buildcraft.BuildCraftCore;
import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoAWP;
import com.minelife.gun.gun.client.GunClientAWP;
import com.minelife.gun.item.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public boolean isFullAuto() {
        return false;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientAWP.class;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Item.getItemFromBlock(Blocks.gold_block),
                'S', BuildCraftCore.goldGearItem,
                'F', ItemGunPart.sniperFrame,
                'T', Item.getItemFromBlock(Blocks.iron_block));
    }


}
