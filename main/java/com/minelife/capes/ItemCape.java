package com.minelife.capes;

import com.minelife.Minelife;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
        if (pixels == null) {
            tagCompound.removeTag("pixels");
        } else {
            tagCompound.setString("pixels", pixels);
        }
        stack.stackTagCompound = tagCompound;
    }

    public String getPixels(ItemStack stack) {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("pixels") ||
                stack.stackTagCompound.getString("pixels").isEmpty()) return null;
        return stack.stackTagCompound.getString("pixels");
    }

    public void setPixels(EntityPlayerMP player, String pixels) {
        if (pixels == null) {
            player.getEntityData().removeTag("cape_pixels");
        } else {
            player.getEntityData().setString("cape_pixels", pixels);
        }
        player.writeEntityToNBT(player.getEntityData());
    }

    public String getPixels(EntityPlayerMP player) {
        return player.getEntityData().hasKey("cape_pixels") ? player.getEntityData().getString("cape_pixels") : null;
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

    public void setUUID(ItemStack stack, UUID uuid) {
        NBTTagCompound tagCompound = stack.stackTagCompound == null ? new NBTTagCompound() : stack.stackTagCompound;
        tagCompound.setString("uuid", uuid.toString());
        stack.stackTagCompound = tagCompound;
    }

    public String getUUID(ItemStack stack) {
        if (stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("uuid") ||
                stack.stackTagCompound.getString("uuid").isEmpty()) return null;
        return stack.stackTagCompound.getString("uuid");
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }
}
