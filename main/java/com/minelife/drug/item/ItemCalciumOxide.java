package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.block.BlockLimestone;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCalciumOxide extends Item {

    public ItemCalciumOxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("calcium_oxide");
        setTextureName(Minelife.MOD_ID + ":calcium_oxide");
    }

}
