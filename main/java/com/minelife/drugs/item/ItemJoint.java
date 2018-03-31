package com.minelife.drugs.item;

import com.minelife.Minelife;
import com.minelife.drugs.DetectableHempEffect;
import com.minelife.drugs.XRayEffect;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJoint extends Item {

    public ItemJoint() {
        setRegistryName(Minelife.MOD_ID, "joint");
        setUnlocalizedName(Minelife.MOD_ID + ":joint");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.SERVER)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.addPotionEffect(new PotionEffect(XRayEffect.INSTANCE, 90 * 20, 0, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 60 * 20, 0, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 60 * 20, 0, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60 * 20, 1, false, false));
        playerIn.addPotionEffect(new PotionEffect(DetectableHempEffect.INSTANCE, 1 *(20 * (60 * 20)) , 0, false, false));
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
