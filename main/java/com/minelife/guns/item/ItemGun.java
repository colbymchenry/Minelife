package com.minelife.guns.item;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;
import java.util.Map;

public class ItemGun extends Item {

    public ItemGun() {
        setRegistryName("gun");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":gun");
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
        for (EnumGunType gunType : EnumGunType.values()) {
            items.add(new ItemStack(this, 1, gunType.ordinal()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WordUtils.capitalizeFully(EnumGunType.values()[stack.getMetadata()].name().replace("_", " "));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) return;

        if (!(entityIn instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entityIn;

        boolean reloading = stack.hasTagCompound() && stack.getTagCompound().hasKey("reloadTime");

        if (isSelected) {
            if (reloading) {
                if (System.currentTimeMillis() >= stack.getTagCompound().getLong("reloadTime")) {
                    stack.getTagCompound().removeTag("reloadTime");
                    reload(player, stack);
                    player.inventory.setInventorySlotContents(itemSlot, stack);
                }
            }
        } else {
            if (reloading) {
                stack.getTagCompound().removeTag("reloadTime");
                player.inventory.setInventorySlotContents(itemSlot, stack);
            }
        }
    }

    public static void reload(EntityPlayer player, ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return;

        EnumGunType gunType = EnumGunType.values()[gunStack.getMetadata()];

        Map<Integer, ItemStack> sniperRounds = ItemAmmo.getSniperAmmo(player);
        Map<Integer, ItemStack> assaultRounds = ItemAmmo.getAssaultAmmo(player);
        Map<Integer, ItemStack> pistolRounds = ItemAmmo.getPistolAmmo(player);


        switch (gunType) {
            case AWP:
                doReload(player, gunStack, sniperRounds);
                break;
            case BARRETT:
                doReload(player, gunStack, sniperRounds);
                break;
            case MAGNUM:
                doReload(player, gunStack, pistolRounds);
                break;
            case DESERT_EAGLE:
                doReload(player, gunStack, pistolRounds);
                break;
            case AK47:
                doReload(player, gunStack, assaultRounds);
                break;
            case M4A4:
                doReload(player, gunStack, assaultRounds);
                break;
        }
    }

    private static void doReload(EntityPlayer player, ItemStack gunStack, Map<Integer, ItemStack> ammo) {
        EnumGunType gunType = EnumGunType.values()[gunStack.getMetadata()];
        int clipSize = gunType.clipSize;
        int clipCount = getClipCount(gunStack);
        int amountNeeded = clipSize - clipCount;
        List<Integer> depletedSlots = Lists.newArrayList();
        ItemStack lastStack = null;
        int lastStackSlot = -1;

        for (Integer slot : ammo.keySet()) {
            ItemStack stack = ammo.get(slot);
            if (stack.getCount() <= amountNeeded) {
                amountNeeded -= stack.getCount();
                depletedSlots.add(slot);
            } else {
                lastStack = stack.copy();
                lastStack.setCount(lastStack.getCount() - amountNeeded);
                lastStackSlot = slot;
                amountNeeded = 0;
            }

            if (amountNeeded <= 0) break;
        }

        depletedSlots.forEach(slot -> player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY));
        if (lastStackSlot > -1) player.inventory.setInventorySlotContents(lastStackSlot, lastStack);

        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        tagCompound.setInteger("ammo", (clipSize - clipCount) - amountNeeded);
        gunStack.setTagCompound(tagCompound);
    }

    public static int getClipCount(ItemStack gunStack) {
        return gunStack != null && gunStack.hasTagCompound() && gunStack.getTagCompound().hasKey("ammo") ? gunStack.getTagCompound().getInteger("ammo") : 0;
    }

    public static void decreaseAmmo(ItemStack gunStack){
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();

        int ammo = tagCompound.hasKey("ammo") ? tagCompound.getInteger("ammo") : 0;
        ammo -= 1;
        ammo = ammo < 0 ? 0 : ammo;

        tagCompound.setInteger("ammo", ammo);
    }

}
