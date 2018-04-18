package com.minelife.jobs.job.bountyhunter;

import com.minelife.economy.ModEconomy;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.EnumJob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

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

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if(event.getTarget() instanceof EntityJobNPC) {
            EntityJobNPC jobNPC = (EntityJobNPC) event.getTarget();
            if(jobNPC.getProfession() == EnumJob.BOUNTY_HUNTER.ordinal()) {
                if(event.getItemStack().getItem() == ItemBountyCard.INSTANCE) {
                    Map<String, Integer> bounties = CommandBounty.getBounties(ItemBountyCard.getTarget(event.getItemStack()));

                    int total = 0;
                    for (Integer integer : bounties.values()) total += integer;

                    ModEconomy.depositATM(event.getEntityPlayer().getUniqueID(), total, true);
                    CommandBounty.removeBounty(ItemBountyCard.getTarget(event.getItemStack()));

                    event.getEntityPlayer().setHeldItem(event.getHand(), ItemStack.EMPTY);
                    ((EntityPlayerMP) event.getEntityPlayer()).inventoryContainer.detectAndSendChanges();
                }
            }
        }
    }

}
