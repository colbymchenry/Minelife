package com.minelife.drug.block;

import com.minelife.drug.ModDrugs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockRoller extends Block {

    //TODO
    public BlockRoller()
    {
        super(Material.anvil);
        setCreativeTab(ModDrugs.tab_drugs);
        setBlockName("roller");
    }
}
