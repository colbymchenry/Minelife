package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockVacuum extends Block {

    public BlockVacuum()
    {
        super(Material.iron);
        setBlockName("vacuum");
        setBlockTextureName(Minelife.MOD_ID + ":vacuum");
        setCreativeTab(ModDrugs.tab_drugs);
    }
}
