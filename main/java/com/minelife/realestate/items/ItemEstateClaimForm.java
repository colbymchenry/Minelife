package com.minelife.realestate.items;

import com.minelife.Minelife;
import com.minelife.realestate.client.renderer.SelectionRenderer;
import com.minelife.realestate.util.PlayerUtil;
import com.minelife.util.Vector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEstateClaimForm extends Item {

    public ItemEstateClaimForm() {
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("estate_claim_form");
        this.setTextureName(Minelife.MOD_ID + ":estate_claim_form");
        this.setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Estate Claim Form";
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        if (entityLiving.worldObj.isRemote) {
            // Left Click
            SelectionRenderer.setStart(PlayerUtil.getBlockCoordinatesInFocus());
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
        if (player.worldObj.isRemote) {
            // Right Click
            SelectionRenderer.setEnd(new Vector(x, y, z));
        }
        return super.onItemUse(stack, player, world, x, y, z, side, px, py, pz);
    }

}