package com.minelife.guns.item;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGunmetal extends Item {

    public ItemGunmetal() {
        setRegistryName(Minelife.MOD_ID, "gunmetal");
        setUnlocalizedName(Minelife.MOD_ID + ":gunmetal");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel(ItemModelMesher mesher) {
        Item item = this;
        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":gunmetal", "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, model);
        mesher.register(item, 0, model);
    }

    public void registerRecipe() {
        ItemStack aluminum = new ItemStack(IEContent.itemMetal, 1, 1);
        ItemStack copper = new ItemStack(IEContent.itemMetal, 1, 0);
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gunmetal");
        ResourceLocation group = null;
        GameRegistry.addShapedRecipe(name, group, new ItemStack(this), "CCC", "ZZZ", "TTT", 'C', copper, 'Z', ModGuns.itemZincPlate, 'T', aluminum);
    }

}
