package com.minelife.guns.item;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class ItemAmmo extends Item {

    public ItemAmmo() {
        setRegistryName(Minelife.MOD_ID, "ammo");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":ammo");
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (int i = 0; i < 3; i++) {
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":ammo_" + i, "inventory");
            ModelLoader.setCustomModelResourceLocation(this, i, itemModelResourceLocation);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab != CreativeTabs.MISC) return;
            ItemStack pistolAmmo = new ItemStack(this, 1, 0);
            items.add(pistolAmmo);
            ItemStack assaultAmmo = new ItemStack(this, 1, 1);
            items.add(assaultAmmo);
            ItemStack sniperAmmo = new ItemStack(this, 1, 2);
            items.add(sniperAmmo);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        switch (stack.getMetadata()) {
            case 0: return "Pistol Ammo";
            case 1: return "Assault Ammo";
            case 2: return "Sniper Ammo";
        }
        return stack.getUnlocalizedName();
    }

    public static Map<Integer, ItemStack> getPistolAmmo(EntityPlayer player) {
        Map<Integer, ItemStack> ammo = Maps.newHashMap();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if(player.inventory.getStackInSlot(i).getItem() == ModGuns.itemAmmo) {
                if(player.inventory.getStackInSlot(i).getMetadata() == 0) {
                    ammo.put(i, player.inventory.getStackInSlot(i));
                }
            }
        }
        return ammo;
    }

    public static Map<Integer, ItemStack> getAssaultAmmo(EntityPlayer player) {
        Map<Integer, ItemStack> ammo = Maps.newHashMap();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if(player.inventory.getStackInSlot(i).getItem() == ModGuns.itemAmmo) {
                if(player.inventory.getStackInSlot(i).getMetadata() == 1) {
                    ammo.put(i, player.inventory.getStackInSlot(i));
                }
            }
        }
        return ammo;
    }

    public static Map<Integer, ItemStack> getSniperAmmo(EntityPlayer player) {
        Map<Integer, ItemStack> ammo = Maps.newHashMap();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if(player.inventory.getStackInSlot(i).getItem() == ModGuns.itemAmmo) {
                if(player.inventory.getStackInSlot(i).getMetadata() == 2) {
                    ammo.put(i, player.inventory.getStackInSlot(i));
                }
            }
        }
        return ammo;
    }

    public static int getAmmoCount(EntityPlayer player, ItemStack gunStack) {
        EnumGun gun = EnumGun.values()[gunStack.getMetadata()];

        Map<Integer, ItemStack> sniperRounds = ItemAmmo.getSniperAmmo(player);
        Map<Integer, ItemStack> assaultRounds = ItemAmmo.getAssaultAmmo(player);
        Map<Integer, ItemStack> pistolRounds = ItemAmmo.getPistolAmmo(player);

        int sniperCount = 0;
        for (ItemStack stack : sniperRounds.values()) sniperCount += stack.getCount();

        int assaultCount = 0;
        for (ItemStack stack : assaultRounds.values()) assaultCount += stack.getCount();

        int pistolCount = 0;
        for (ItemStack stack : pistolRounds.values()) pistolCount += stack.getCount();

        switch (gun) {
            case AWP:
                return sniperCount;
            case BARRETT:
                return sniperCount;
            case MAGNUM:
                return pistolCount;
            case DESERT_EAGLE:
                return pistolCount;
            case AK47:
                return assaultCount;
            case M4A4:
                return assaultCount;
        }

        return 0;
    }

    public void registerRecipes() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gun_ammo_0");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 16, 0),
                " L ",
                "CGC",
                'C', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 2)),
                'G', Ingredient.fromItem(Items.GUNPOWDER),
                'L', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 3)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_ammo_1");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 8, 1),
                " L ",
                " G ",
                "CCC",
                'C', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 2)),
                'G', Ingredient.fromItem(Items.GUNPOWDER),
                'L', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 3)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_ammo_2");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 4, 2),
                " L ",
                "CGC",
                "CGC",
                'C', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 2)),
                'G', Ingredient.fromItem(Items.GUNPOWDER),
                'L', Ingredient.fromStacks(new ItemStack(ItemName.ingot.getInstance(), 1, 3)));
    }

}
