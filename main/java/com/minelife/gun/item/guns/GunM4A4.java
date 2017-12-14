package com.minelife.gun.item.guns;

import buildcraft.BuildCraftCore;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.parts.ItemGunPart;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GunM4A4 extends ItemGun {

    @Override
    public String getName() {
        return "M4A4";
    }

    @Override
    public int getFireRate() {
        return 20;
    }

    @Override
    public int getDamage() {
        return 33;
    }

    @Override
    public int getReloadTime() {
        return 1300;
    }

    @Override
    public int getClipSize() {
        return 30;
    }

    @Override
    public List<ItemAmmo> getAmmo() {
        return new ArrayList<ItemAmmo>() {{
            add(MLItems.ammo_556);
            add(MLItems.ammo_556_explosive);
            add(MLItems.ammo_556_incendiary);
        }};
    }

    @Override
    public boolean isFullAuto() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public ItemGunClient getClientHandler() {
        return ItemGunClient.m4A4;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Item.getItemFromBlock(Blocks.iron_block),
                'S', BuildCraftCore.ironGearItem,
                'F', MLItems.rifle_frame,
                'T', Item.getItemFromBlock(Blocks.iron_block));
    }

}
