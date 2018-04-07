package com.minelife.guns.turret;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import com.minelife.Minelife;
import com.minelife.util.client.render.Vector;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class TileEntityRenderTurret extends TileEntitySpecialRenderer<TileEntityTurret> {

    private static CCModel modelLegs, modelBody;
    private static ResourceLocation textureLegs, textureOn, textureOff;

    public TileEntityRenderTurret() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_legs.obj"));
        modelLegs = CCModel.combine(map.values());
        map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_body.obj"));
        modelBody = CCModel.combine(map.values());
        textureLegs = new ResourceLocation(Minelife.MOD_ID, "textures/block/turret_legs.png");
        textureOn = new ResourceLocation(Minelife.MOD_ID, "textures/block/turret_body_on.png");
        textureOff = new ResourceLocation(Minelife.MOD_ID, "textures/block/turret_body_off.png");
    }

    @Override
    public void render(TileEntityTurret te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.enableRescaleNormal();

//        int lightc = world.getCombinedLight(flow.pipe.getHolder().getPipePos(), 0);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightc % (float) 0x1_00_00, lightc / (float) 0x1_00_00);

        RenderHelper.enableGUIStandardItemLighting();
        CCRenderState ccrs = CCRenderState.instance();

        GL11.glColor4f(1, 1, 1, 1);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y + 1, z + 1);

            bindTexture(textureLegs);
            GL11.glTranslatef(0, -1, 0);
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            GL11.glTranslatef(-0.2f, 0, 0.155f);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
            modelLegs.render(ccrs);
            ccrs.draw();
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z + 1);
            GL11.glTranslatef(0, 1f, 0);

            GL11.glTranslated(0.5, 0.3, -0.5);

            bindTexture(te.getAmmo().isEmpty() ? textureOff : textureOn);

            GL11.glRotatef(te.getDirection() == EnumFacing.NORTH ? -90 : te.getDirection() == EnumFacing.SOUTH ? 90 : te.getDirection() == EnumFacing.EAST ? -180 : 0, 0, 1, 0);

            if (te.getWorld() != null && te.getWorld().getEntityByID(te.getTargetID()) != null) {
                EntityLivingBase target = (EntityLivingBase) te.getWorld().getEntityByID(te.getTargetID());
                Vector lookVec = te.getLookVec(target);
                float yaw = (float) Math.atan2(lookVec.getX(), lookVec.getZ());
                float pitch = (float) Math.asin(lookVec.getY() / Math.sqrt(lookVec.getZ() * lookVec.getZ() + lookVec.getX() * lookVec.getX()));
                yaw = (float) Math.toDegrees(yaw);
                pitch = (float) Math.toDegrees(pitch) + 6;

                if (te.getDirection() == EnumFacing.SOUTH)
                    GL11.glRotatef(yaw - 180, 0, 1, 0);
                else if (te.getDirection() == EnumFacing.NORTH)
                    GL11.glRotatef(yaw, 0, 1, 0);
                else if (te.getDirection() == EnumFacing.EAST) {
                    GL11.glRotatef(yaw + 90, 0, 1, 0);
                } else if(te.getDirection() == EnumFacing.WEST) {
                    GL11.glRotatef(yaw - 90, 0, 1, 0);
                }
                GL11.glRotatef(pitch, 0, 0, 1);
            } else {
                GL11.glRotated(te.rotationYaw, 0, 1, 0);
            }


            GL11.glTranslated(-0.5, -0.3, 0.5);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
            modelBody.render(ccrs);
            ccrs.draw();
        }
        GL11.glPopMatrix();

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableCull();
    }

}
