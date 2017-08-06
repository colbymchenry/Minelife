package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCementMixer extends Block {

    public BlockCementMixer()
    {
        super(Material.iron);
        setBlockName("cement_mixer");
        setBlockTextureName(Minelife.MOD_ID + ":cement_mixer");
        setCreativeTab(ModDrugs.tab_drugs);
    }
}
