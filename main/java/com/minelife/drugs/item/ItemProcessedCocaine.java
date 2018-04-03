package com.minelife.drugs.item;

import com.minelife.Minelife;
import com.minelife.drugs.DetectableCocaineEffect;
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

public class ItemProcessedCocaine extends Item {

    public ItemProcessedCocaine() {
        setRegistryName(Minelife.MOD_ID, "processed_cocaine");
        setUnlocalizedName(Minelife.MOD_ID + ":processed_cocaine");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.SERVER)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 300, 3, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20 * 300, 2, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 300, 1, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 300, 1, false, false));
        playerIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 300, 0, false, false));
        // 60 * 20 = 1 minute, 20 * 60 * 20 = 1 mc day, 7 * 20 * 60 * 20 = 7 mc days
        playerIn.addPotionEffect(new PotionEffect(DetectableCocaineEffect.INSTANCE, 1 *(20 * (60 * 20)) , 0, false, false));

        if(playerIn.getHeldItem(handIn).getCount() == 1) playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        else {
            ItemStack stack = playerIn.getHeldItem(handIn).copy();
            stack.setCount(stack.getCount() - 1);
            playerIn.setHeldItem(handIn, stack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
