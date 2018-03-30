package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

public class ItemGunPart extends Item {

    public ItemGunPart() {
        setRegistryName(Minelife.MOD_ID, "gun_part");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":gun_part");
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
        for (Type type : Type.values()) items.add(new ItemStack(this, 1, type.ordinal()));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WordUtils.capitalizeFully(Type.values()[stack.getMetadata()].name().replace("_", " "));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (int i = 0; i < Type.values().length; i++) {
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":gunparts/" + Type.values()[i].name().toLowerCase(), "inventory");
            ModelLoader.setCustomModelResourceLocation(this, i, itemModelResourceLocation);
        }
    }

    public void registerRecipes() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.GRIP.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.GRIP.ordinal()),
                " G ",
                " G ",
                'G', ModGuns.itemGunmetal);

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.PISTOL_BARREL.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.PISTOL_BARREL.ordinal()),
                "GGG",
                'G', ModGuns.itemGunmetal);

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.PISTOL_FRAME.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.PISTOL_FRAME.ordinal()),
                "GTB",
                'G', Ingredient.fromStacks(new ItemStack(this, 1, Type.GRIP.ordinal())),
                'T', Ingredient.fromStacks(new ItemStack(this, 1, Type.TRIGGER.ordinal())),
                'B', Ingredient.fromStacks(new ItemStack(this, 1, Type.PISTOL_BARREL.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.RIFLE_BARREL.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.RIFLE_BARREL.ordinal()),
                "GGG",
                'G', Ingredient.fromStacks(new ItemStack(this, 1, Type.PISTOL_BARREL.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.RIFLE_FRAME.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.RIFLE_FRAME.ordinal()),
                "STB",
                " G ",
                'S', Ingredient.fromStacks(new ItemStack(this, 1, Type.RIFLE_STOCK.ordinal())),
                'T', Ingredient.fromStacks(new ItemStack(this, 1, Type.TRIGGER.ordinal())),
                'B', Ingredient.fromStacks(new ItemStack(this, 1, Type.RIFLE_BARREL.ordinal())),
                'G', Ingredient.fromStacks(new ItemStack(this, 1, Type.GRIP.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.RIFLE_STOCK.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.RIFLE_STOCK.ordinal()),
                "G  ",
                "GGG",
                "G  ",
                'G', Ingredient.fromItem(ModGuns.itemGunmetal));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.SNIPER_BARREL.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.SNIPER_BARREL.ordinal()),
                "GGG",
                'G', Ingredient.fromStacks(new ItemStack(this, 1, Type.RIFLE_BARREL.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.SNIPER_FRAME.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.SNIPER_FRAME.ordinal()),
                " S ",
                "KTB",
                " G ",
                'S', Ingredient.fromStacks(new ItemStack(this, 1, Type.SNIPER_SCOPE.ordinal())),
                'K', Ingredient.fromStacks(new ItemStack(this, 1, Type.RIFLE_STOCK.ordinal())),
                'T', Ingredient.fromStacks(new ItemStack(this, 1, Type.TRIGGER.ordinal())),
                'B', Ingredient.fromStacks(new ItemStack(this, 1, Type.SNIPER_BARREL.ordinal())),
                'G', Ingredient.fromStacks(new ItemStack(this, 1, Type.GRIP.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.SNIPER_SCOPE.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.SNIPER_SCOPE.ordinal()),
                "GMG",
                'G', Ingredient.fromItem(Item.getItemFromBlock(Blocks.GLASS_PANE)),
                'M', Ingredient.fromItem(ModGuns.itemGunmetal));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_part_" + Type.TRIGGER.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, Type.TRIGGER.ordinal()),
                " G ",
                " G ",
                "  G",
                'G', Ingredient.fromItem(ModGuns.itemGunmetal));
    }

    enum Type {
        GRIP, PISTOL_BARREL, PISTOL_FRAME, RIFLE_BARREL, RIFLE_FRAME, RIFLE_STOCK, SNIPER_BARREL, SNIPER_FRAME, SNIPER_SCOPE, TRIGGER
    }
}