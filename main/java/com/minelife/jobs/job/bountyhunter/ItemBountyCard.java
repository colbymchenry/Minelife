package com.minelife.jobs.job.bountyhunter;

import com.minelife.Minelife;
import com.minelife.util.server.NameFetcher;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class ItemBountyCard extends Item {

    public static final ItemBountyCard INSTANCE = new ItemBountyCard();

    private ItemBountyCard() {
        setRegistryName(Minelife.MOD_ID, "bounty_card");
        setUnlocalizedName(Minelife.MOD_ID + ":bounty_card");
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return getTarget(stack) == null ? "Bounty Card" : TextFormatting.RED + "Bounty Card: " + NameFetcher.asyncFetchClient(getTarget(stack));
    }

    public static void setTarget(UUID target, ItemStack stack) {
        if(stack.getItem() != INSTANCE) return;
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tagCompound.setString("target", target.toString());
        stack.setTagCompound(tagCompound);
    }

    public static UUID getTarget(ItemStack stack) {
        if(stack.getItem() != INSTANCE) return null;
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("target") ? UUID.fromString(stack.getTagCompound().getString("target")) : null;
    }

}
