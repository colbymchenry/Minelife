package com.minelife.jobs;

import com.minelife.Minelife;
import com.minelife.clothing.ModelPlayer;
import hats.client.core.HatInfoClient;
import hats.client.render.HatRendererHelper;
import hats.client.render.helper.HelperPlayer;
import hats.common.core.CommonProxy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntityJobNPC extends RenderBiped {

    private ResourceLocation farmerTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/farmer.png");

    public RenderEntityJobNPC() {
        super(new ModelPlayer(), 0.5F);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float pitch) {
        super.doRender(entity, x, y, z, yaw, pitch);

        HelperPlayer helper = (HelperPlayer) CommonProxy.renderHelpers.get(EntityPlayer.class);

        float scale = helper.getHatScale(entity);

        this.renderNameTag(entity, entity.getCustomNameTag(), x, y, z, 64);


        // TODO: Render item in hand.

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z);

            HatRendererHelper.renderHat(new HatInfoClient("straw hat", 255, 255, 255, 255), 255, scale, scale, scale, scale, helper.getRenderYaw(entity), helper.getRotationYaw(entity), helper.getRotationPitch(entity), helper.getRotationRoll(entity),
                    helper.getRotatePointVert(entity), helper.getOffsetPointHori(entity), helper.getRotatePointSide(entity), helper.getOffsetPointVert(entity), helper.getOffsetPointHori(entity),
                    helper.getOffsetPointSide(entity), true, true, helper.renderTick);
        }
        GL11.glPopMatrix();


    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityJobNPC jobNPC = (EntityJobNPC) entity;
        switch(jobNPC.npcType) {
            case 0: return farmerTex;
        }
        return null;
    }

    private void renderNameTag(Entity p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_)
    {
        double d3 = p_147906_1_.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (d3 <= (double)(p_147906_9_ * p_147906_9_))
        {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)p_147906_3_ + 0.0F, (float)p_147906_5_ + p_147906_1_.height + 0.5F, (float)p_147906_7_);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;
            byte b0 = -10;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int j = fontrenderer.getStringWidth(p_147906_2_) / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
}
