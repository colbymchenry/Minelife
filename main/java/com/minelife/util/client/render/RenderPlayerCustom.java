package com.minelife.util.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class RenderPlayerCustom extends RenderPlayer {
    /**
     * this field is used to indicate the 3-pixel wide arms
     */
    private final boolean smallArms;

    public RenderPlayerCustom(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, false);
        this.smallArms = useSmallArms;
        this.addLayer(new CustomLayerCape(this));
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.isUser() || this.renderManager.renderViewEntity == entity)
        {
            System.out.println("BNOOM");
            double d0 = y;

            if (entity.isSneaking())
            {
                d0 = y - 0.125D;
            }

            Method method = ReflectionHelper.findMethod(RenderPlayer.class, "setModelVisibilities", null, AbstractClientPlayer.class);
            method.setAccessible(true);


            method = ReflectionHelper.findMethod(RenderLivingBase.class, "doRender", null, Entity.class, double.class, double.class, double.class, float.class, float.class);
            method.setAccessible(true);

            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            try {
                method.invoke(this, entity, x, y, z, entityYaw, partialTicks);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        MinecraftForge.EVENT_BUS.post(new AdjustPlayerModelEvent(this.getMainModel(), partialTickTime, (EntityPlayer) entitylivingbaseIn));
    }

    @Override
    protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
        MinecraftForge.EVENT_BUS.post(new AdjustPlayerModelEvent(this.getMainModel(), partialTicks, (EntityPlayer) entityLiving));
    }

}