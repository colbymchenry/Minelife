package com.minelife.gun.client.guns;

import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.client.Animation;
import com.minelife.util.client.render.ModelBipedCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class GunClientMagnum extends ItemGunClient {

    public GunClientMagnum(ItemGun gun) {
        super(gun);
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        {
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glScalef(0.3f, 0.3f, 0.3f);
                GL11.glRotatef(310f, 0, 1, 0);

                getAnimation().animate();
                GL11.glTranslatef(0.4f + getAnimation().posX(), -0.25f + getAnimation().posY(), -4.2f + getAnimation().posZ());
                GL11.glRotatef(getAnimation().rotX(), 1, 0, 0);
                GL11.glRotatef(getAnimation().rotY(), 0, 1, 0);
                GL11.glRotatef(getAnimation().rotZ(), 0, 0, 1);
            }

            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.3f, 0.3f, 0.3f);
                GL11.glTranslatef(0.1f, -1.25f, 1.5f);
            }

            if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(0.28f, 0.28f, 0.28f);
                GL11.glRotatef(50F, 0f, 1f, 0f);
                GL11.glRotatef(290F, 1f, 0f, 0f);
                GL11.glTranslatef(-1.8f, -5F, 1f);
            }

            getModel().renderAll();
        }
        GL11.glPopMatrix();
    }

    @Override
    public void renderFirstPerson(Minecraft mc, EntityPlayer player) {
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0.65F + (getAnimation().posX() / 5), -0.4f + (getAnimation().posY() / 5), -0.2f + (getAnimation().posZ() / 5));
            float scale = 10f;
            GL11.glScalef(scale, scale, scale);

            float f = 1.0F;
            GL11.glColor3f(f, f, f);
            renderer.modelBipedMain.onGround = 0.0F;
            renderer.modelBipedMain.setRotationAngles(0.0F, 0.0F, 2.0F, 285.0F, 28.0F, 0.0625F, player);
            renderer.modelBipedMain.bipedRightArm.offsetY = 0.01F;
            renderer.modelBipedMain.bipedRightArm.render(0.0095F);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0F + (getAnimation().posX() / 5), getAnimation().posY() / 5, 0.3f + (getAnimation().posZ() / 5));
            GL11.glRotatef(-20, 0, 1 ,1);
            float scale = 10f;
            GL11.glScalef(scale, scale, scale);

            float f = 1.0F;
            GL11.glColor3f(f, f, f);

            renderer.modelBipedMain.onGround = 0.0F;
            renderer.modelBipedMain.setRotationAngles(0.0F, 5.0F, 0, 0, 0, 0.0625F, player);
            renderer.modelBipedMain.bipedLeftArm.offsetY = 0.01F;
            renderer.modelBipedMain.bipedLeftArm.render(0.0095F);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void setArmRotations(ModelBipedCustom model, float f1) {
        float f6 = 0.0F;
        float f7 = 0.0F;
        model.bipedRightArm.rotateAngleZ = 0.0F;
//        model.bipedLeftArm.rotateAngleZ = 0.0F;
        model.bipedRightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + model.bipedHead.rotateAngleY;
//        model.bipedLeftArm.rotateAngleY = 0.1F - f6 * 0.6F + model.bipedHead.rotateAngleY + 0.4F;
        model.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + model.bipedHead.rotateAngleX;
//        model.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + model.bipedHead.rotateAngleX;
        model.bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
//        model.bipedLeftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        model.bipedRightArm.rotateAngleZ += MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
//        model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedRightArm.rotateAngleX += MathHelper.sin(f1 * 0.067F) * 0.05F;
//        model.bipedLeftArm.rotateAngleX -= MathHelper.sin(f1 * 0.067F) * 0.05F;
    }

    @Override
    public void shootBullet() {
        setAnimation(new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.x, 35, 6.5f).rotateTo(Animation.EnumRotation.x, 0, 4.5f));
    }

    @Override
    public int getReboundSpeed() {
        return 2;
    }

    @Override
    public int[] yawSpread() {
        return new int[]{1, 10};
    }

    @Override
    public int[] pitchSpread() {
        return new int[]{20, 60};
    }

}
