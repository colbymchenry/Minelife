package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockPyrolusiteBlock extends Block {

    private static BlockPyrolusiteBlock instance;

    public static BlockPyrolusiteBlock instance() {
        if(instance == null) instance = new BlockPyrolusiteBlock();
        return instance;
    }

    private BlockPyrolusiteBlock()
    {
        super(Material.iron);
        setCreativeTab(ModDrugs.tab_drugs);
        setBlockName("pyrolusite_block");
        setBlockTextureName(Minelife.MOD_ID + ":pyrolusite_block");
    }
}
