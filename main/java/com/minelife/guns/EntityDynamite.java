package com.minelife.guns;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityDynamite extends EntityArrow {

    private int timeInGround = 0;

    public EntityDynamite(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (inGround) {
            timeInGround++;

            if (timeInGround > 20 * 3) {
                world.removeEntity(this);
                if (!world.isRemote)
                    world.createExplosion(this, posX, posY, posZ, 2, true);
            }
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return null;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
//        super.onCollideWithPlayer(entityIn);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if(raytraceResultIn.entityHit == null) super.onHit(raytraceResultIn);
    }
}
