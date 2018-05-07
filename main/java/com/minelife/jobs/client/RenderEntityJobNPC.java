package com.minelife.jobs.client;

import com.minelife.Minelife;
import com.minelife.jobs.EntityJobNPC;
import me.ichun.mods.hats.client.core.HatInfoClient;
import me.ichun.mods.hats.client.render.HatRendererHelper;
import me.ichun.mods.hats.client.render.helper.HelperPlayer;
import me.ichun.mods.hats.common.core.HatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntityJobNPC extends RenderBiped {

    private ResourceLocation farmerTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/farmer.png");
    private ResourceLocation fishermanTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/fisherman.png");
    private ResourceLocation bountyHunterTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/bountyhunter.png");
    private ResourceLocation chefTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/chef.png");
    private ResourceLocation lumberjackTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/lumberjack.png");
    private ResourceLocation minerTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/miner.png");
    private ResourceLocation policeTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/police.png");
    private ResourceLocation drugProducerTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/drug_producer.png");
    private ResourceLocation emtTex = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/emt.png");

    public RenderEntityJobNPC(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
    }

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float pitch) {
        super.doRender(entity, x, y, z, yaw, pitch);

        HelperPlayer helper = (HelperPlayer) HatHandler.getRenderHelper(EntityPlayer.class);
        float scale = helper.getHatScale(entity);

        this.renderEntityName((EntityJobNPC) entity, x, y, z, entity.getDistance(Minecraft.getMinecraft().player));

        String hat = null;

        switch (((EntityJobNPC) entity).getProfession()) {
            case 0:
                hat = "straw hat";
                break;
            case 1:
                hat = null;
                break;
            case 2:
                hat = "pickaxe in head";
                break;
            case 3:
                hat = null;
                break;
            case 4:
                hat = null;
                break;
            case 5:
                hat = "woodhat";
                break;
            case 6:
                hat = null;
                break;
        }

        if (hat != null) {
            GL11.glPushMatrix();
            {
                GL11.glTranslated(x, y, z);

                HatRendererHelper.renderHat(new HatInfoClient(hat, 255, 255, 255, 255), 255, scale, scale, scale, scale, helper.getRenderYaw(entity), helper.getRotationYaw(entity), helper.getRotationPitch(entity), helper.getRotationRoll(entity),
                        helper.getRotatePointVert(entity), helper.getOffsetPointHori(entity), helper.getRotatePointSide(entity), helper.getOffsetPointVert(entity), helper.getOffsetPointHori(entity),
                        helper.getOffsetPointSide(entity), true, true, helper.renderTick);
            }
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        EntityJobNPC jobNPC = (EntityJobNPC) entity;
        switch (jobNPC.getProfession()) {
            case 0:
                return farmerTex;
            case 1:
                return fishermanTex;
            case 2:
                return minerTex;
            case 3:
                return bountyHunterTex;
            case 4:
                return chefTex;
            case 5:
                return lumberjackTex;
            case 6:
                return policeTex;
            case 7:
                return drugProducerTex;
            case 8:
                return emtTex;
        }
        return null;
    }

    protected void renderEntityName(EntityJobNPC npc, double x, double y, double z, double distanceSq) {
        if (distanceSq < 100.0D) {
            this.renderLivingLabel(npc, npc.getCustomNameTag(), x, y + 0.5, z, 64);
        }
    }
}
