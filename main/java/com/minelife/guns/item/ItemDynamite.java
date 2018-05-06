package com.minelife.guns.item;

import com.minelife.Minelife;
import com.minelife.guns.EntityDynamite;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemDynamite extends ItemBow {

    public ItemDynamite() {
        setRegistryName(Minelife.MOD_ID, "dynamite");
        setUnlocalizedName(Minelife.MOD_ID + ":dynamite");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(64);
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

            EntityPlayer player = (EntityPlayer) entityLiving;

            stack.shrink(1);

            if (stack.isEmpty()) player.inventory.deleteStack(stack);

            player.inventoryContainer.detectAndSendChanges();

            worldIn.spawnEntity(dynamite);
        }
    }


    /**
     * Called when the equipped item is right clicked.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }


    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":dynamite", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    public void registerRecipe() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":dynamite");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(this, 2), Ingredient.fromItem(Item.getItemFromBlock(Blocks.TNT)), Ingredient.fromItem(Items.STRING));
    }

}
