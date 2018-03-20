package com.minelife.util.fireworks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class FireworkBuilder {

    private NBTTagList Explosions = new NBTTagList();

    public static FireworkBuilder builder() { return new FireworkBuilder(); }

    public FireworkBuilder addExplosion(boolean Flicker, boolean Trail, Type Type, int[] Colors, int[] FadeColors) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Flicker", Flicker);
        tag.setBoolean("Trail", Trail);
        tag.setInteger("Type", Type.id);
        tag.setIntArray("Colors", Colors);
        tag.setIntArray("FadeColors", FadeColors);
        Explosions.appendTag(tag);
        return this;
    }

    public ItemStack getStack(int Flight) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORKS);
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagCompound Fireworks = new NBTTagCompound();
        Fireworks.setTag("Explosions", Explosions);
        tagCompound.setInteger("Flight", Flight);
        tagCompound.setTag("Fireworks", Fireworks);
        fireworkStack.setTagCompound(tagCompound);
        return fireworkStack;
    }

    public enum Type {

        SMALL_BALL(0),LARGE_BALL(1), STAR(2), CREEPER(3), BURST(4);
        int id;
        Type(int id) {
            this.id = id;
        }

    }

}
