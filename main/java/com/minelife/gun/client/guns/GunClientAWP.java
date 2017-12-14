package com.minelife.gun.client.guns;

import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.Animation;
import com.minelife.util.client.render.ModelBipedCustom;
import com.minelife.util.client.render.RenderPlayerCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GunClientAWP extends ItemGunClient {

    private boolean zoom = false, rightMouseDown = false;

    public GunClientAWP(ItemGun gun) {
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
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(310f, 0, 1, 0);

                getAnimation().animate();
                GL11.glTranslatef(0.1f + getAnimation().posX(), -0.7f + getAnimation().posY(), -1.5f + getAnimation().posZ());
            }

            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.2f, 0.2f, 0.2f);
                GL11.glTranslatef(0.5f, -1.25f, 0f);
            }

            if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(0.28f, 0.28f, 0.28f);
                GL11.glRotatef(50F, 0f, 1f, 0f);
                GL11.glRotatef(290F, 1f, 0f, 0f);
                GL11.glTranslatef(-1.8f, -5F, 1f);
            }

            if (zoom && getAnimation().getActions().isEmpty() && type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                PlayerHelper.zoom(6.0D);
            } else {
                getModel().renderAll();
                PlayerHelper.zoom(1.0D);
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public void renderFirstPerson(Minecraft mc, EntityPlayer player) {
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());

        GL11.glPushMatrix();
        GL11.glTranslatef(0.3F + (getAnimation().posX() / 5), getAnimation().posY() / 5, -0.1f + (getAnimation().posZ() / 5));
        GL11.glScalef(3f, 3f, 3f);

        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        RenderPlayerCustom.instance.modelBipedMain.onGround = 0.0F;
        RenderPlayerCustom.instance.modelBipedMain.setRotationAngles(0.0F, 0.0F, 2.0F, 285.0F, 28.0F, 0.0625F, player);
        RenderPlayerCustom.instance.modelBipedMain.bipedRightArm.offsetY = -0.1F;
        RenderPlayerCustom.instance.modelBipedMain.bipedRightArm.render(0.0225F);
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
        model.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + model.bipedHead.rotateAngleX;
        model.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + model.bipedHead.rotateAngleX;
        model.bipedRightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        model.bipedLeftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        model.bipedRightArm.rotateAngleZ += MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedRightArm.rotateAngleX += MathHelper.sin(f1 * 0.067F) * 0.05F;
        model.bipedLeftArm.rotateAngleX -= MathHelper.sin(f1 * 0.067F) * 0.05F;
    }

    @Override
    public void shootBullet() {
        setAnimation(new Animation(0, 0, 0f).translateTo((float) (Math.random() / 7f), (float) (Math.random() / 7f), 2, 0.2f).translateTo(0, 0, 0f, 0.2f));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer holder, int slot, boolean inHand) {
        super.onUpdate(stack, world, holder, slot, inHand);

        if (inHand && Mouse.isButtonDown(1)) {
            if(!rightMouseDown) {
                if (!zoom) {
                    setAnimation(new Animation(0, 0, 0f).translateTo(-2.5f, 0f, 0f, 0.2f));
                    PlayerHelper.zoom(6.0D);
                    zoom = true;
                } else {
                    setAnimation(new Animation(-2f, 0, 0f).translateTo(0f, 0f, 0f, 0.2f));
                    PlayerHelper.zoom(1.0D);
                    zoom = false;
                }

                rightMouseDown = true;
            }
        } else {
            rightMouseDown = false;
        }

        if(!inHand) {
            zoom = false;
            setAnimation(new Animation(0f, 0f, 0f).translate(0f, 0f, 0f));
        }

    }

    public boolean isZoom() {
        return zoom;
    }

    @Override
    public int getReboundSpeed() {
        return 8;
    }

    @Override
    public int[] yawSpread() {
        return new int[]{1, 10};
    }

    @Override
    public int[] pitchSpread() {
        return new int[]{1, 20};
    }
}
