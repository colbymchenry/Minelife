package com.minelife.economy;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.util.DyeColor;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemWallet extends Item {

    public IIcon icon_empty;
    public IIcon icon_bills;

    public ItemWallet() {
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("wallet");
        setTextureName(Minelife.MOD_ID + ":wallet_empty");
        setMaxStackSize(1);
    }

    public static void registerRecipes() {
        ItemStack dyeStack = new ItemStack(Items.dye);

        for (int i = 0; i < ItemDye.field_150921_b.length; ++i)
        {
            dyeStack.setItemDamage(i);
            GameRegistry.addShapedRecipe(new ItemStack(MLItems.wallet, 1, i), "AAA", "AWA", "AAA", 'A', Items.leather, 'W', dyeStack);
        }
    }



    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Wallet: $" + NumberConversions.formatter.format(getHoldings(stack));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!stack.hasTagCompound()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setString("UUID", UUID.randomUUID().toString());
            stack.setTagCompound(tagCompound);
        } else {
            if(!stack.getTagCompound().hasKey("UUID")) stack.getTagCompound().setString("UUID", UUID.randomUUID().toString());
        }

        if(!stack.getTagCompound().hasKey("owner")) stack.getTagCompound().setString("owner", player.getUniqueID().toString());


        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);

        if (!world.isRemote) player.openGui(Minelife.instance, 80098, world, 0, 0, 0);

        return stack;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon_empty = register.registerIcon(Minelife.MOD_ID + ":wallet_empty");
        icon_bills = register.registerIcon(Minelife.MOD_ID + ":wallet_bills");
    }

    public static int getHoldings(ItemStack stack) {
        int holdings = 0;

        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("Items")) {
                NBTTagList nbttaglist = stack.getTagCompound().getTagList("Items", 10);

                for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                    NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
                    ItemStack s = ItemStack.loadItemStackFromNBT(slot);
                    if(s.getItem() instanceof ItemMoney) holdings += ((ItemMoney) s.getItem()).amount * s.stackSize;
                }

            }
        }

        return holdings;
    }

}
