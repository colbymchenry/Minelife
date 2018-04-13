package com.minelife.drugs.block;

import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;

public class BlockLimeCrop extends BlockCrops {

    public BlockLimeCrop() {
        setRegistryName(Minelife.MOD_ID, "lime_crop");
        setUnlocalizedName(Minelife.MOD_ID + ":lime_crop");
        setTickRandomly(true);
        setCreativeTab(null);
        setHardness(0.0F);
        disableStats();
    }

    @Override
    protected Item getCrop() {
        return ModDrugs.itemLime;
    }

    @Override
    protected Item getSeed() {
        return ModDrugs.itemLimeSeed;
    }
}
