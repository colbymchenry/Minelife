package com.minelife.gun.gun;

import buildcraft.BuildCraftCore;
import com.minelife.gun.*;
import com.minelife.gun.ammo.AmmoAK47;
import com.minelife.gun.gun.client.GunClientAK47;
import com.minelife.gun.item.*;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import ic2.core.Ic2Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public BaseAmmo getAmmoType() {
        return AmmoRegistry.get(AmmoAK47.class);
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @Override
    protected Class<? extends BaseGunClient> getClientHandlerClass() {
        return GunClientAK47.class;
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
