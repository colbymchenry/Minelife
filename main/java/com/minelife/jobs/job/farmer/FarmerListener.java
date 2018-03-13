package com.minelife.jobs.job.farmer;

import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.util.Color;
import com.minelife.util.FireworkBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class FarmerListener {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        World world = event.world;

        if(!(event.world.getBlock(event.x, event.y, event.z) instanceof BlockCrops)) return;

        if(!FarmerHandler.isFarmer(player)) return;

        Estate estate = EstateHandler.getEstateAt(event.world, event.x, event.y, event.z);
        boolean inFarmZone = estate != null && FarmerHandler.config.getStringList("zones").contains(String.valueOf(estate.getID()));

        int level = FarmerHandler.getLevel(player);

        // auto replant
        if(inFarmZone) {
            if( event.world.getBlockMetadata(event.x, event.y, event.z) == 0) {
                event.setCanceled(true);
                return;
            }

            if(FarmerHandler.replant(player)) {
                world.setBlock(event.x, event.y, event.z, event.block);
                world.setBlockMetadataWithNotify(event.x, event.y, event.z, FarmerHandler.getGrowthStage(player), 2);
            } else {
                world.setBlock(event.x, event.y, event.z, event.block);
            }
        } else if(FarmerHandler.replant(player)) {
            world.setBlock(event.x, event.y, event.z, event.block);
            world.setBlockMetadataWithNotify(event.x, event.y, event.z, FarmerHandler.getGrowthStage(player), 2);
        }

        if(FarmerHandler.getLevel(player) > level) {
            world.playSoundAtEntity(player, Minelife.MOD_ID + ":level_up", 1.0f, 1.0f);
            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.BLUE.asRGB()}, new int[]{Color.PURPLE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.worldObj, player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.worldObj.spawnEntityInWorld(ent);

            ModEssentials.sendTitle(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD.toString() + "Level Up!",
                    EnumChatFormatting.YELLOW + "New Level: " + EnumChatFormatting.BLUE + FarmerHandler.getLevel(player), 5, player);
        }
    }

}
