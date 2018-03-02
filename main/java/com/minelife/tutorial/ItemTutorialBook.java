package com.minelife.tutorial;

import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTutorialBook extends Item {

    public ItemTutorialBook() {
        setUnlocalizedName("tutorialBook");
        setTextureName(Minelife.MOD_ID + ":tutorialBook");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiTutorial());
        return super.onItemRightClick(stack, world, player);
    }
}
