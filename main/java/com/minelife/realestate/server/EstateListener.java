package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.realestate.*;
import com.minelife.util.BreakHelper;
import com.minelife.util.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class EstateListener {

    private static Map<EntityPlayerMP, Estate> PLAYERS_ESTATE = Maps.newHashMap();

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        if (estate == null) {
            if (PLAYERS_ESTATE.containsKey(player)) {
                if (PLAYERS_ESTATE.get(player).getOutro() != null && !PLAYERS_ESTATE.get(player).getOutro().trim().isEmpty())
                    player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(PLAYERS_ESTATE.get(player).getOutro(), '&')));
                PLAYERS_ESTATE.remove(player);
            }
            return;
        }

        if (!PLAYERS_ESTATE.containsKey(player)) {
            if (estate.getIntro() != null && !estate.getIntro().trim().isEmpty())
                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(estate.getIntro(), '&')));
            PLAYERS_ESTATE.put(player, estate);
            return;
        }

        if (PLAYERS_ESTATE.get(player) != estate) {
            if (PLAYERS_ESTATE.get(player).getOutro() != null && !PLAYERS_ESTATE.get(player).getOutro().trim().isEmpty())
                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(PLAYERS_ESTATE.get(player).getOutro(), '&')));
            if (estate.getIntro() != null && !estate.getIntro().trim().isEmpty())
                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(estate.getIntro(), '&')));
            PLAYERS_ESTATE.put(player, estate);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());

        if (estate == null) return;
        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.BREAK)) return;

        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to build here."));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        if(event.getPlacedBlock().getBlock() == Blocks.PISTON || event.getPlacedBlock().getBlock() == Blocks.STICKY_PISTON) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Pistons are disabled due to the lag they may cause in a server."));
            event.setCanceled(true);
        }

        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());

        if (estate == null) return;
        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.PLACE)) return;

        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to build here."));
        event.setCanceled(true);
    }

//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
//        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
//        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());
//
//        if (estate == null) return;
//
//        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.INTERACT)) return;
//
//        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to interact here."));
//        event.setCanceled(true);
//    }

    // TODO: Add fire spread protection
    // TODO: cannot interact with cars, but can interact with containers

    @SubscribeEvent
    public void onHit(LivingAttackEvent event) {
        if(event.getEntityLiving() instanceof EntityJobNPC || event.getEntityLiving() instanceof EntityReceptionist) {
            event.setCanceled(true);
            return;
        }

        Estate estate = ModRealEstate.getEstateAt(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosition());

        if (estate == null) return;

        if (event.getEntityLiving() instanceof EntityMob) {
            if (!estate.getProperties().contains(EstateProperty.MOB_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (!estate.getProperties().contains(EstateProperty.ANIMAL_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }


        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!estate.getProperties().contains(EstateProperty.PLAYER_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                if (!estate.getProperties().contains(EstateProperty.PVP)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        Estate estate = ModRealEstate.getEstateAt(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosition());

        if (estate == null) return;

        if (event.getEntityLiving() instanceof EntityMob) {
            if (!estate.getProperties().contains(EstateProperty.MOB_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (!estate.getProperties().contains(EstateProperty.ANIMAL_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }


        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!estate.getProperties().contains(EstateProperty.PLAYER_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                if (!estate.getProperties().contains(EstateProperty.PVP)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        Estate estate = ModRealEstate.getEstateAt(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosition());

        if (estate == null) return;

        if (event.getEntityLiving() instanceof EntityMob) {
            if (!estate.getProperties().contains(EstateProperty.MOB_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (!estate.getProperties().contains(EstateProperty.ANIMAL_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }


        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!estate.getProperties().contains(EstateProperty.PLAYER_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                if (!estate.getProperties().contains(EstateProperty.PVP)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKnockback(LivingKnockBackEvent event) {
        Estate estate = ModRealEstate.getEstateAt(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosition());

        if (estate == null) return;

        if (event.getEntityLiving() instanceof EntityMob) {
            if (!estate.getProperties().contains(EstateProperty.MOB_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getEntityLiving() instanceof EntityAnimal) {
            if (!estate.getProperties().contains(EstateProperty.ANIMAL_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
        }


        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!estate.getProperties().contains(EstateProperty.PLAYER_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
            if (event.getAttacker() instanceof EntityPlayer) {
                if (!estate.getProperties().contains(EstateProperty.PVP)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        Estate estate = ModRealEstate.getEstateAt(event.getEntity().getEntityWorld(), event.getEntity().getPosition());

        if (estate == null) return;

        if (event.getEntity() instanceof EntityAnimal) {
            if (!estate.getProperties().contains(EstateProperty.ANIMAL_SPAWN)) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getEntity() instanceof EntityMob) {
            if (!estate.getProperties().contains(EstateProperty.MOB_SPAWN)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().clear();

        Map<BlockPos, Integer> explosionMap = BreakHelper.getAffectedBlocksFromExplosion(event.getWorld(),
                event.getExplosion().getPosition(), event.getExplosion().getExplosivePlacedBy(), 2.0F, 10);

        for (BlockPos blockPos : explosionMap.keySet()) {

            Estate estate = ModRealEstate.getEstateAt(event.getWorld(), new BlockPos(event.getExplosion().getPosition()));

            if (estate == null || estate.getProperties().contains(EstateProperty.EXPLOSIONS)) {
                if (event.getWorld().getBlockState(blockPos).getBlock() != Blocks.AIR) {

                    BreakHelper.BreakProgress breakProgress = BreakHelper.get(blockPos, event.getWorld());

                    if (breakProgress == null)
                        breakProgress = BreakHelper.create(blockPos, event.getWorld(), explosionMap.get(blockPos));
                    else
                        breakProgress.setProgress(breakProgress.getUncalculatedProgress() + explosionMap.get(blockPos));

                    if (breakProgress.getUncalculatedProgress() >= 240.0F) {
                        event.getAffectedBlocks().add(blockPos);
                        BreakHelper.remove(breakProgress);
                    } else {
                        breakProgress.sendProgress();
                    }
                }
            }
        }

    }

//    @SubscribeEvent
//    public void ic2Explosion(ic2.api.event.ExplosionEvent event) {
//        event.setCanceled(true);
//
//        event.getWorld().createExplosion(event.entity, event.pos.x, event.pos.y, event.pos.z, 4.0F, true);
//    }

}
