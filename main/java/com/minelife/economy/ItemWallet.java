package com.minelife.economy;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.economy.client.wallet.InventoryWallet;
import com.minelife.util.DyeColor;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
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

        for (int i = 0; i < ItemDye.field_150921_b.length; ++i) {
            dyeStack.setItemDamage(i);
            GameRegistry.addShapedRecipe(new ItemStack(MLItems.wallet, 1, i), "AAA", "AWA", "AAA", 'A', Items.leather, 'W', dyeStack);
        }
    }


    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Wallet: $" + NumberConversions.formatter.format(getHoldings(stack));
    }

    @SideOnly(Side.SERVER)
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();

        boolean hadToUpdate = false;

        if (!tagCompound.hasKey("UUID")) {
            tagCompound.setString("UUID", UUID.randomUUID().toString());
            hadToUpdate = true;
        }
        if (!tagCompound.hasKey("owner")) {
            tagCompound.setString("owner", player.getUniqueID().toString());
            hadToUpdate = true;
        }

        if(hadToUpdate) {
            stack.setTagCompound(tagCompound);
            player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Updated! Please right-click again!"));
            return stack;
        }


        FMLNetworkHandler.openGui(player, Minelife.instance, 80098, world, 0, 0, 0);

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
                    if (s.getItem() instanceof ItemMoney)
                        holdings += ((ItemMoney) s.getItem()).amount * s.stackSize;
                }

            }
        }

        return holdings;
    }

    public static void setHoldings(ItemStack stack, List<ItemStack> stacks) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        if (stack.hasTagCompound()) {
            NBTTagList slots = new NBTTagList();

            for (byte index = 0; index < stacks.size(); ++index) {
                if (stacks.get(index) != null && stacks.get(index).stackSize > 0) {
                    NBTTagCompound slot = new NBTTagCompound();
                    slots.appendTag(slot);
                    slot.setByte("Slot", index);
                    stacks.get(index).writeToNBT(slot);
                }
            }

            stack.getTagCompound().setTag("Items", slots);
        }
    }

    public static void updateItem(ItemStack stack, InventoryWallet wallet) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (stack.hasTagCompound()) wallet.writeToNBT(stack.getTagCompound());
    }

}
