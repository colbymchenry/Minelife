package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import ic2.core.ref.ItemName;
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
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gunmetal");
        ResourceLocation group = null;
        GameRegistry.addShapedRecipe(name, group, new ItemStack(this), "CCC", "ZZZ", "TTT", 'C', new ItemStack(ItemName.plate.getInstance(), 1, 1), 'Z', ModGuns.itemZincPlate, 'T', new ItemStack(ItemName.plate.getInstance(), 1, 8));
    }

}
