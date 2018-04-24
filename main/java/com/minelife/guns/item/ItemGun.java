package com.minelife.guns.item;

import buildcraft.core.BCCoreItems;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.client.RenderGun;
import com.minelife.guns.packet.PacketBullet;
import com.minelife.guns.packet.PacketFire;
import com.minelife.guns.packet.PacketReload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemGun extends Item {

    public static long nextFire = 0L, reloadTime = 0L;

    public ItemGun() {
        setRegistryName(Minelife.MOD_ID, "gun");
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

        boolean reloading = isReloading(worldIn, stack);

        if (isSelected) {
            if (reloading) {
                if (System.currentTimeMillis() >= getReloadTime(worldIn, stack)) {
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + "Ammo: " + ItemGun.getClipCount(stack) + "/" + EnumGun.values()[stack.getMetadata()].clipSize);
    }

    public static long getReloadTime(World world, ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return 0;
        if (world.isRemote) return reloadTime;
        return gunStack.hasTagCompound() && gunStack.getTagCompound().hasKey("ReloadTime") ? gunStack.getTagCompound().getLong("ReloadTime") : 0;
    }

    public static void setReloadTime(World world, ItemStack gunStack, long time) {
        if (world.isRemote) reloadTime = time;
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        tagCompound.setLong("ReloadTime", time);
        gunStack.setTagCompound(tagCompound);
    }

    public static void reload(EntityPlayer player, ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return;
        doReload(player, gunStack, ItemAmmo.getAmmoCount(player, gunStack));
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

    public static boolean isReloading(World world, ItemStack stack) {
        if (world.isRemote) return reloadTime > System.currentTimeMillis();
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("ReloadTime");
    }

    public static boolean fire(EntityPlayer player, Vec3d lookVector, long pingDelay) {
        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return false;

        EnumGun gun = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

        pingDelay = pingDelay > 300 ? 300 : pingDelay;

        if (ItemGun.isReloading(player.getEntityWorld(), player.getHeldItemMainhand())) return false;

        if (ItemGun.getClipCount(player.getHeldItemMainhand()) <= 0) {
            if (player.world.isRemote) {
                player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gun.soundEmpty), SoundCategory.NEUTRAL, 1, 1);
            }
            return false;
        }

        if (!ItemGun.canFire(player.getEntityWorld(), player.getHeldItemMainhand())) return false;

        ItemGun.addFireRate(player.getEntityWorld(), player.getHeldItemMainhand(), pingDelay);

        Bullet bullet = new Bullet(player.getEntityWorld(), player.posX, player.posY + player.getEyeHeight(), player.posZ,
                lookVector, gun.bulletSpeed, gun.damage, pingDelay, player);

        Bullet.BULLETS.add(bullet);

        ItemGun.decreaseAmmo(player.getHeldItemMainhand());

        if (!player.world.isRemote) {
            Minelife.getNetwork().sendToAllAround(new PacketBullet(gun, bullet),
                    new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 112));
        } else {
            Minelife.getNetwork().sendToServer(new PacketFire(lookVector));
            gun.resetAnimation();
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gun.soundShot), SoundCategory.NEUTRAL, 1, 1);
            initRecoil();
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void initRecoil() {
        if (Minecraft.getMinecraft().currentScreen != null) return;

        ItemStack heldItem = Minecraft.getMinecraft().player.getHeldItemMainhand();

        if (heldItem.getItem() != ModGuns.itemGun) return;

        EnumGun gunType = EnumGun.values()[heldItem.getMetadata()];

        RenderGun.recoilYaw = Minecraft.getMinecraft().player.rotationYaw;
        RenderGun.recoilPitch = Minecraft.getMinecraft().player.rotationPitch;
        RenderGun.recoilProcess = true;

        Minecraft.getMinecraft().player.turn(
                (float) generateRandomDouble(gunType.getRecoilYaw()[0],
                        gunType.getRecoilYaw()[1]) * getLeftOrRight(),
                (float) generateRandomDouble(gunType.getRecoilPitch()[0],
                        gunType.getRecoilPitch()[1]));
    }

    @SideOnly(Side.CLIENT)
    public static int getLeftOrRight() {
        int[] i = new int[]{1, -1};
        return i[Minecraft.getMinecraft().world.rand.nextInt(2)];
    }

    @SideOnly(Side.CLIENT)
    public static double generateRandomDouble(double min, double max) {
        return min + (max - min) * Minecraft.getMinecraft().world.rand.nextDouble();
    }

    // TODO: Slow down when aiming down sight and when reloading
    public static void addFireRate(World world, ItemStack gunStack, long pingDelay) {
        if (!(gunStack.getItem() == ModGuns.itemGun)) return;
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        EnumGun gunType = EnumGun.values()[gunStack.getMetadata()];
        if (world.isRemote) nextFire = System.currentTimeMillis() + gunType.fireRate;
        tagCompound.setLong("NextFire", System.currentTimeMillis() + gunType.fireRate - pingDelay);
        gunStack.setTagCompound(tagCompound);
    }

    public static boolean canFire(World world, ItemStack gunStack) {
        if (!(gunStack.getItem() == ModGuns.itemGun)) return false;
        if (world.isRemote) return System.currentTimeMillis() > nextFire;
        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : new NBTTagCompound();
        if (!tagCompound.hasKey("NextFire")) return true;
        return System.currentTimeMillis() > tagCompound.getLong("NextFire");
    }

    public static boolean reload(EntityPlayer player, long pingDelay) {
        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return false;

        EnumGun gunType = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

        pingDelay = pingDelay > 300 ? 300 : pingDelay;

        if (ItemGun.getClipCount(player.getHeldItemMainhand()) == gunType.clipSize) return false;

        if (ItemGun.isReloading(player.getEntityWorld(), player.getHeldItemMainhand())) return false;

        if (ItemAmmo.getAmmoCount(player, player.getHeldItemMainhand()).values().stream().mapToInt(ItemStack::getCount).sum() <= 0) {
            if (player.world.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Guns] " + TextFormatting.GOLD + "Out of ammo."));
            }
            return false;
        }

        ItemStack gunStack = player.getHeldItemMainhand();
        ItemGun.setReloadTime(player.getEntityWorld(), gunStack, System.currentTimeMillis() + gunType.reloadTime - pingDelay);
        player.setHeldItem(EnumHand.MAIN_HAND, gunStack);

        if (player.world.isRemote) {
            Minelife.getNetwork().sendToServer(new PacketReload());
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gunType.soundReload), SoundCategory.NEUTRAL, 1, 1);
        } else {
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * (gunType.reloadTime / 1000), 1));
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

    public void registerRecipes() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.AK47.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.AK47.ordinal()),
                "LLL",
                "SFS",
                "TTT",
                'L', Ingredient.fromItem(Item.getItemFromBlock(Blocks.PLANKS)),
                'S', Ingredient.fromItem(BCCoreItems.gearIron),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.RIFLE_FRAME.ordinal())),
                'T', Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_BLOCK)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.AWP.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.AWP.ordinal()),
                "LLL",
                "SFS",
                "TTT",
                'L', Ingredient.fromItem(Item.getItemFromBlock(Blocks.GOLD_BLOCK)),
                'S', Ingredient.fromItem(BCCoreItems.gearGold),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.SNIPER_FRAME.ordinal())),
                'T', Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_BLOCK)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.BARRETT.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.BARRETT.ordinal()),
                "LLL",
                "SFS",
                "TTT",
                'L', Ingredient.fromItem(Item.getItemFromBlock(Blocks.DIAMOND_BLOCK)),
                'S', Ingredient.fromItem(BCCoreItems.gearDiamond),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.SNIPER_FRAME.ordinal())),
                'T', Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_BLOCK)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.DESERT_EAGLE.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.DESERT_EAGLE.ordinal()),
                "LLL",
                "SFS",
                "LLL",
                'L', Ingredient.fromItem(Items.IRON_INGOT),
                'S', Ingredient.fromItem(BCCoreItems.gearIron),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.PISTOL_FRAME.ordinal())));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.M4A4.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.M4A4.ordinal()),
                "LLL",
                "SFS",
                "TTT",
                'L', Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_BLOCK)),
                'S', Ingredient.fromItem(BCCoreItems.gearIron),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.RIFLE_FRAME.ordinal())),
                'T', Ingredient.fromItem(Item.getItemFromBlock(Blocks.IRON_BLOCK)));

        name = new ResourceLocation(Minelife.MOD_ID + ":gun_" + EnumGun.MAGNUM.ordinal());
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, EnumGun.MAGNUM.ordinal()),
                "SFS",
                "TTT",
                'S', Ingredient.fromItem(BCCoreItems.gearIron),
                'F', Ingredient.fromStacks(new ItemStack(ModGuns.itemGunPart, 1, ItemGunPart.Type.PISTOL_FRAME.ordinal())),
                'T', Ingredient.fromItem(Items.IRON_INGOT));
    }
}
