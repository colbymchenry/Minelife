package com.minelife.locks;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGunPart;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

public class ItemLock extends Item {

    public ItemLock() {
        setRegistryName(Minelife.MOD_ID, "lock");
        setUnlocalizedName(Minelife.MOD_ID + ":lock");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @SideOnly(Side.SERVER)
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.getBlockState(pos).getBlock() == ModEconomy.blockCash) return EnumActionResult.SUCCESS;

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
        if(!ModLocks.addLock(worldIn, pos, type, player.getUniqueID())) {
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

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WordUtils.capitalizeFully(LockType.values()[stack.getMetadata()].name().replace("_", " ")) + " Lock";
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

    public void registerRecipes() {
        for (LockType lockType : LockType.values()) {
            ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":lock_" + lockType.name());
            GameRegistry.addShapedRecipe(name, null, new ItemStack(this, 1, lockType.ordinal()),
                    " X ",
                    "XXX",
                    "XXX",
                    'X', Ingredient.fromItem(lockType.itemResource));
        }
    }

}
