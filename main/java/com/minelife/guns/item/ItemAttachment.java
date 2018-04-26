package com.minelife.guns.item;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.text.WordUtils;

public class ItemAttachment extends Item {

    public ItemAttachment() {
        setRegistryName(Minelife.MOD_ID, "gunAttachment");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":gunAttachment");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != CreativeTabs.MISC) return;
        for (EnumAttachment attachment : EnumAttachment.values()) {
            items.add(new ItemStack(this, 1, attachment.ordinal()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WordUtils.capitalizeFully(EnumAttachment.values()[stack.getMetadata()].name().replace("_", " "));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    public void registerRecipes() {
//        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gun_attachment_" + EnumAttachment.HOLOGRAPHIC.ordinal());
//        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumAttachment.HOLOGRAPHIC.ordinal()),
//                "AAA",
//                " G ",
//                "ABA",
//                'A', Ingredient.fromItem(ModGuns.itemGunmetal),
//                'G', Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS_PANE)),
//                'B', new ItemStack(ItemName.crafting.getInstance(), 1, 1));

        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gun_attachment_" + EnumAttachment.RED_DOT.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumAttachment.RED_DOT.ordinal()),
                " A ",
                " G ",
                "ABA",
                'A', Ingredient.fromItem(ModGuns.itemGunmetal),
                'G', Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS_PANE)),
                'B', new ItemStack(IEContent.itemMaterial, 1, 27));
    }

}
