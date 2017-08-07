package com.minelife.drug;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityChest;

public class Recipe {

    private ItemStack output;
    private ItemStack[] input;

    public Recipe(ItemStack output, ItemStack... input) {
        this.output = output;
        this.input = input;
    }

    public ItemStack output() {
        return output;
    }

    public ItemStack[] input() {
        return input;
    }

    public boolean matches(ItemStack... items) {
        if(input.length == items.length) {
            for (ItemStack itemStack : input) {
                boolean foundItem = false;
                for(ItemStack itemStack1 : items) {
                    if(itemStack != null && itemStack1 != null && itemStack.isItemEqual(itemStack1)) {
                        foundItem = true;
                    }
                }

                if(!foundItem) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public void writeToNBT(NBTTagCompound tag) {
        NBTTagCompound tagOutput = new NBTTagCompound();
        NBTTagList inputs = new NBTTagList();
        output.writeToNBT(tagOutput);
        for (ItemStack itemStack : input) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            itemStack.writeToNBT(tagCompound);
            inputs.appendTag(tagCompound);
        }

        tag.setTag("output", tagOutput);
        tag.setTag("input", inputs);
    }

    public static Recipe readFromNBT(NBTTagCompound tag) {
        ItemStack output = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("output"));
        NBTTagList tagList = tag.getTagList("input", 10);
        ItemStack[] input = new ItemStack[tagList.tagCount()];
        for(int i = 0; i < tagList.tagCount(); i++) {
            input[i] = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
        }

        return new Recipe(output, input);
    }

}
