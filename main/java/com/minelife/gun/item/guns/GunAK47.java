package com.minelife.gun.item.guns;

import buildcraft.BuildCraftCore;
import com.minelife.gun.client.guns.GunClientAK47;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.parts.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GunAK47 extends ItemGun {

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
        return 22;
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
    public ItemAmmo getAmmoType() {
        return ItemAmmo.ak47;
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @Override
    public ItemGunClient getClientHandler() {
        return ItemGunClient.ak47;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Item.getItemFromBlock(Blocks.log),
                'S', BuildCraftCore.ironGearItem,
                'F', ItemGunPart.rifleFrame,
                'T', Item.getItemFromBlock(Blocks.iron_block));
    }


}
