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

public class ItemHolographicSite extends Item {

    public static int[] red = new int[]{255, 0, 0, 255};

    public ItemHolographicSite() {
        setUnlocalizedName("holographic_site");
        setCreativeTab(ModGun.tabGuns);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int[] color = ItemHolographicSite.getHolographicColor(stack);
        Minecraft.getMinecraft().displayGuiScreen(new GuiModifyColor(color[0], color[1], color[2]));
        return super.onItemRightClick(stack, world, player);
    }

    public static boolean hasHolographic(ItemStack stack) {
        return stack.getItem() instanceof ItemGun && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("holographic");
    }

    public static int[] getHolographicColor(ItemStack stack) {
        return stack.getItem() instanceof ItemGun && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("holographic_color") ? stack.stackTagCompound.getIntArray("holographic_color") : red;
    }

    public static void setHolographicColor(ItemStack stack, int[] color) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        tagCompound.setIntArray("holographic_color", color);
        stack.stackTagCompound = tagCompound;
    }

}
