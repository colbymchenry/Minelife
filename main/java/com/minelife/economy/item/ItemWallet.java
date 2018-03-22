package com.minelife.economy.item;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.GuiHandler;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.WithdrawlResult;
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

    public static WithdrawlResult withdrawPlayer(EntityPlayer player, int amount) {
        return ModEconomy.withdraw(new InventoryRange(player.inventory, 0, 36), amount);
    }

    public static int depositPlayer(EntityPlayer player, int amount) {
        return ModEconomy.deposit(new InventoryRange(player.inventory, 0, 36), amount);
    }

}
