package com.minelife.gun.gun;

import com.google.common.collect.Lists;
import com.minelife.util.client.Animation;
import com.minelife.Minelife;
import com.minelife.util.PlayerHelper;
import com.minelife.gun.*;
import com.minelife.gun.ammo.Ammo556;
import com.minelife.util.client.render.ModelBipedCustom;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class M4 extends ItemGun {

    public M4(FMLPreInitializationEvent event) {
        super(event);
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    private Animation animation = new Animation(0, 0, 0);

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        {
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(310f, 0, 1, 0);

                animation.animate();
                GL11.glTranslatef(0.1f + animation.posX(), 0f + animation.posY(), 1f + animation.posZ());
            }

            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslatef(0.5f, -1.25f, 0f);
            }

            if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(0.28f, 0.28f, 0.28f);
                GL11.glRotatef(50F, 0f, 1f, 0f);
                GL11.glRotatef(290F, 1f, 0f, 0f);
                GL11.glTranslatef(-1.8f, -5F, 1f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();
    }

    @Override
    public void setArmRotations(ModelBipedCustom model, float f1) {
        float f6 = 0.0F;
        float f7 = 0.0F;
        model.bipedRightArm.rotateAngleZ = 0.0F;
        model.bipedLeftArm.rotateAngleZ = 0.0F;
        model.bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + model.bipedHead.rotateAngleY;
        model.bipedLeftArm.rotateAngleY = 0.1F - f6 * 0.6F + model.bipedHead.rotateAngleY + 0.4F;
        model.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + model.bipedHead.rotateAngleX;
        model.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + model.bipedHead.rotateAngleX;
        model.bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        model.bipedLeftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        model.bipedRightArm.rotateAngleZ += MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedRightArm.rotateAngleX += MathHelper.sin(f1 * 0.067F) * 0.05F;
        model.bipedLeftArm.rotateAngleX -= MathHelper.sin(f1 * 0.067F) * 0.05F;
    }


    @Override
    public void fire(ItemStack gunStack) {
        animation = new Animation(0, 0, 0).translateTo((float) (Math.random() / 7f), (float) (Math.random() / 7f), 2, 0.2f).translateTo(0, 0, 0, 0.2f);
        Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + getSoundForShot(getAmmo(gunStack)), 0.5F, 1.0F);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        PlayerHelper.getTargetEntity(player, 50);
    }

    @Override
    public String getSoundForShot(ItemStack ammo) {
        return "gun.m4.shot";
    }

    @Override
    public int getClipSize() {
        return 30;
    }

    @Override
    public long getFireRate() {
        return 100;
    }

    @Override
    public List<ItemAmmo> validAmmo() {
        List<ItemAmmo> validAmmo = Lists.newArrayList();
        validAmmo.add(ModGun.ammoMap.get(Ammo556.class));
        return validAmmo;
    }
}
