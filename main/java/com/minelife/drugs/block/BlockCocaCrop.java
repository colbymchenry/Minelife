package com.minelife.drugs.block;

import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;

public class BlockCocaCrop extends BlockCrops {

    public BlockCocaCrop() {
        setRegistryName(Minelife.MOD_ID, "coca_crop");
        setUnlocalizedName(Minelife.MOD_ID + ":coca_crop");
        setTickRandomly(true);
        setCreativeTab(null);
        setHardness(0.0F);
        disableStats();
    }

    @Override
    protected Item getCrop() {
        return ModDrugs.itemCocaSeed;
    }

    @Override
    protected Item getSeed() {
        return ModDrugs.itemCocaSeed;
    }
}
