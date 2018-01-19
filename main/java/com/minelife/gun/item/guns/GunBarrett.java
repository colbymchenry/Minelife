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

public class GunBarrett extends ItemGun {

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
        return 20;
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
    public List<ItemAmmo> getAmmo() {
        return new ArrayList<ItemAmmo>() {{
            add(MLItems.ammo_556);
            add(MLItems.ammo_556_explosive);
            add(MLItems.ammo_556_incendiary);
        }};
    }

    @Override
    public boolean isFullAuto() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ItemGunClient getClientHandler() {
        return ItemGunClient.barrett;
    }

    @Override
    public void registerRecipe() {
        GameRegistry.addRecipe(new ItemStack(this),
                "LLL",
                "SFS",
                "TTT",
                'L', Item.getItemFromBlock(Blocks.diamond_block),
                'S', BuildCraftCore.diamondGearItem,
                'F', MLItems.sniper_frame,
                'T', Item.getItemFromBlock(Blocks.iron_block));
    }


}
