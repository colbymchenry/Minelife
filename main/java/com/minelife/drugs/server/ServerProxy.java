package com.minelife.drugs.server;

import com.minelife.MLProxy;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        IBlockState blockState = event.getState();

        if(blockState.getBlock() == Blocks.TALLGRASS) {
            if(MathHelper.nextDouble(event.getWorld().rand, 0, 100) > 99) {
                EntityItem entityItem = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY() + 0.5, event.getPos().getZ(), new ItemStack(ModDrugs.itemHempSeed, 2));
                event.getWorld().spawnEntity(entityItem);
            }
            if(MathHelper.nextDouble(event.getWorld().rand, 0, 100) > 99.5) {
                EntityItem entityItem = new EntityItem(event.getWorld(), event.getPos().getX(), event.getPos().getY() + 0.5, event.getPos().getZ(), new ItemStack(ModDrugs.itemCocaSeed, 2));
                event.getWorld().spawnEntity(entityItem);
            }
        }
    }

}
