package com.minelife.gun.block;

import com.minelife.Minelife;
import com.minelife.gun.ModGun;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockZincOre extends Block {

    public BlockZincOre() {
        super(Material.iron);
        setBlockName("zinc_ore");
        setBlockTextureName(Minelife.MOD_ID + ":zinc_ore");
        setHardness(3);
        setResistance(15);
//        tool: "pickaxe", "axe", "shovel"
//        level: 0=wood; 1=stone; 2=iron; 3=diamond tool
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(ModGun.tabGuns);
    }

}
