package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.block.BlockPotash;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPotassiumHydroxide extends Item {

    public ItemPotassiumHydroxide() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("potassium_hydroxide");
        setTextureName(Minelife.MOD_ID + ":potassium_hydroxide");

    }

}
