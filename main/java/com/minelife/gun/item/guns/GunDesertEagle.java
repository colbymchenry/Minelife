package com.minelife.gun.item.guns;

import buildcraft.BuildCraftCore;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.parts.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GunDesertEagle extends ItemGun {
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
    public List<ItemAmmo> getAmmo() {
        return new ArrayList<ItemAmmo>() {{
            add(ItemAmmo.ammoPistol);
            add(ItemAmmo.ammoPistolIncendiary);
        }};
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ItemGunClient getClientHandler() {
        return ItemGunClient.desertEagle;
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