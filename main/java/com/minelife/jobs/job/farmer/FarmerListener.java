package com.minelife.jobs.job.farmer;

import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmerListener {

    private static final ResourceLocation levelUpSFX = new ResourceLocation(Minelife.MOD_ID, "level_up");

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        World world = event.getWorld();

        if(!(world.getBlockState(event.getPos()) instanceof BlockCrops)) return;

        if(!FarmerHandler.isFarmer(player)) return;

        Estate estate = ModRealEstate.getEstateAt(world, event.getPos());
        boolean inFarmZone = estate != null && FarmerHandler.config.getStringList("zones").contains(String.valueOf(estate.getUniqueID()));

        int level = FarmerHandler.getLevel(player);
        BlockCrops blockCrop = (BlockCrops) world.getBlockState(event.getPos());

        // auto replant
        if(inFarmZone) {
            if(blockCrop.getMaxAge() < world.getBlockState(event.getPos()).getValue(BlockCrops.AGE)) {
                event.setCanceled(true);
                return;
            }

            if(FarmerHandler.replant(player)) {
                world.setBlockState(event.getPos(), blockCrop.getDefaultState().withProperty(BlockCrops.AGE, FarmerHandler.getGrowthStage(player)), 2);
            } else {
                world.setBlockState(event.getPos(), blockCrop.getDefaultState(), 2);
            }
        } else if(FarmerHandler.replant(player)) {
            world.setBlockState(event.getPos(), blockCrop.getDefaultState().withProperty(BlockCrops.AGE, FarmerHandler.getGrowthStage(player)), 2);
        }

        if(FarmerHandler.getLevel(player) > level) {
            world.playSound(event.getPlayer(), event.getPos(), new SoundEvent(levelUpSFX), SoundCategory.MASTER, 1.0F, 1.0F);
            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.PURPLE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);

            ModEssentials.sendTitle(TextFormatting.YELLOW.toString() + TextFormatting.BOLD.toString() + "Level Up!",
                    TextFormatting.YELLOW + "New Level: " + TextFormatting.BLUE + FarmerHandler.getLevel(player), 5, player);
        }
    }

}
