package com.minelife.cape;

import com.minelife.Minelife;
import com.minelife.cape.client.GuiEditCape;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ItemCape extends Item {

    public ItemCape() {
        setRegistryName(Minelife.MOD_ID, "cape");
        setUnlocalizedName(Minelife.MOD_ID + ":cape");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItemMainhand();

        if (stack.getItem() == ModCapes.itemCape) {
            int slot = playerIn.inventory.currentItem;
            Minecraft.getMinecraft().displayGuiScreen(new GuiEditCape(stack, slot));
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public void setPixels(ItemStack stack, String pixels) {
        NBTTagCompound tagCompound = !stack.hasTagCompound() ? new NBTTagCompound() : stack.getTagCompound();
        if (pixels == null) {
            tagCompound.removeTag("pixels");
        } else {
            tagCompound.setString("pixels", pixels);
        }
        stack.setTagCompound(tagCompound);
    }

    public String getPixels(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("pixels") ||
                stack.getTagCompound().getString("pixels").isEmpty()) return null;
        return stack.getTagCompound().getString("pixels");
    }

    public void setPixels(EntityPlayer player, String pixels) {
        if (pixels == null) {
            player.getEntityData().removeTag("CapePixels");
        } else {
            player.getEntityData().setString("CapePixels", pixels);
        }
        player.writeEntityToNBT(player.getEntityData());
    }

    public String getPixels(EntityPlayer player) {
        return player.getEntityData().hasKey("CapePixels") ? player.getEntityData().getString("CapePixels") : null;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        setUniqueID(stack);
    }

    public void setUniqueID(ItemStack stack) {
        NBTTagCompound tagCompound = !stack.hasTagCompound() ? new NBTTagCompound() : stack.getTagCompound();
        tagCompound.setString("uuid", UUID.randomUUID().toString());
        stack.setTagCompound(tagCompound);
    }

    public UUID getUniqueID(ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("uuid") ||
                stack.getTagCompound().getString("uuid").isEmpty()) return null;
        return UUID.fromString(stack.getTagCompound().getString("uuid"));
    }
}