package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.guns.ModGuns;
import com.minelife.police.server.Prison;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.render.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AICopPolice extends EntityAIBase {

    private double moveSpeed = 1.8;
    private int followRange = 20, arrestRange = 2, tazeRange = 6;
    private int pathAttempt = 0;
    private EntityCop entity;
    private List<ChargeType> chargesForTarget = Lists.newArrayList();
    private long lastPlayerTazed;

    public AICopPolice(EntityCop cop) {
        // TODO: Add the canSee from the SkeletonAI
        this.entity = cop;
        setMutexBits(7);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.entity.getAttackTarget() != null || hasArrestedPlayer()) {
            // if does not have arrested player go to player that we are attempting to arrest
            if (!hasArrestedPlayer() && !isTargetArrested()) {
                this.entity.getNavigator().tryMoveToXYZ(this.entity.getAttackTarget().posX, this.entity.getAttackTarget().posY, this.entity.getAttackTarget().posZ, moveSpeed);

                // taze an aggressive player and don't go further and arrest
                if (Objects.equals(entity.getAttackTarget().getUniqueID(), getAggressivePlayer())
                        && !Objects.equals(entity.getAttackTarget().getUniqueID(), getKillerPlayer())) {
                    if (this.entity.getDistance(this.entity.getAttackTarget()) < tazeRange) {
                        ModPolice.setUnconscious((EntityPlayer) entity.getAttackTarget(), true);
                        playTazerSound();
                        setAggressivePlayer(null);
                        entity.setAttackTarget(null);
                    }
                    return true;
                }

                if (this.entity.getDistance(this.entity.getAttackTarget()) < arrestRange) {
                    this.entity.getAttackTarget().startRiding(this.entity, true);
                    ModPolice.setUnconscious((EntityPlayer) entity.getAttackTarget(), false);
                } else if (this.entity.getDistance(this.entity.getAttackTarget()) < tazeRange) {
                    entity.getAttackTarget().getEntityData().setBoolean("Tazed", true);
                    ModPolice.setUnconscious((EntityPlayer) entity.getAttackTarget(), true);
                    playTazerSound();
                }
            } else {

                if (!hasArrestedPlayer() && isTargetArrested()) {
                    this.entity.setAttackTarget(null);
                    return false;
                }

                if (chargesForTarget.isEmpty()) {
                    if (targetHolding(ModGuns.itemGun)) {
                        chargesForTarget.add(ChargeType.POSSESSION_OF_UNLAWFUL_FIREARM);
                    } else if (targetHolding(ModGuns.itemDynamite)) {
                        chargesForTarget.add(ChargeType.POSSESSION_OF_UNLAWFUL_FIREARM);
                    } else if (targetHolding(ModDrugs.itemJoint)) {
                        chargesForTarget.add(ChargeType.POSSESSION_OF_MARIJUANA);
                    }

                    if (Objects.equals(entity.getAttackTarget().getUniqueID(), getKillerPlayer())) {
                        chargesForTarget.add(ChargeType.MURDER);
                    }
                }

                // if we have an arrested player take them to the nearest prison
                Prison nearbyPrison = entity.getNearbyPrison();

                if (nearbyPrison != null) {
                    if(getNearestButton() != null) {
                        BlockPos nearestButton = getNearestButton();
                        IBlockState blockState = entity.getEntityWorld().getBlockState(nearestButton);
                        blockState.getBlock().onBlockActivated(entity.getEntityWorld(), nearestButton, blockState, null, null, null, 0.5f, 0.5f, 0.5f);
                    }
                }

                if (nearbyPrison != null && this.getClosestForPathTarget() != null) {
                    this.entity.getNavigator().setPath(getClosestForPathTarget(), moveSpeed);
                    // drop player at drop off position
                    if (this.entity.getPosition().getDistance(nearbyPrison.getDropOffPos().getX(), nearbyPrison.getDropOffPos().getY(), nearbyPrison.getDropOffPos().getZ()) < 2) {
                        ModPolice.setUnconscious((EntityPlayer) entity.getAttackTarget(), false);
                        this.entity.getAttackTarget().removePotionEffect(MobEffects.SLOWNESS);
                        this.entity.setAttackTarget(null);
                        this.entity.removePassengers();
                        this.entity.getRegroupAI().setRegroupPos(this.entity.getRegroupAI().getRandomRegroupPos());
                        lostPlayer();
                    }
                } else if (getClosestForPathTarget() == null) {
                    this.pathAttempt++;

                    if (this.pathAttempt > 100) {
                        this.entity.setPosition(nearbyPrison.getDropOffPos().getX(), nearbyPrison.getDropOffPos().getY(), nearbyPrison.getDropOffPos().getZ());
                        this.entity.getAttackTarget().setPosition(nearbyPrison.getDropOffPos().getX(), nearbyPrison.getDropOffPos().getY(), nearbyPrison.getDropOffPos().getZ());
                        ModPolice.setUnconscious((EntityPlayer) entity.getAttackTarget(), false);
                        this.entity.getAttackTarget().removePotionEffect(MobEffects.SLOWNESS);
                        this.entity.setAttackTarget(null);
                        this.entity.removePassengers();
                        this.entity.getRegroupAI().setRegroupPos(this.entity.getRegroupAI().getRandomRegroupPos());
                        lostPlayer();
                        this.pathAttempt = 0;
                    }
                }
            }
        } else {
            lostPlayer();
        }
        return this.entity.getAttackTarget() != null && this.entity.getDistance(this.entity.getAttackTarget()) < followRange;
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.entity.getAttackTarget().posX, this.entity.getAttackTarget().posY, this.entity.getAttackTarget().posZ, moveSpeed);
        this.entity.setChasingPlayer((EntityPlayer) this.entity.getAttackTarget());
        super.startExecuting();
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null && this.entity.getEntitySenses().canSee(this.entity.getAttackTarget()) && !hasArrestedPlayer() && !isTargetArrested()
                && (targetHolding(ModGuns.itemGun) || targetHolding(ModGuns.itemDynamite) || targetHolding(ModDrugs.itemJoint) || getAggressivePlayer() != null);
    }

    public boolean targetHolding(Item item) {
        if (this.entity.getAttackTarget() != null && this.entity.getAttackTarget() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) this.entity.getAttackTarget();
            return player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item || player.getHeldItem(EnumHand.OFF_HAND).getItem() == item;
        }
        return false;
    }

    public UUID getAggressivePlayer() {
        return entity.getEntityData().hasKey("AggressivePlayer") ? UUID.fromString(entity.getEntityData().getString("AggressivePlayer")) : null;
    }

    public void setAggressivePlayer(UUID playerID) {
        if (playerID == null) {
            entity.getEntityData().removeTag("AggressivePlayer");
            return;
        }
        entity.getEntityData().setString("AggressivePlayer", playerID.toString());
    }

    public UUID getKillerPlayer() {
        return entity.getEntityData().hasKey("KillerPlayer") ? UUID.fromString(entity.getEntityData().getString("KillerPlayer")) : null;
    }

    public void setKillerPlayer(UUID playerID) {
        if (playerID == null) {
            entity.getEntityData().removeTag("KillerPlayer");
            return;
        }
        entity.getEntityData().setString("KillerPlayer", playerID.toString());
    }

    public boolean hasArrestedPlayer() {
        return !this.entity.getPassengers().isEmpty();
    }

    public boolean isTargetArrested() {
        return this.entity.getAttackTarget() != null && this.entity.getAttackTarget().getRidingEntity() != null && this.entity.getAttackTarget().getRidingEntity() instanceof EntityCop;
    }

    public Path getClosestForPathTarget() {
        Prison prison = entity.getNearbyPrison();
        Vector v = new Vector(entity.posX, entity.posY, entity.posZ);
        Vector v1 = new Vector(prison.getDropOffPos().getX(), prison.getDropOffPos().getY(), prison.getDropOffPos().getZ());
        int distance = (int) v.distance(v1);
        distance = distance > (int) this.entity.getNavigator().getPathSearchRange() ? (int) this.entity.getNavigator().getPathSearchRange() : distance;
        Vector subtracted = v.subtract(v1).normalize();

        Path path = this.entity.getNavigator().getPathToPos(prison.getDropOffPos());
        if (path == null)
            path = this.entity.getNavigator().getPathToPos(new BlockPos(entity.posX + (-subtracted.getX() *distance), entity.posY, entity.posZ + (-subtracted.getZ() * distance)));



        return path;
    }

    public void setChargesForTarget(List<ChargeType> chargesForTarget) {
        this.chargesForTarget = chargesForTarget;
    }

    public List<ChargeType> getChargesForTarget() {
        return chargesForTarget;
    }

    private void playTazerSound() {
        if (lastPlayerTazed < System.currentTimeMillis()) {
            Minelife.getNetwork().sendToAllAround(new PacketPlaySound(Minelife.MOD_ID + ":tazer", 0.2f, 1), new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 10));
            lastPlayerTazed = System.currentTimeMillis() + 2000L;
        }
    }

    private void lostPlayer() {
        setChargesForTarget(Lists.newArrayList());
        setAggressivePlayer(null);
        setKillerPlayer(null);
        entity.setChasingPlayer(null);
    }

    private BlockPos getNearestButton() {
        int minX = (int) (entity.posX - 2);
        int minY = (int) (entity.posY - 2);
        int minZ = (int) (entity.posZ - 2);
        int maxX = (int) (entity.posX + 2);
        int maxY = (int) (entity.posY + 2);
        int maxZ = (int) (entity.posZ + 2);
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (entity.getEntityWorld().getBlockState(pos).getBlock() == Blocks.STONE_BUTTON) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }
}
