package com.minelife.drug.item;

import com.minelife.Minelife;
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
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import java.util.List;

public class ItemGrinder extends Item {

    private static ItemGrinder instance;
    private IIcon[] icons;

    private ItemGrinder() {
        setCreativeTab(CreativeTabs.tabBrewing);
        setHasSubtypes(true);
        setUnlocalizedName("grinder");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        int j = MathHelper.clamp_int(damage, 0, 15);
        return this.icons[j];
    }


    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items_list)
    {
        for (int i = 0; i < 16; ++i)
        {
            items_list.add(new ItemStack(item, 1, i));
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

    public static ItemGrinder instance() {
        if(instance == null) instance = new ItemGrinder();
        return instance;
    }

    public static void register_recipe() {
        GameRegistry.addShapedRecipe(new ItemStack(instance()), "AAA", "BBB", "AAA", 'A', Item.getItemFromBlock(Blocks.planks), 'B', Items.iron_ingot);
        for(int i = 0; i < ItemDye.field_150921_b.length; i++) {
            GameRegistry.addShapelessRecipe(new ItemStack(instance(), 1, i), instance(), new ItemStack(Items.dye, 1, i));
        }
    }

}
