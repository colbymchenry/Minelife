package com.minelife.locks;

import com.minelife.Minelife;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLock extends Item {

    public ItemLock() {
        setRegistryName(Minelife.MOD_ID, "lock");
        setUnlocalizedName(Minelife.MOD_ID + ":lock");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @SideOnly(Side.SERVER)
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(ModLocks.getLock(worldIn, pos) != null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "There is already a lock on that block."));
            return EnumActionResult.FAIL;
        }

        Estate estate = ModRealEstate.getEstateAt(worldIn, pos);

        if(estate != null && !estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.PLACE)) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "You cannot place locks here."));
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);
        LockType type = LockType.values()[getMetadata(stack)];
        if(!ModLocks.addLock(worldIn, pos, type)) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
            return EnumActionResult.FAIL;
        }
        if(stack.getCount() == 1) {
            player.setHeldItem(hand, ItemStack.EMPTY);
        } else {
            stack.setCount(stack.getCount() - 1);
            player.setHeldItem(hand, stack);
        }

        Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:block.anvil.use", 1, 1), (EntityPlayerMP) player);
        player.inventoryContainer.detectAndSendChanges();
        player.sendMessage(new TextComponentString("Lock placed! There is a " + (100.0D - type.chance) + "% of lockpick success."));
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        for (int i = 0; i < LockType.values().length; i++) {
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":lock_" + LockType.values()[i].name().toLowerCase(), "inventory");
            ModelLoader.setCustomModelResourceLocation(this, i, itemModelResourceLocation);
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab != CreativeTabs.MISC) return;
        for (int i = 0; i < LockType.values().length; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

}
