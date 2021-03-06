package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.essentials.TeleportHandler;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.realestate.*;
import com.minelife.util.BreakHelper;
import com.minelife.util.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
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
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class EstateListener {

    private static Map<EntityPlayerMP, Estate> PLAYERS_ESTATE = Maps.newHashMap();

//    @SubscribeEvent
//    public void playerTick(TickEvent.PlayerTickEvent event) {
//        EntityPlayerMP player = (EntityPlayerMP) event.player;
//        Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());
//
//        if (estate == null) {
//            if (PLAYERS_ESTATE.containsKey(player)) {
//                if (PLAYERS_ESTATE.get(player).getOutro() != null && !PLAYERS_ESTATE.get(player).getOutro().trim().isEmpty())
//                    player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(PLAYERS_ESTATE.get(player).getOutro(), '&')));
//                PLAYERS_ESTATE.remove(player);
//            }
//            return;
//        }
//
//        if (!PLAYERS_ESTATE.containsKey(player)) {
//            if (estate.getIntro() != null && !estate.getIntro().trim().isEmpty())
//                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(estate.getIntro(), '&')));
//            PLAYERS_ESTATE.put(player, estate);
//            return;
//        }
//
//        if (PLAYERS_ESTATE.get(player) != estate) {
//            if (PLAYERS_ESTATE.get(player).getOutro() != null && !PLAYERS_ESTATE.get(player).getOutro().trim().isEmpty())
//                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(PLAYERS_ESTATE.get(player).getOutro(), '&')));
//            if (estate.getIntro() != null && !estate.getIntro().trim().isEmpty())
//                player.sendMessage(new TextComponentString(StringHelper.ParseFormatting(estate.getIntro(), '&')));
//            PLAYERS_ESTATE.put(player, estate);
//        }
//    }

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
    public void onFill(FillBucketEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if(event.getTarget() == null || event.getTarget().getBlockPos() == null) return;
        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getTarget().getBlockPos());

        if (estate == null) return;
        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.BREAK)) return;

        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to build here."));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEvent(BlockEvent event) {
        if(event.getState().getBlock() instanceof BlockPistonBase) {
            if(event.getState().getValue(BlockPistonBase.EXTENDED)) {
                event.getWorld().setBlockToAir(event.getPos().add(0, 1, 0));
                event.getWorld().setBlockState(event.getPos(), event.getState().withProperty(BlockPistonBase.EXTENDED, false));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        if (event.getPlacedBlock().getBlock().getRegistryName().toString().contains("mirror")) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Mirrors are disabled on this server."));
            event.setCanceled(true);
            return;
        }

        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());

        if (estate == null) return;
        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.PLACE)) return;

        event.setCanceled(true);

        if (player.posY > event.getPos().getY() && player.getPosition().getX() == event.getPos().getX() && player.getPosition().getZ() == event.getPos().getZ()) {
            for (int i = 0; i < 64; i++) {
                BlockPos pos = event.getPos().add(0, -i, 0);
                if (player.world.getBlockState(pos).getBlock() != Blocks.AIR) {
                    player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    break;
                }
            }
        }

        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to build here."));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        Estate estate = ModRealEstate.getEstateAt(event.getWorld(), event.getPos());

        if (estate == null) return;
        if (estate.getPlayerPermissions(player.getUniqueID()).contains(PlayerPermission.INTERACT)) return;

        player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " You are " + TextFormatting.RED + "not" + TextFormatting.GOLD + " authorized to interact here."));
        event.setCanceled(true);
    }

    // TODO: Add fire spread protection
    // TODO: cannot interact with cars, but can interact with containers

    @SubscribeEvent
    public void onHit(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityJobNPC || event.getEntityLiving() instanceof EntityReceptionist) {
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

}
