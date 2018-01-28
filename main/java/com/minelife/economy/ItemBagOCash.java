package com.minelife.economy;

import com.minelife.Minelife;
import com.minelife.util.NumberConversions;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemBagOCash extends ItemWallet {

    public IIcon icon;

    public ItemBagOCash() {
        setUnlocalizedName("bagocash");
        setTextureName(Minelife.MOD_ID + ":bagocash");
        setCreativeTab(CreativeTabs.tabMisc);
        setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Bag O' Cash: $" + NumberConversions.formatter.format(getHoldings(stack));
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return icon;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return icon;
    }

    @Override
    public IIcon getIconIndex(ItemStack p_77650_1_) {
        return icon;
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return icon;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_) {
        return icon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon = register.registerIcon(Minelife.MOD_ID + ":bagocash");
    }

}
