package com.minelife.gun.item.guns;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.gun.client.RenderGun;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.ModGun;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.server.EntityShotEvent;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public abstract class ItemGun extends Item {

    public static GunAK47 ak47;
    public static GunAWP awp;
    public static GunBarrett barrett;
    public static GunDesertEagle desertEagle;
    public static GunM4A4 m4A4;
    public static GunMagnum magnum;

    public static final void registerGuns() {
        ak47 = new GunAK47();
        awp = new GunAWP();
        barrett = new GunBarrett();
        desertEagle = new GunDesertEagle();
        m4A4 = new GunM4A4();
        magnum = new GunMagnum();
        registerRecipes();
    }

    public static final void registerRecipes() {
        ak47.registerRecipe();
        awp.registerRecipe();
        barrett.registerRecipe();
        desertEagle.registerRecipe();
        m4A4.registerRecipe();
        magnum.registerRecipe();
    }

    @SideOnly(Side.CLIENT)
    public static final void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer(ak47, new RenderGun(ak47));
        MinecraftForgeClient.registerItemRenderer(awp, new RenderGun(awp));
        MinecraftForgeClient.registerItemRenderer(barrett, new RenderGun(barrett));
        MinecraftForgeClient.registerItemRenderer(desertEagle, new RenderGun(desertEagle));
        MinecraftForgeClient.registerItemRenderer(m4A4, new RenderGun(m4A4));
        MinecraftForgeClient.registerItemRenderer(magnum, new RenderGun(magnum));
    }

    @SideOnly(Side.CLIENT)
    public ItemGunClient clientHandler;

    public ItemGun() {
        setUnlocalizedName(getName());
        setCreativeTab(ModGun.tabGuns);
        GameRegistry.registerItem(this, getClass().getSimpleName());
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean inHand) {
        if (!world.isRemote) {
            if(stack.hasTagCompound() && stack.stackTagCompound.hasKey("reloadTime")) {
                if(System.currentTimeMillis() > stack.stackTagCompound.getLong("reloadTime")) {
                    reload((EntityPlayer) entity, stack);
                    stack.stackTagCompound.removeTag("reloadTime");
                }
            }
            return;
        }
        if (!(entity instanceof EntityPlayer)) return;
        getClientHandler().onUpdate(stack, world, (EntityPlayer) entity, slot, inHand);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean arg) {
        list.add("Ammo: " + getCurrentClipHoldings(stack) + "/" + getClipSize());
    }

    @SideOnly(Side.SERVER)
    public final void shootBullet(EntityPlayer player, ItemStack stack) {
        if(stack == null || !(stack.getItem() instanceof ItemGun)) return;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        ItemAmmo.AmmoType ammoType = stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ammoType") ?
                ItemAmmo.AmmoType.valueOf(stack.stackTagCompound.getString("ammoType")) : ItemAmmo.AmmoType.NORMAL;

        boolean canFire = System.currentTimeMillis() > (!stackData.hasKey("nextFire") ? 0 : stackData.getLong("nextFire"))
                && getCurrentClipHoldings(stack) > 0;

        if (!canFire) return;

        stackData.setLong("nextFire", System.currentTimeMillis() + getFireRate());
        stackData.setInteger("ammo." + ammoType.name(), getCurrentClipHoldings(stack) - 1);

        stack.stackTagCompound = stackData;

        player.worldObj.playSoundToNearExcept(player, Minelife.MOD_ID + ":guns." + getName() + ".shot", 0.5F, 1.0F);

        PlayerHelper.TargetResult target = PlayerHelper.getTarget(player, 75);

        if(target.getBlock() != null) {
            if(ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                player.worldObj.createExplosion(player, target.getBlockVector().getX(), target.getBlockVector().getY(), target.getBlockVector().getZ(), 4.0F, false);
            } else if (ammoType == ItemAmmo.AmmoType.INCENDIARY) {
            }
        }

        if (target.getEntity() != null) {

            if(ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                player.worldObj.createExplosion(player, target.getEntity().posX, target.getEntity().posY, target.getEntity().posZ, 4.0F, false);
            } else if (ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                target.getEntity().setFire(6);
            }

            MinecraftForge.EVENT_BUS.post(new EntityShotEvent(player, target.getEntity(), player.getHeldItem()));
        }
    }

    public abstract String getName();

    public abstract int getFireRate();

    public abstract int getDamage();

    public abstract int getReloadTime();

    public abstract int getClipSize();

    public abstract List<ItemAmmo> getAmmo();

    public abstract boolean isFullAuto();

    @SideOnly(Side.CLIENT)
    public abstract ItemGunClient getClientHandler();

    public abstract void registerRecipe();

    /**
     * ---------------------------- STATIC METHODS -----------------------------
     */

    // TODO: Take into account ammo type
    public static final int getCurrentClipHoldings(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemGun)) return 0;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        ItemAmmo.AmmoType ammoType = stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ammoType") ?
                ItemAmmo.AmmoType.valueOf(stack.stackTagCompound.getString("ammoType")) : ItemAmmo.AmmoType.NORMAL;

        return stackData.hasKey("ammo." + ammoType.name()) ? stackData.getInteger("ammo." + ammoType.name()) : 0;
    }

    public static final void reload(EntityPlayer player, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemGun)) return;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        ItemAmmo.AmmoType ammoType = stack.stackTagCompound != null && stack.stackTagCompound.hasKey("ammoType") ?
                ItemAmmo.AmmoType.valueOf(stack.stackTagCompound.getString("ammoType")) : ItemAmmo.AmmoType.NORMAL;

        ItemStack[] ammoFromInventory = getAmmoFromInventory(player, stack, ammoType);

        if(ammoFromInventory.length == 0) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find any " + ammoType.name().toLowerCase() + " rounds."));
            return;
        }

        if (ammoFromInventory != null) {

            int maxAmmo = ((ItemGun) stack.getItem()).getClipSize();
            int currentHoldings = getCurrentClipHoldings(stack);
            int needed = maxAmmo - currentHoldings;
            int insertingCount = 0;



            for(ItemStack itemStack : ammoFromInventory) {
                if(insertingCount < needed) {
                    if(itemStack.stackSize - (needed - insertingCount) < 1) {
                        insertingCount += itemStack.stackSize;
                        itemStack.stackSize = 0;
                    } else {
                        itemStack.stackSize -= (needed - insertingCount);
                        insertingCount += (needed - insertingCount);
                    }
                } else {
                    break;
                }
            }

            stackData.setInteger("ammo." + ammoType.name(), currentHoldings + insertingCount);

            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                for(ItemStack itemStack : ammoFromInventory) {
                    if (ItemStack.areItemStacksEqual(player.inventory.mainInventory[i], itemStack)) {
                        if (itemStack.stackSize < 1) {
                            player.inventory.mainInventory[i] = null;
                        } else {
                            player.inventory.mainInventory[i] = itemStack;
                        }
                    }
                }
            }
        }

        stack.stackTagCompound = stackData;
    }

    public static ItemStack[] getAmmoFromInventory(EntityPlayer player, ItemStack stack, ItemAmmo.AmmoType ammoType) {
        if (stack == null || !(stack.getItem() instanceof ItemGun)) return null;

        ItemGun gun = (ItemGun) stack.getItem();
        int maxAmmo = gun.getClipSize();
        int needed = maxAmmo - getCurrentClipHoldings(stack);
        int obtained = 0;

        List<ItemStack> itemStacks = Lists.newArrayList();

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack != null && itemStack.getItem() instanceof ItemAmmo) {
                if (gun.getAmmo().contains(itemStack.getItem()) && ammoType == ((ItemAmmo) itemStack.getItem()).getAmmoType()) {
                    obtained += itemStack.stackSize;
                    itemStacks.add(itemStack);
                    if(obtained >= needed) {
                        break;
                    }
                }
            }
        }



        return itemStacks.toArray(new ItemStack[itemStacks.size()]);
    }

}
