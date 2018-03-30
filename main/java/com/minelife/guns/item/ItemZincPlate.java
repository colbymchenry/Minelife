package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import ic2.core.ref.ItemName;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemZincPlate extends Item {

    public ItemZincPlate() {
        setRegistryName(Minelife.MOD_ID, "zinc_plate");
        setUnlocalizedName(Minelife.MOD_ID + ":zinc_plate");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel(ItemModelMesher mesher) {
        Item item = this;
        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":zinc_plate", "inventory");
        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, 0, model);
    }

    public void registerRecipe() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":zinc_plate");
        ResourceLocation group = null;
        GameRegistry.addShapelessRecipe(name, group, new ItemStack(this), Ingredient.fromItem(ItemName.forge_hammer.getInstance()), Ingredient.fromItem(ModGuns.itemZincIngot));
    }

}
