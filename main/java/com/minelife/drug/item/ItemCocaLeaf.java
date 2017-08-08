package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemCocaLeaf extends Item {

    public ItemCocaLeaf()
    {
        setCreativeTab(ModDrugs.tab_drugs);
        setTextureName(Minelife.MOD_ID + ":coca_leaf");
        setUnlocalizedName("coca_leaf");
        setMaxDamage(100);
    }

    public void set_moisture_level(ItemStack stack, int moisture) {
        if(stack == null) return;
        if(stack.getItem() != this) return;
        moisture = moisture < 0 ? 0 : moisture > 100 ? 100 : moisture;
        stack.setItemDamage(100 - moisture);
    }

    public int get_moisture_level(ItemStack stack) {
        if(stack == null) return 0;
        if(stack.getItem() != this) return 0;

        return 100 - stack.getItemDamage();
    }

}
