package com.minelife.capes;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemCape extends Item {

    public ItemCape() {
        setUnlocalizedName("cape");
        setCreativeTab(CreativeTabs.tabDecorations);
        setMaxStackSize(1);
        setTextureName(Minelife.MOD_ID + ":cape");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return super.onItemRightClick(stack, world, player);
    }

    public void setPixels(ItemStack stack, String pixels) {
        NBTTagCompound tagCompound = stack.stackTagCompound == null ? new NBTTagCompound() : stack.stackTagCompound;
        tagCompound.setString("pixels", pixels);
        stack.stackTagCompound = tagCompound;
    }

    public String getPixels(ItemStack stack) {
        if(stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("pixels") ||
                stack.stackTagCompound.getString("pixels").isEmpty()) return null;
        return stack.stackTagCompound.getString("pixels");
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        setUUID(stack);
    }

    public void setUUID(ItemStack stack) {
        NBTTagCompound tagCompound = stack.stackTagCompound == null ? new NBTTagCompound() : stack.stackTagCompound;
        tagCompound.setString("uuid", UUID.randomUUID().toString());
        stack.stackTagCompound = tagCompound;
    }

    public String getUUID(ItemStack stack) {
        if(stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("uuid") ||
                stack.stackTagCompound.getString("uuid").isEmpty()) return null;
        return stack.stackTagCompound.getString("uuid");
    }
}
