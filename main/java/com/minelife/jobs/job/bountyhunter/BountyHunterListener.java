package com.minelife.jobs.job.bountyhunter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BountyHunterListener {

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(!(event.getEntityLiving() instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if(CommandBounty.getBounties(player.getUniqueID()).isEmpty()) return;
        ItemStack bountyCard = new ItemStack(ItemBountyCard.INSTANCE);
        ItemBountyCard.setTarget(player.getUniqueID(), bountyCard);
        player.dropItem(bountyCard, false);
    }

}
