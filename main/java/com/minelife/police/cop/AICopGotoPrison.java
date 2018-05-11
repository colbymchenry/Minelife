package com.minelife.police.cop;

import com.minelife.police.ModPolice;
import com.minelife.police.Prisoner;
import com.minelife.police.server.Prison;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.Vector;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.sql.SQLException;
import java.util.List;

public class AICopGotoPrison extends EntityAIBase {

    private EntityCop cop;
    private Prison prison = null;

    public AICopGotoPrison(EntityCop cop) {
        this.cop = cop;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (cop.getNavigator().noPath()) {
            Path path = findBestPath();
            if (path != null) cop.getNavigator().setPath(path, 1.8);
            else {
                // if we can't find a path then just teleport cop to prison
                jailPlayer();
                cop.setPositionAndUpdate(prison.getDropOffPos().getX() + 0.5, prison.getDropOffPos().getY() + 0.5, prison.getDropOffPos().getZ() + 0.5);
                cop.getCarryingPlayer().setPositionAndUpdate(prison.getDropOffPos().getX() + 0.5, prison.getDropOffPos().getY(), prison.getDropOffPos().getZ() + 0.5);
                cop.getNavigator().clearPath();
            }
        }

        if (!cop.isCarryingPlayer() || cop.getNavigator().noPath()) {
            if(cop.isCarryingPlayer()) {
                cop.getCarryingPlayer().dismountRidingEntity();
                BlockPos randomSpawnPoint = getRandomSpawnPoint();
                cop.setPositionAndUpdate(randomSpawnPoint.getX() + 0.5, randomSpawnPoint.getY() + 0.5, randomSpawnPoint.getZ() + 0.5);
            }
            return false;
        }

        double distance = cop.getDistance(prison.getDropOffPos().getX(), prison.getDropOffPos().getY(), prison.getDropOffPos().getZ());

        if (distance < 4) {
            jailPlayer();
            EntityPlayer carryingPlayer = cop.getCarryingPlayer();
            carryingPlayer.dismountRidingEntity();
            carryingPlayer.setPositionAndUpdate(prison.getDropOffPos().getX()+ 0.5, prison.getDropOffPos().getY(), prison.getDropOffPos().getZ() + 0.5);
            BlockPos randomSpawnPoint = getRandomSpawnPoint();
            cop.setPositionAndUpdate(randomSpawnPoint.getX(), randomSpawnPoint.getY(), randomSpawnPoint.getZ());
            cop.getNavigator().clearPath();
        }

        return true;
    }

    @Override
    public void startExecuting() {
        prison = Prison.getClosestPrison(cop.getPosition());
    }

    @Override
    public boolean shouldExecute() {
        return cop.isCarryingPlayer();
    }

    @Override
    public void resetTask() {
        if(cop.getRidingEntity() != null)
            cop.getRidingEntity().dismountRidingEntity();

        cop.setAttackTarget(null);
        cop.setChasingPlayer(null);
        cop.getNavigator().clearPath();
        prison = null;
    }

    public Path findBestPath() {
        if (prison == null) return null;

        double maxDistance = cop.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue();
        double distance = cop.getDistance(prison.getDropOffPos().getX(), prison.getDropOffPos().getY(), prison.getDropOffPos().getZ());

        if (distance > maxDistance) {
            Vector v = new Vector(cop.posX, cop.posY, cop.posZ);
            Vector v1 = new Vector(prison.getDropOffPos().getX(), prison.getDropOffPos().getY(), prison.getDropOffPos().getZ());
            Vector subtracted = v.subtract(v1).normalize();

            return cop.getNavigator().getPathToXYZ(cop.posX + (-subtracted.getX() * maxDistance), cop.posY, cop.posZ + (-subtracted.getZ() * maxDistance));
        }

        return cop.getNavigator().getPathToXYZ(prison.getDropOffPos().getX(), prison.getDropOffPos().getY(), prison.getDropOffPos().getZ());
    }

    public BlockPos getRandomSpawnPoint() {
        if (ModPolice.getConfig().getStringList("PoliceSpawnPoints") == null) return cop.getPosition();
        List<String> spawnPoints = ModPolice.getConfig().getStringList("PoliceSpawnPoints");
        String s = spawnPoints.get(cop.getEntityWorld().rand.nextInt(spawnPoints.size()));
        String[] data = s.split(",");
        return new BlockPos(NumberConversions.toInt(data[0]), NumberConversions.toInt(data[1]), NumberConversions.toInt(data[2]));
    }

    public void jailPlayer() {
        if(!cop.chargesForTarget.isEmpty() && cop.getCarryingPlayer() != null) {
            Prisoner prisoner = new Prisoner(cop.getCarryingPlayer().getUniqueID(), cop.chargesForTarget);
            try {
                prisoner.setSavedInventory(cop.getCarryingPlayer().inventory);
                cop.getCarryingPlayer().inventory.clear();
                cop.getCarryingPlayer().inventoryContainer.detectAndSendChanges();
                prisoner.save();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
