package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.EntityDynamite;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

public class ItemDynamite extends ItemBow {

    public ItemDynamite() {
        setRegistryName(Minelife.MOD_ID, "dynamite");
        setUnlocalizedName(Minelife.MOD_ID + ":dynamite");
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityDynamite dynamite = new EntityDynamite(worldIn);
            dynamite.getEntityData().setBoolean("client", worldIn.isRemote);

            dynamite.setPosition(entityLiving.posX + ((entityLiving.getLookVec().x + entityLiving.motionX) * 1.2),
                    entityLiving.posY + entityLiving.getEyeHeight() + (entityLiving.getLookVec().y + entityLiving.motionY),
                    entityLiving.posZ + (entityLiving.getLookVec().z + entityLiving.motionZ) * 1.2);

            boolean flag = ((EntityPlayer) entityLiving).capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer) entityLiving, i, !stack.isEmpty() || flag);

            dynamite.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0, getArrowVelocity(i) * 1.4F, 1);

            worldIn.spawnEntity(dynamite);
        }
    }

    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":dynamite", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    public void registerRecipe() {

    }

}
