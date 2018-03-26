package com.minelife.guns.item;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

public class ItemGun extends Item {

    public ItemGun() {
        setRegistryName("gun");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":gun");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != CreativeTabs.MISC) return;
        for (EnumGunType gunType : EnumGunType.values()) {
            items.add(new ItemStack(this, 1, gunType.ordinal()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WordUtils.capitalizeFully(EnumGunType.values()[stack.getMetadata()].name().replace("_", " "));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
