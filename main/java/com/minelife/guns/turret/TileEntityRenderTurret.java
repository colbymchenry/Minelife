package com.minelife.guns.turret;

import com.minelife.guns.ModGuns;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.render.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class TileEntityRenderTurret extends TileEntitySpecialRenderer<TileEntityTurret> {

    private static ItemStack legs = new ItemStack(ModGuns.itemTurret);
    private static ItemStack head = new ItemStack(ModGuns.itemTurret, 1, 1);

    public TileEntityRenderTurret() {
    }

    @Override
    public void render(TileEntityTurret te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.rotate(te.getDirection() == EnumFacing.NORTH ? 180 : te.getDirection() == EnumFacing.SOUTH ? 0 : te.getDirection() == EnumFacing.EAST ? 90 : -90, 0, 1, 0);
        GlStateManager.scale(2, 2, 2);
        GuiFakeInventory.renderItem(Minecraft.getMinecraft(), legs, ItemCameraTransforms.TransformType.FIXED);

        if (te.getWorld() != null && te.getWorld().getEntityByID(te.getTargetID()) != null
                && te.getWorld().getEntityByID(te.getTargetID()) instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) te.getWorld().getEntityByID(te.getTargetID());
            Vector lookVec = te.getLookVec(target);
            float yaw = (float) Math.atan2(lookVec.getX(), lookVec.getZ());
            float pitch = (float) Math.asin(lookVec.getY() / Math.sqrt(lookVec.getZ() * lookVec.getZ() + lookVec.getX() * lookVec.getX()));
            yaw = (float) Math.toDegrees(yaw);
            pitch = (float) Math.toDegrees(pitch) + 6;

            double w = 0, h = 0.25, l = 0;
            GlStateManager.translate(w, h, l);

            if (te.getDirection() == EnumFacing.SOUTH)
                GlStateManager.rotate(yaw - 180, 0, 1, 0);
            else if (te.getDirection() == EnumFacing.NORTH)
                GlStateManager.rotate(yaw, 0, 1, 0);
            else if (te.getDirection() == EnumFacing.EAST) {
                GlStateManager.rotate(yaw + 90, 0, 1, 0);
            } else if (te.getDirection() == EnumFacing.WEST) {
                GlStateManager.rotate(yaw - 90, 0, 1, 0);
            }
            GlStateManager.rotate(pitch, 1, 0, 0);
            GlStateManager.translate(-w, -h, -l);

        } else {
            GlStateManager.rotate(te.rotationYaw, 0, 1, 0);
        }
        GuiFakeInventory.renderItem(Minecraft.getMinecraft(), head, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
