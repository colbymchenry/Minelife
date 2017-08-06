package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.block.BlockPotash;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCalciumHydroxide extends Item {

    public ItemCalciumHydroxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("calcium_hydroxide");
        setTextureName(Minelife.MOD_ID + ":calcium_hydroxide");
    }

}
