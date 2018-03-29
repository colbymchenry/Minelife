package com.minelife.guns.item;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.packet.PacketBullet;
import com.minelife.guns.packet.PacketFire;
import com.minelife.guns.packet.PacketReload;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
        for (EnumGun gun : EnumGun.values()) {
            items.add(new ItemStack(this, 1, gun.ordinal()));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return ItemGun.getCustomName(stack) != null ? TextFormatting.ITALIC + ItemGun.getCustomName(stack) :
                WordUtils.capitalizeFully(EnumGun.values()[stack.getMetadata()].name().replace("_", " "));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) return;

        if (!(entityIn instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entityIn;

        boolean reloading = isReloading(stack);

        if (isSelected) {
            if (reloading) {
                if (System.currentTimeMillis() >= getReloadTime(stack)) {
                    stack.getTagCompound().removeTag("ReloadTime");
                    reload(player, stack);
                    player.inventory.setInventorySlotContents(itemSlot, stack);
                }
            }
        } else {
            if (reloading) {
                stack.getTagCompound().removeTag("ReloadTime");
                player.inventory.setInventorySlotContents(itemSlot, stack);
            }
        }
    }

    public static long getReloadTime(ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return 0;
        return gunStack.hasTagCompound() && gunStack.getTagCompound().hasKey("ReloadTime") ? gunStack.getTagCompound().getLong("ReloadTime") : 0;
    }

    public static void setReloadTime(ItemStack gunStack, long time) {
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        tagCompound.setLong("ReloadTime", time);
        gunStack.setTagCompound(tagCompound);
    }

    public static void reload(EntityPlayer player, ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return;

        EnumGun gun = EnumGun.values()[gunStack.getMetadata()];

        Map<Integer, ItemStack> sniperRounds = ItemAmmo.getSniperAmmo(player);
        Map<Integer, ItemStack> assaultRounds = ItemAmmo.getAssaultAmmo(player);
        Map<Integer, ItemStack> pistolRounds = ItemAmmo.getPistolAmmo(player);


        switch (gun) {
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
        EnumGun gun = EnumGun.values()[gunStack.getMetadata()];
        int clipSize = gun.clipSize;
        int clipCount = getClipCount(gunStack);
        int amountNeeded = clipSize - clipCount;
        List<Integer> depletedSlots = Lists.newArrayList();
        int lastStackSlot = -1;

        int toAdd = 0;
        for (Integer slot : ammo.keySet()) {
            ItemStack stack = ammo.get(slot);
            if (stack.getCount() <= amountNeeded) {
                amountNeeded -= stack.getCount();
                depletedSlots.add(slot);
                toAdd += stack.getCount();
            } else {
                stack.setCount(stack.getCount() - amountNeeded);
                lastStackSlot = slot;
                toAdd += amountNeeded;
                amountNeeded = 0;
            }

            if (amountNeeded <= 0) break;
        }

        depletedSlots.forEach(slot -> player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY));
        if (lastStackSlot > -1) player.inventory.setInventorySlotContents(lastStackSlot, ammo.get(lastStackSlot));

        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        tagCompound.setInteger("Ammo", clipCount + toAdd);
        gunStack.setTagCompound(tagCompound);
    }

    public static int getClipCount(ItemStack gunStack) {
        return gunStack != null && gunStack.hasTagCompound() && gunStack.getTagCompound().hasKey("Ammo") ? gunStack.getTagCompound().getInteger("Ammo") : 0;
    }

    public static void decreaseAmmo(ItemStack gunStack) {
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();

        int ammo = tagCompound.hasKey("Ammo") ? tagCompound.getInteger("Ammo") : 0;
        ammo -= 1;
        ammo = ammo < 0 ? 0 : ammo;

        tagCompound.setInteger("Ammo", ammo);
    }

    public static boolean isReloading(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("ReloadTime");
    }

    public static boolean fire(EntityPlayer player, Vec3d lookVector, long pingDelay) {
        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return false;

        EnumGun gun = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

        pingDelay = pingDelay > 200 ? 60 : pingDelay;

        if (ItemGun.isReloading(player.getHeldItemMainhand())) return false;

        if (ItemGun.getClipCount(player.getHeldItemMainhand()) <= 0) {
            if (player.world.isRemote) {
                player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gun.soundEmpty), SoundCategory.NEUTRAL, 1, 1);
            }
            return false;
        }

        Bullet bullet = new Bullet(player.getEntityWorld(), player.posX, player.posY + player.getEyeHeight(), player.posZ, pingDelay,
                lookVector, gun.bulletSpeed, gun.damage, player);

        Bullet.BULLETS.add(bullet);

        ItemGun.decreaseAmmo(player.getHeldItemMainhand());

        if (!player.world.isRemote) {
            Minelife.getNetwork().sendToAllAround(new PacketBullet(bullet),
                    new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 112));
        } else {
            Minelife.getNetwork().sendToServer(new PacketFire(lookVector));
            gun.resetAnimation();
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gun.soundShot), SoundCategory.NEUTRAL, 1, 1);
        }

        return true;
    }

    public static boolean reload(EntityPlayer player, long pingDelay) {
        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return false;

        EnumGun gunType = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

        pingDelay = pingDelay > 200 ? 60 : pingDelay;

        if (ItemGun.getClipCount(player.getHeldItemMainhand()) == gunType.clipSize) return false;

        if (ItemGun.isReloading(player.getHeldItemMainhand())) return false;

        if (ItemAmmo.getAmmoCount(player, player.getHeldItemMainhand()) <= 0) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Guns] " + TextFormatting.GOLD + "Out of ammo."));
            }
            return false;
        }

        ItemStack gunStack = player.getHeldItemMainhand();
        ItemGun.setReloadTime(gunStack, System.currentTimeMillis() + gunType.reloadTime - pingDelay);
        player.setHeldItem(EnumHand.MAIN_HAND, gunStack);

        if (player.world.isRemote) {
            Minelife.getNetwork().sendToServer(new PacketReload());
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gunType.soundReload), SoundCategory.NEUTRAL, 1, 1);
        }
        return true;
    }

    public static EnumAttachment getAttachment(ItemStack gun) {
        if (gun == null || gun.getItem() != ModGuns.itemGun) return null;
        return gun.hasTagCompound() && gun.getTagCompound().hasKey("Attachment") ? EnumAttachment.valueOf(gun.getTagCompound().getString("Attachment")) : null;
    }

    public static void setAttachment(ItemStack gun, EnumAttachment attachment) {
        if (gun == null || gun.getItem() != ModGuns.itemGun) return;
        NBTTagCompound tagCompound = gun.hasTagCompound() ? gun.getTagCompound() : new NBTTagCompound();
        if (attachment == null)
            tagCompound.removeTag("Attachment");
        else
            tagCompound.setString("Attachment", attachment.name());
        gun.setTagCompound(tagCompound);
    }

    public static String getCustomName(ItemStack gun) {
        if (gun == null || gun.getItem() != ModGuns.itemGun) return null;
        return gun.hasTagCompound() && gun.getTagCompound().hasKey("CustomName") ? gun.getTagCompound().getString("CustomName") : null;
    }

    public static void setCustomName(ItemStack gun, String name) {
        if (gun == null || gun.getItem() != ModGuns.itemGun) return;
        NBTTagCompound tagCompound = gun.hasTagCompound() ? gun.getTagCompound() : new NBTTagCompound();
        if (name == null)
            tagCompound.removeTag("CustomName");
        else
            tagCompound.setString("CustomName", name);
        gun.setTagCompound(tagCompound);
    }

}
