package com.minelife.gun.item.attachments;

import com.minelife.gun.ModGun;
import com.minelife.gun.client.gui.GuiModifyColor;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemSite extends Item {

    public static int[] red = new int[]{255, 0, 0, 255};

    public ItemSite(String name) {
        setUnlocalizedName(name);
        setCreativeTab(ModGun.tabGuns);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int[] color = getSiteColor(stack);
        Minecraft.getMinecraft().displayGuiScreen(new GuiModifyColor(color[0], color[1], color[2]));
        return super.onItemRightClick(stack, world, player);
    }

    public static int[] getSiteColor(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemSite && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("color") ? stack.stackTagCompound.getIntArray("color") : red;
    }

    public static void setSiteColor(ItemStack stack, int[] color) {
        NBTTagCompound tagCompound = stack.stackTagCompound != null ? stack.stackTagCompound : new NBTTagCompound();
        tagCompound.setIntArray("color", color);
        stack.stackTagCompound = tagCompound;
    }

}
