package com.minelife.capes;

import com.minelife.MLItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CapeRecipe extends ShapedOreRecipe {

    // TODO: Broken need to fix
    public CapeRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        final ItemStack output = super.getCraftingResult(inv); // Get the default output

        if (output != null) {
            for (int i = 0; i < inv.getSizeInventory(); i++) { // For each slot in the crafting inventory,
                final ItemStack ingredient = inv.getStackInSlot(i); // Get the ingredient in the slot

                if (ingredient != null && ingredient.getItem() == MLItems.cape) {
                    if (MLItems.cape.getPixels(ingredient) != null) {
                        MLItems.cape.setPixels(output, MLItems.cape.getPixels(ingredient));
                    }
                    break; // Break now
                }
            }
        }


        return output;
    }



}
