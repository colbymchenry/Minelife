package com.minelife.gun;

import com.minelife.Minelife;
import com.minelife.gun.server.EntityShotEvent;
import com.minelife.gun.server.ServerProxy;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public abstract class BaseGun extends Item {

    @SideOnly(Side.CLIENT)
    public BaseGunClient clientHandler;

    public BaseGun() {
        setUnlocalizedName(getName());
        setCreativeTab(ModGun.tabGuns);
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
        if(stack == null || !(stack.getItem() instanceof BaseGun)) return;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        boolean canFire = System.currentTimeMillis() > (!stackData.hasKey("nextFire") ? 0 : stackData.getLong("nextFire"))
                && getCurrentClipHoldings(stack) > 0;

        if (!canFire) return;

        stackData.setLong("nextFire", System.currentTimeMillis() + getFireRate());
        stackData.setInteger("ammo", getCurrentClipHoldings(stack) - 1);

        stack.stackTagCompound = stackData;

        player.worldObj.playSoundToNearExcept(player, Minelife.MOD_ID + ":gun." + getName() + ".shot", 0.5F, 1.0F);

        EntityLivingBase target = PlayerHelper.getTargetEntity(player, 75);

        if (target != null)
            MinecraftForge.EVENT_BUS.post(new EntityShotEvent(player, target, player.getHeldItem()));
    }

    public abstract String getName();

    public abstract int getFireRate();

    public abstract int getDamage();

    public abstract int getReloadTime();

    public abstract int getClipSize();

    public abstract BaseAmmo getAmmoType();

    public abstract boolean isFullAuto();

    @SideOnly(Side.CLIENT)
    protected abstract Class<?extends BaseGunClient> getClientHandlerClass();

    @SideOnly(Side.CLIENT)
    public final BaseGunClient getClientHandler() {
        return clientHandler;
    }

    /**
     * ---------------------------- STATIC METHODS -----------------------------
     */

    public static final int getCurrentClipHoldings(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof BaseGun)) return 0;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        return stackData.hasKey("ammo") ? stackData.getInteger("ammo") : 0;
    }

    public static final void reload(EntityPlayer player, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof BaseGun)) return;

        NBTTagCompound stackData = !stack.hasTagCompound() ? new NBTTagCompound() : stack.stackTagCompound;

        ItemStack ammoFromInventory = getAmmoFromInventory(player, stack);

        if (ammoFromInventory != null) {
            stackData.setInteger("ammo", ((BaseGun) stack.getItem()).getClipSize());

            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                if (ItemStack.areItemStacksEqual(player.inventory.mainInventory[i], ammoFromInventory)) {
                    if(ammoFromInventory.stackSize == 1) {
                        player.inventory.mainInventory[i] = null;
                    } else {
                        ammoFromInventory.stackSize -= 1;
                        player.inventory.mainInventory[i] = ammoFromInventory;
                    }
                    break;
                }
            }
        }

        stack.stackTagCompound = stackData;
    }

    public static ItemStack getAmmoFromInventory(EntityPlayer player, ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof BaseGun)) return null;

        BaseGun gun = (BaseGun) stack.getItem();

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack != null && itemStack.getItem() instanceof BaseAmmo) {
                if (gun.getAmmoType() == itemStack.getItem()) {
                    return itemStack;
                }
            }
        }

        return null;
    }

}
