package com.minelife.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class FireworkBuilder {

    private NBTTagList Explosions = new NBTTagList();

    public static FireworkBuilder builder() { return new FireworkBuilder(); }

    public FireworkBuilder addExplosion(boolean Flicker, boolean Trail, int Type, int[] Colors, int[] FadeColors) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Flicker", Flicker);
        tag.setBoolean("Trail", Trail);
        tag.setInteger("Type", Type);
        tag.setIntArray("Colors", Colors);
        tag.setIntArray("FadeColors", FadeColors);
        Explosions.appendTag(tag);
        return this;
    }

    public ItemStack getStack(int Flight) {
        ItemStack fireworkStack = new ItemStack(Items.fireworks);
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagCompound Fireworks = new NBTTagCompound();
        Fireworks.setTag("Explosions", Explosions);
        tagCompound.setInteger("Flight", Flight);
        tagCompound.setTag("Fireworks", Fireworks);
        fireworkStack.stackTagCompound = tagCompound;
        return fireworkStack;
    }

}
