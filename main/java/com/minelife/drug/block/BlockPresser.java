package com.minelife.drug.block;

import com.minelife.drug.ModDrugs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockPresser extends Block {

    // TODO
    public BlockPresser() {
        super(Material.iron);
        setCreativeTab(ModDrugs.tab_drugs);
        setBlockName("presser");
    }

}
