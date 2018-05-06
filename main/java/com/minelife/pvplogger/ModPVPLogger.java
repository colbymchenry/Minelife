package com.minelife.pvplogger;

import com.google.common.collect.Maps;
import com.minelife.MLMod;
import com.minelife.Minelife;
import com.minelife.essentials.server.EventTeleport;
import com.minelife.realestate.EntityReceptionist;
import com.minelife.realestate.client.RenderEntityReceptionist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class ModPVPLogger extends MLMod {

    public static Map<UUID, Long> damageMap = Maps.newHashMap();
    public static Map<UUID, EntityPlayerTracker> playerTrackers = Maps.newHashMap();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "player_tracker"), EntityPlayerTracker.class, "player_tracker", 3, Minelife.getInstance(), 77, 1, true);
        if (event.getSide() == Side.SERVER)
            MinecraftForge.EVENT_BUS.register(this);
        else
            RenderingRegistry.registerEntityRenderingHandler(EntityPlayerTracker.class, RenderEntityPlayerTracker::new);
    }

    @SubscribeEvent
    public void onTeleport(EventTeleport event) {
        if(ModPVPLogger.damageMap.containsKey(event.getPlayer().getUniqueID())) {
            if (System.currentTimeMillis() < ModPVPLogger.damageMap.get(event.getPlayer().getUniqueID()) + (1000L * 20)) {
                event.setCanceled(true);
                int timeLeft = (int) (((ModPVPLogger.damageMap.get(event.getPlayer().getUniqueID()) + (1000L * 20)) - System.currentTimeMillis()) / 1000L);
                event.getPlayer().sendMessage(new TextComponentString(TextFormatting.GOLD + "You were attacked recently! Please wait " + TextFormatting.RED + timeLeft + TextFormatting.GOLD + " seconds."));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingDamageEvent event) {
        if (event.isCanceled()) return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            if(event.getSource().getImmediateSource() instanceof EntityPlayer) {
                damageMap.put(((EntityPlayer) event.getEntityLiving()).getUniqueID(), System.currentTimeMillis());
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerTracker)) return;


        EntityPlayerTracker playerTracker = (EntityPlayerTracker) event.getEntityLiving();
        if (!playerTrackers.containsKey(playerTracker.getPlayerID())) return;

        if(playerTracker.getEntityData().hasKey("DiedNaturally")) return;

        for (int i = 0; i < playerTracker.getInventory().getSizeInventory(); i++) {
            if(playerTracker.getInventory().getStackInSlot(i) != ItemStack.EMPTY) {
                playerTracker.entityDropItem(playerTracker.getInventory().getStackInSlot(i), 0.5f);
            }
        }

        File file = new File(System.getProperty("user.dir") + "/world/playerdata", playerTracker.getPlayerID().toString() + ".dat");
        file.delete();
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!damageMap.containsKey(event.player.getUniqueID())) return;

        if (System.currentTimeMillis() - damageMap.get(event.player.getUniqueID()) > (1000L * 20)) {
            damageMap.remove(event.player.getUniqueID());
            return;
        }


        EntityPlayerTracker playerTracker = new EntityPlayerTracker(event.player.getEntityWorld(), event.player);
        playerTracker.setPosition(event.player.posX, event.player.posY, event.player.posZ);
        playerTracker.rotationYaw = event.player.rotationYaw;
        playerTracker.renderYawOffset = event.player.rotationYaw;
        playerTracker.rotationPitch = event.player.rotationPitch;
        event.player.getEntityWorld().spawnEntity(playerTracker);
        playerTrackers.put(event.player.getUniqueID(), playerTracker);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!playerTrackers.containsKey(event.player.getUniqueID())) return;
        EntityPlayerTracker playerTracker = playerTrackers.get(event.player.getUniqueID());

        event.player.setPositionAndUpdate(playerTracker.posX, playerTracker.posY, playerTracker.posZ);
        event.player.rotationYaw = playerTracker.rotationYaw;
        event.player.rotationPitch = playerTracker.rotationPitch;
        playerTracker.setDead();
        playerTrackers.remove(event.player.getUniqueID());
    }

}
