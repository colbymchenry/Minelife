package com.minelife.gun.gun;

import buildcraft.BuildCraftCore;
import com.minelife.gun.AmmoRegistry;
import com.minelife.gun.BaseAmmo;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.ammo.AmmoBarrett;
import com.minelife.gun.gun.client.GunClientBarrett;
import com.minelife.gun.item.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Item.getItemFromBlock(Blocks.diamond_block),
                'S', BuildCraftCore.diamondGearItem,
                'F', ItemGunPart.sniperFrame,
                'T', Item.getItemFromBlock(Blocks.iron_block));
    }


}
