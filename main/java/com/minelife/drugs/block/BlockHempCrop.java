package com.minelife.drugs.block;

import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;

public class BlockHempCrop extends BlockCrops {

    public BlockHempCrop() {
        setRegistryName(Minelife.MOD_ID, "hemp_crop");
        setUnlocalizedName(Minelife.MOD_ID + ":hemp_crop");
        setTickRandomly(true);
        setCreativeTab(null);
        setHardness(0.0F);
        disableStats();
    }

    // TODO: Still grows in non farmland

    @Override
    protected Item getCrop() {
        return ModDrugs.itemHempBuds;
    }

    @Override
    protected Item getSeed() {
        return ModDrugs.itemHempSeed;
    }
}
