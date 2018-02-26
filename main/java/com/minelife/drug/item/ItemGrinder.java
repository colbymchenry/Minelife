package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import java.util.List;

public class ItemGrinder extends Item {

    public static IIcon[] icons;

    public ItemGrinder() {
        setCreativeTab(ModDrugs.tab_drugs);
        setHasSubtypes(true);
        setUnlocalizedName("grinder");
        setMaxDamage(100);
        setNoRepair();
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items_list)
    {
        for (int i = 0; i < 16; ++i)
        {
            ItemStack grinder = new ItemStack(item);
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger("color_index", i);
            grinder.setTagCompound(tagCompound);
            items_list.add(grinder);
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister icon_register)
    {
        this.icons = new IIcon[ItemDye.field_150921_b.length];

        for (int i = 0; i < ItemDye.field_150921_b.length; ++i)
        {
            this.icons[i] = icon_register.registerIcon(Minelife.MOD_ID + ":grinder_" + ItemDye.field_150921_b[i]);
        }
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        int dmg = stack.getItemDamage();
        if (dmg == getMaxDamage()) {
            return null;
        }
        ItemStack tr = ItemStack.copyItemStack(stack);
        tr.setItemDamage(dmg + 1);
        return tr;
    }

}
