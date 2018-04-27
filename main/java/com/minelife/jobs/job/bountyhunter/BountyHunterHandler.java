package com.minelife.jobs.job.bountyhunter;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.NPCHandler;
import com.minelife.jobs.job.SellingOption;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.server.CommandJob;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;

import javax.xml.soap.Text;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class BountyHunterHandler extends NPCHandler {

    public static final BountyHunterHandler INSTANCE = new BountyHunterHandler();

    private BountyHunterHandler() {
        super("bountyhunter");
    }

    @Override
    public void onEntityRightClick(EntityPlayer player) {
        if(player.world.isRemote) return;

        if(!isProfession((EntityPlayerMP) player)) {
            Minelife.getNetwork().sendTo(new PacketOpenSignupGui(EnumJob.BOUNTY_HUNTER), (EntityPlayerMP) player);
            return;
        }

        if(player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemBountyCard.INSTANCE) {
            ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);

            if(ItemBountyCard.getTarget(itemStack) == null) {
                CommandJob.sendMessage(player, EnumJob.BOUNTY_HUNTER, TextFormatting.RED + "There is no player bound to that bounty card.");
                return;
            }

            Map<String, Integer> bounties = CommandBounty.getBounties(ItemBountyCard.getTarget(itemStack));

            if(bounties.isEmpty()) {
                CommandJob.sendMessage(player, EnumJob.BOUNTY_HUNTER, TextFormatting.RED + "There are no bounties out for that player.");
                return;
            }

            int total = 0;
            for (Integer integer : bounties.values()) total += integer;

            ModEconomy.depositATM(player.getUniqueID(), total, true);
            CommandBounty.removeBounty(ItemBountyCard.getTarget(itemStack));

           player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            ((EntityPlayerMP) player).inventoryContainer.detectAndSendChanges();
        } else {
            CommandJob.sendMessage(player, EnumJob.BOUNTY_HUNTER, TextFormatting.RED + "You need to hold a bounty card in your main hand.");
        }
    }

    @Override
    public void joinProfession(EntityPlayer player) {
        if(isProfession((EntityPlayerMP) player)) {
            CommandJob.sendMessage(player, EnumJob.BOUNTY_HUNTER, TextFormatting.RED + "You are already a bounty hunter.");
            return;
        }

        try {
            ModJobs.getDatabase().query("INSERT INTO bountyhunter (playerID, xp) VALUES ('" + player.getUniqueID().toString() + "', '0')");

            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.ORANGE.asRGB(), Color.GRAY.asRGB()}, new int[]{Color.SILVER.asRGB(), Color.RED.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent);
            EntityFireworkRocket ent1 = new EntityFireworkRocket(player.getEntityWorld(), player.posX, player.posY + 2, player.posZ, fireworkStack);
            player.getEntityWorld().spawnEntity(ent1);
        } catch (SQLException e) {
            e.printStackTrace();
            CommandJob.sendMessage(player, EnumJob.BOUNTY_HUNTER, TextFormatting.RED + "Something went wrong. Notify an admin.");
        }
    }

    @Override
    public List<SellingOption> getSellingOptions() {
        return null;
    }

    @Override
    public void setupConfig() {

    }
}
