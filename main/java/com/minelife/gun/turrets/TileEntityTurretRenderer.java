package com.minelife.gun.turrets;

import codechicken.lib.vec.Vector3;
import com.minelife.Minelife;
import com.minelife.util.Vector;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class TileEntityTurretRenderer extends TileEntitySpecialRenderer {

    ResourceLocation TexLegs, TexBodyOn, TexBodyOff;
    ResourceLocation OBJLegs, OBJBody;
    IModelCustom ModelLegs, ModelBody;

    public TileEntityTurretRenderer() {
        TexLegs = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_legs.png");
        TexBodyOn = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_body_on.png");
        TexBodyOff = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_body_off.png");
        OBJLegs = new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_legs.obj");
        OBJBody = new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_body.obj");
        ModelLegs = AdvancedModelLoader.loadModel(OBJLegs);
        ModelBody = AdvancedModelLoader.loadModel(OBJBody);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y + 1, z + 1);

            bindTexture(TexLegs);
            GL11.glTranslatef(0, -1, 0);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            GL11.glTranslatef(-0.2f, 0, 0.155f);
            ModelLegs.renderAll();
        }
        GL11.glPopMatrix();

        TileEntityTurret turret = (TileEntityTurret) tileEntity;


        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z + 1);
            GL11.glTranslatef(0, 1f, 0);

            GL11.glTranslated(0.5, 0.3, -0.5);

            bindTexture(turret.getAmmo().isEmpty() ? TexBodyOff : TexBodyOn);

            GL11.glRotatef(turret.getDirection() == EnumFacing.NORTH ? -90 : turret.getDirection() == EnumFacing.SOUTH ? 90 : turret.getDirection() == EnumFacing.EAST ? -180 : 0, 0, 1, 0);

            if (turret.getWorldObj().getEntityByID(turret.getTargetID()) != null) {
                EntityLivingBase target = (EntityLivingBase) turret.getWorldObj().getEntityByID(turret.getTargetID());
                Vector lookVec = turret.getLookVec(target);
                float yaw = (float) Math.atan2(lookVec.getX(), lookVec.getZ());
                float pitch = (float) Math.asin(lookVec.getY() / Math.sqrt(lookVec.getZ() * lookVec.getZ() + lookVec.getX() * lookVec.getX()));
                yaw = (float) Math.toDegrees(yaw);
                pitch = (float) Math.toDegrees(pitch) + 6;

                if (turret.getDirection() == EnumFacing.SOUTH)
                    GL11.glRotatef(yaw - 180, 0, 1, 0);
                else if (turret.getDirection() == EnumFacing.NORTH)
                    GL11.glRotatef(yaw, 0, 1, 0);
                else if (turret.getDirection() == EnumFacing.EAST) {
                    GL11.glRotatef(yaw + 90, 0, 1, 0);
                } else if(turret.getDirection() == EnumFacing.WEST) {
                    GL11.glRotatef(yaw - 90, 0, 1, 0);
                }
                GL11.glRotatef(pitch, 0, 0, 1);
            } else {
                GL11.glRotated(turret.rotationYaw, 0, 1, 0);
            }


            GL11.glTranslated(-0.5, -0.3, 0.5);
            ModelBody.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
