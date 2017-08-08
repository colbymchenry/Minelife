package com.minelife.drug;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe {

    private ItemStack output;
    private ItemStack[] inputs;
    private FluidStack[] fluids;

    private Recipe(ItemStack output, ItemStack... inputs)
    {
        this.output = output;
        this.inputs = inputs;
    }

    public static Recipe build(ItemStack output, ItemStack... inputs)
    {
        return new Recipe(output, inputs);
    }

    public Recipe addLiquids(FluidStack... fluids)
    {
        this.fluids = fluids;
        return this;
    }

    public ItemStack output()
    {
        return output.copy();
    }

    public ItemStack[] inputs()
    {
        return inputs;
    }

    public FluidStack[] fluids()
    {
        return fluids;
    }

    public ItemStack process(FluidStack[] fluids, ItemStack[] items)
    {
        if (matches(fluids, items)) {
            for (ItemStack itemStack : inputs) {
                for (ItemStack itemStack1 : items) {
                    if (itemStack != null && itemStack1 != null && itemStack.isItemEqual(itemStack1)) {
                        itemStack1.stackSize -= itemStack.stackSize;
                        break;
                    }
                }

            }

            for (FluidStack fluidStack : this.fluids) {
                for (FluidStack fluidStack1 : fluids) {
                    if (fluidStack1.containsFluid(fluidStack)) {
                        fluidStack1.amount -= fluidStack.amount;
                        break;
                    }
                }
            }
            return output();
        }

        return null;
    }

    public boolean matches(FluidStack[] fluids, ItemStack[] items)
    {
        for (ItemStack itemStack : inputs) {
            boolean foundItem = false;
            for (ItemStack itemStack1 : items) {
                if (itemStack != null && itemStack1 != null && itemStack.isItemEqual(itemStack1)) {
                    foundItem = true;
                }
            }

            if (!foundItem) {
                return false;
            }
        }

        for (FluidStack fluidStack : this.fluids) {
            boolean foundFluid = false;
            for (FluidStack fluidStack1 : fluids) {
                if (fluidStack1.containsFluid(fluidStack)) {
                    foundFluid = true;
                }
            }

            if (!foundFluid) {
                return false;
            }
        }

        return true;
    }

}
