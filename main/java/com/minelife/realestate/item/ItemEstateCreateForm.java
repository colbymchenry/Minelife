package com.minelife.realestate.item;

import com.minelife.Minelife;
import com.minelife.realestate.client.renderer.SelectionRenderer;
import com.minelife.realestate.client.util.PlayerUtil;
import com.minelife.util.Vector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEstateCreateForm extends Item {

    public ItemEstateCreateForm() {
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("Estate Claiming Tool");
        this.setTextureName(Minelife.MOD_ID + ":estate_create_form");
        this.setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        return "Estate Creation Form";
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (entityLiving.worldObj.isRemote) {
            Vector point = PlayerUtil.getBlockCoordinatesInFocus();
            SelectionRenderer.setStart(point);
        }
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {

        if (world.isRemote) {

            Vector point = new Vector(x, y, z);

            SelectionRenderer.setEnd(point);

        }

        return super.onItemUse(itemstack, player, world, x, y, z, side, px, py, pz);

    }

}