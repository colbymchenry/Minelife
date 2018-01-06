package com.minelife.clothing;

import com.minelife.Minelife;
import com.minelife.util.client.OSValidator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ItemDesigner extends Item {

    public ItemDesigner() {
        setUnlocalizedName("designer");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiDesigner(world));
        return super.onItemRightClick(stack, world, player);
    }


}
