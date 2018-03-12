package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.permission.ModPermission;
import com.minelife.realestate.*;
import com.minelife.util.StringHelper;
import com.minelife.util.server.BlockBreakFix;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EstateListener {

    public static Map<EntityPlayer, Estate> insideEstate = Maps.newHashMap();

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;


        Estate estate = EstateHandler.getEstateAt(player.worldObj, player.posX, player.posY, player.posZ);

        // leaving estate
        if (estate == null && insideEstate.containsKey(player)) {
            estate = insideEstate.get(player);
            insideEstate.remove(player);
            if (!estate.getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(StringHelper.ParseFormatting(estate.getOutro(), '&')));
            return;
        }

        if (estate != null && insideEstate.containsKey(player) && !insideEstate.get(player).equals(estate)) {
            if (!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.ENTER)) {
                if (ModPermission.hasPermission(player.getUniqueID(), "estate.override.enter")) return;
                handleEntrance(estate, player);
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to enter this estate."));
                return;
            }

            if (!insideEstate.get(player).getOutro().trim().isEmpty())
                player.addChatComponentMessage(new ChatComponentText(StringHelper.ParseFormatting(insideEstate.get(player).getOutro(), '&')));
            if (!estate.getIntro().trim().isEmpty()) {
                player.addChatComponentMessage(new ChatComponentText(StringHelper.ParseFormatting(estate.getIntro(), '&')));
            }

            if (estate.isForRent() || estate.isPurchasable()) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Estate is for " + (estate.isForRent() && estate.isPurchasable() ? "rent and sale!" : estate.isForRent() && !estate.isPurchasable() ? "rent!" : "sale!")));
            }
        }

        // entering estate
        if (estate != null) {
            if (!insideEstate.containsKey(player)) {
                if (!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.ENTER)) {
                    if (ModPermission.hasPermission(player.getUniqueID(), "estate.override.enter")) return;
                    handleEntrance(estate, player);
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to enter this estate."));
                    return;
                }

                insideEstate.put(player, estate);
                if (!estate.getIntro().trim().isEmpty()) {
                    player.addChatComponentMessage(new ChatComponentText(StringHelper.ParseFormatting(estate.getIntro(), '&')));
                }

                if (estate.isForRent() || estate.isPurchasable()) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Estate is for " + (estate.isForRent() && estate.isPurchasable() ? "rent and sale!" : estate.isForRent() && !estate.isPurchasable() ? "rent!" : "sale!")));
                }


            } else {
                if (!insideEstate.get(player).equals(estate)) {
                    insideEstate.put(player, estate);
                }
            }
        }
    }

    @SideOnly(Side.SERVER)
    private void handleEntrance(Estate estate, EntityPlayerMP player) {
        double distanceMinX = player.posX - estate.getBounds().minX;
        double distanceMaxX = estate.getBounds().maxX - player.posX;
        double distanceMinZ = player.posZ - estate.getBounds().minZ;
        double distanceMaxZ = estate.getBounds().maxZ - player.posZ;

        if (player.posX - 1 > estate.getBounds().minX && player.posX + 1 < estate.getBounds().maxX) {
            if (distanceMinZ < distanceMaxZ) {
                player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, estate.getBounds().minZ - 1, player.rotationYaw, player.rotationPitch);
            } else if (distanceMinZ > distanceMaxZ) {
                player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, estate.getBounds().maxZ + 1, player.rotationYaw, player.rotationPitch);
            }
        } else if (player.posZ > estate.getBounds().minZ && player.posZ < estate.getBounds().maxZ) {
            if (distanceMinX < distanceMaxX) {
                player.playerNetServerHandler.setPlayerLocation(estate.getBounds().minX - 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            } else if (distanceMinX > distanceMaxX) {
                player.playerNetServerHandler.setPlayerLocation(estate.getBounds().maxX + 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            }
        }
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        long start = System.currentTimeMillis();
        Estate estate = EstateHandler.getEstateAt(player.worldObj, event.x, event.y, event.z);

        if (estate == null) return;

        if(ModPermission.hasPermission(player.getUniqueID(), "estate.override.break")) return;

        event.setCanceled(!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.BREAK));
        BlockBreakFix.onBreak(event);
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Estate estate = EstateHandler.getEstateAt(player.worldObj,event.x, event.y, event.z);

        if (estate == null) return;

        if(ModPermission.hasPermission(player.getUniqueID(), "estate.override.place")) return;

        event.setCanceled(!estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.PLACE));
    }

    // TODO: Implement not needing interact as it is enabled for everything unless locked.
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
        Estate estate = EstateHandler.getEstateAt(player.worldObj,event.x, event.y, event.z);
        if (estate == null) return;
        if(ModPermission.hasPermission(player.getUniqueID(), "estate.override.interact")) return;
        Block block = event.world.getBlock(event.x, event.y, event.z);

        if (((block instanceof BlockContainer) || ServerProxy.config.getIntegerList("black-listed-blocks").contains(Block.getIdFromBlock(block))) &&
                !estate.getPlayerPermissions(player.getUniqueID()).contains(Permission.INTERACT)) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not allowed to interact within this region."));
            event.setCanceled(true);
            if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                BlockBreakFix.onBreak(new BlockEvent.BreakEvent(event.x, event.y, event.z, event.world, block, event.world.getBlockMetadata(event.x, event.y, event.z), event.entityPlayer));
            }
        }
    }

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.world, event.entity.posX, event.entity.posY, event.entity.posZ);
        if (estate == null) return;
        if (event.entity instanceof EntityMob || event.entity instanceof IMob)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.MONSTER_SPAWN));
        else if (event.entity instanceof EntityAnimal)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.CREATURE_SPAWN));
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ);
        if (estate == null) return;

        if (event.entity instanceof EntityPlayer)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PVP));
        else
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PVE));
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ);
        if (estate == null) return;
        event.setCanceled(!estate.getEstatePermissions().contains(Permission.FALL_DAMAGE));
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ);
        if (estate == null) return;
        event.setCanceled(!estate.getEstatePermissions().contains(Permission.HEAL));
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        Estate estate = EstateHandler.getEstateAt(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ);
        if (estate == null) return;
        if (event.entity instanceof EntityMob)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.MONSTER_DEATH));
        else if (event.entity instanceof EntityAnimal)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.CREATURE_DEATH));
        else if (event.entity instanceof EntityPlayer)
            event.setCanceled(!estate.getEstatePermissions().contains(Permission.PLAYER_DEATH));
    }

    // TODO: Implement dynamite breaking one block for raiding
    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        List<ChunkPosition> toRemove = Lists.newArrayList();
        event.getAffectedBlocks().forEach(cP -> {
            Estate estate = EstateHandler.getEstateAt(event.world, cP.chunkPosX, cP.chunkPosY, cP.chunkPosZ);
            if (estate != null) {
                if (!estate.getEstatePermissions().contains(Permission.EXPLOSION)) {
                    toRemove.add(cP);
                } else {
                    if (event.explosion.getExplosivePlacedBy() instanceof EntityPlayerMP) {
                        if (!estate.getPlayerPermissions(((EntityPlayer) event.explosion.getExplosivePlacedBy()).getUniqueID()).contains(Permission.BREAK)) {
                            toRemove.add(cP);
                        }
                    }
                }
            }
        });

        event.getAffectedBlocks().removeAll(toRemove);
    }

//    @SubscribeEvent
//    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
//        EstateHandler.loadedEstates.forEach(e -> {
//            if (e.getRenter() != null && e.getRenter().equals(event.player.getUniqueID())) {
//                if (e.getBill() != null) {
//                    long diff = e.getBill().getDueDate().getTime() - Calendar.getInstance().getTime().getTime();
//                    if (e.getBill().getAmountDue() > 0) {
//                        RentDueNotification notification = new RentDueNotification(event.player.getUniqueID(), (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS), e.getID(), e.getBill().getAmountDue());
//                        notification.sendTo((EntityPlayerMP) event.player);
//                    }
//                }
//            }
//        });
//    }

}
