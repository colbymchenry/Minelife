package com.minelife.gun.item.guns;

import buildcraft.BuildCraftCore;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.client.guns.GunClientAWP;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.parts.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GunAWP extends ItemGun {
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
    public ItemAmmo getAmmo() {
        return ItemAmmo.ammo556;
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ItemGunClient getClientHandler() {
        return ItemGunClient.awp;
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
