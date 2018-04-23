package com.minelife.locks;

import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.util.PacketPlaySound;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class ItemLockpick extends Item {

    public ItemLockpick() {
        setRegistryName(Minelife.MOD_ID, "lockpick");
        setUnlocalizedName(Minelife.MOD_ID + ":lockpick");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":lockpick", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    @SideOnly(Side.SERVER)
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        LockType lockType = ModLocks.getLock(worldIn, pos);
        ItemStack stack = player.getHeldItem(hand);

        if (lockType == null) return EnumActionResult.FAIL;

        ItemStack toDrop = new ItemStack(ModLocks.itemLock, 1, lockType.ordinal());

        Estate estate = ModRealEstate.getEstateAt(worldIn, pos);

        if (ModPermission.hasPermission(player.getUniqueID(), "locks.override")
                || (estate != null && estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.BREAK_LOCKS))
                || Objects.equals(ModLocks.getLockPlacer(worldIn, pos), player.getUniqueID()) || lockType.tryToUnlock()) {
            ModLocks.deleteLock(worldIn, pos);
            player.dropItem(toDrop, false);
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:lock_unlocked", 1, 1), (EntityPlayerMP) player);
        } else {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minelife:lock_pick_use", 1, 1), (EntityPlayerMP) player);
        }

        if (stack.getCount() == 1) {
            player.setHeldItem(hand, ItemStack.EMPTY);
        } else {
            stack.setCount(stack.getCount() - 1);
            player.setHeldItem(hand, stack);
        }

        player.inventoryContainer.detectAndSendChanges();

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
