package com.minelife.economy.item;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.GuiHandler;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.client.gui.wallet.InventoryWallet;
import com.minelife.economy.client.render.RenderWalletItem;
import com.minelife.util.NumberConversions;
import com.minelife.util.fireworks.DyeColor;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemWallet extends Item {

    public ItemWallet() {
        setRegistryName("wallet");
        setMaxDamage(0);
        setUnlocalizedName(Minelife.MOD_ID + ":wallet");
        setCreativeTab(CreativeTabs.MISC);
        setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            if (playerIn.getHeldItem(EnumHand.MAIN_HAND) != null && playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == this) {
                ItemStack stack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
                NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
                if (!tagCompound.hasKey("Owner")) {
                    tagCompound.setString("Owner", playerIn.getUniqueID().toString());
                    stack.setTagCompound(tagCompound);
                    playerIn.setHeldItem(EnumHand.MAIN_HAND, stack);
                }
                playerIn.openGui(Minelife.getInstance(), GuiHandler.WALLET_ID, playerIn.world, 0, 0, 0);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getMetadata();
    }

    @SideOnly(Side.CLIENT)
    public void registerModels() {
        final ModelResourceLocation location0 = new ModelResourceLocation("minelife:wallet_empty", "inventory");
        final ModelResourceLocation location1 = new ModelResourceLocation("minelife:wallet_bills", "inventory");
        for (DyeColor dyeColor : DyeColor.values())
            ModelLoader.setCustomModelResourceLocation(this, dyeColor.ordinal(), location0);
        ModelLoader.setCustomModelResourceLocation(this, DyeColor.values().length + 1, location1);
        ModelRegistryHelper.register(location0, new RenderWalletItem(() -> location0));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != CreativeTabs.MISC) return;
        for (DyeColor dyeColor : DyeColor.values()) {
            items.add(new ItemStack(this, 1, dyeColor.ordinal()));
        }
    }

    public void registerRecipes() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":wallet." + DyeColor.BROWN.ordinal());
        ResourceLocation group = null;

        GameRegistry.addShapedRecipe(name, group,
                new ItemStack(this, 1, DyeColor.BROWN.ordinal()), "###", "###", '#', new ItemStack(Items.LEATHER));

        for (DyeColor dyeColor : DyeColor.values()) {
            if (dyeColor != DyeColor.BROWN) {
                name = new ResourceLocation(Minelife.MOD_ID + ":wallet." + dyeColor.ordinal());
                GameRegistry.addShapedRecipe(name, group, new ItemStack(this, 1, dyeColor.ordinal()), "###", "#D#", "###", '#', Items.LEATHER, 'D', dyeColor.getItem());
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        boolean hasOwner = stack.hasTagCompound() && stack.getTagCompound().hasKey("Owner");
        int holdings = getHoldings(stack);

        if (hasOwner) {
            UUID uniqueID = UUID.fromString(stack.getTagCompound().getString("Owner"));
            String name = NameFetcher.asyncFetchClient(uniqueID);
            return name.equalsIgnoreCase("Fetching...") ? super.getItemStackDisplayName(stack) : name + "'s Wallet";
        }


        return WordUtils.capitalizeFully(DyeColor.values()[stack.getMetadata()].name().replace("_", " ")) + " Wallet";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + "$" + NumberConversions.format(getHoldings(stack)));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    public static int getHoldings(ItemStack wallet) {
        int holdings = 0;
        if (wallet != null && wallet.getItem() == ModEconomy.itemWallet && wallet.hasTagCompound() &&
                wallet.getTagCompound().hasKey("Inventory")) {
            InventoryWallet inventoryWallet = new InventoryWallet(wallet);
            for (ItemStack itemStack : inventoryWallet.getInventory().getItems())
                holdings += ItemCash.getAmount(itemStack);
        }
        return holdings;
    }

    public static Map<Integer, ItemStack> getWallets(EntityPlayer player) {
        Map<Integer, ItemStack> map = Maps.newHashMap();
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i).getItem() == ModEconomy.itemWallet) {
                map.put(i, player.inventory.getStackInSlot(i));
            }
        }
        return map;
    }

    public static List<ItemStack> withdrawPlayer(EntityPlayer player, int amount) {
        List<ItemStack> cashItems = Lists.newArrayList();
        List<Integer> emptySlots = Lists.newArrayList();


        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == ModEconomy.itemCash) {
                amount -= ItemCash.getAmount(itemStack);
                emptySlots.add(i);
                cashItems.add(itemStack);
                if (amount < 1) break;
            }
        }

        for (Integer emptySlot : emptySlots) player.inventory.setInventorySlotContents(emptySlot, ItemStack.EMPTY);

        InventoryRange inventoryRange = new InventoryRange(player.inventory, 0, 35);

        if (amount < 0) {
            int addBack = Math.abs(amount);

            int hundreds = addBack / 100;
            if (hundreds > 0) {
                addBack -= 100 * hundreds;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, hundreds, 5), false);
            }

            int fifties = addBack / 50;
            if (fifties > 0) {
                addBack -= 50 * fifties;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fifties, 4), false);
            }

            int twenties = addBack / 20;
            if (twenties > 0) {
                addBack -= 20 * twenties;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, twenties, 3), false);
            }

            int tens = addBack / 10;
            if (tens > 0) {
                addBack -= 10 * tens;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, tens, 2), false);
            }

            int fives = addBack / 5;
            if (fives > 0) {
                addBack -= 5 * fives;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fives, 1), false);
            }

            int ones = addBack;
            if (ones > 0) {
                addBack -= ones;
                InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, ones, 0), false);
            }
        }

        return cashItems;
    }

    public static int deposit(EntityPlayer player, int amount) {
        InventoryRange inventoryRange = new InventoryRange(player.inventory, 0, 35);

        int hundreds =amount / 100;
        if (hundreds > 0) {
            amount -= 100 * hundreds;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, hundreds, 5), false);
            amount += 100 * didNotFit;
        }

        int fifties =amount / 50;
        if (fifties > 0) {
            amount -= 50 * fifties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fifties, 4), false);
            amount += 50 * didNotFit;
        }

        int twenties =amount / 20;
        if (twenties > 0) {
            amount -= 20 * twenties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, twenties, 3), false);
            amount += 20 * didNotFit;
        }

        int tens =amount / 10;
        if (tens > 0) {
            amount -= 10 * tens;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, tens, 2), false);
            amount += 10 * didNotFit;
        }

        int fives =amount / 5;
        if (fives > 0) {
            amount -= 5 * fives;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fives, 1), false);
            amount += 5 * didNotFit;
        }

        int ones = (amount);
        if (ones > 0) {
            amount -= ones;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, ones, 0), false);
            amount += didNotFit;
        }

        return amount;
    }

}
