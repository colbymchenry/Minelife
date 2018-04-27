package com.minelife.jobs.job.bountyhunter;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BountyHunterListener {

    public static Map<UUID, Long> playerDeaths = Maps.newHashMap();

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(!(event.getEntityLiving() instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if(CommandBounty.getBounties(player.getUniqueID()).isEmpty()) return;
        ItemStack bountyCard = new ItemStack(ItemBountyCard.INSTANCE);
        ItemBountyCard.setTarget(player.getUniqueID(), bountyCard);
        player.dropItem(bountyCard, false);

        playerDeaths.put(player.getUniqueID(), System.currentTimeMillis() + 600000L);
    }

}
