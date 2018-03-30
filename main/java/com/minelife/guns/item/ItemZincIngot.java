package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemZincIngot extends Item {

    public ItemZincIngot() {
        setRegistryName(Minelife.MOD_ID, "zinc_ingot");
        setUnlocalizedName(Minelife.MOD_ID + ":zinc_ingot");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel(ItemModelMesher mesher) {
        Item item = this;
        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":zinc_ingot", "inventory");
        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, 0, model);
    }

    public void registerSmeltingRecipe() {
        GameRegistry.addSmelting(ModGuns.itemZincOre, new ItemStack(ModGuns.itemZincIngot), 4);
    }

}
