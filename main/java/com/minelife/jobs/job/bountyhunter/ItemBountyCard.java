package com.minelife.jobs.job.bountyhunter;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ItemBountyCard extends Item {

    public static final ItemBountyCard INSTANCE = new ItemBountyCard();

    private ItemBountyCard() {
        setRegistryName(Minelife.MOD_ID, "bounty_card");
        setUnlocalizedName(Minelife.MOD_ID + ":bounty_card");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiBountyCard(playerIn.getHeldItem(handIn)));
        return super.onItemRightClick(worldIn, playerIn, handIn);
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
