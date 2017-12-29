package com.minelife.gun.client.guns;

import com.minelife.Minelife;
import com.minelife.gun.client.HoloRenderHelper;
import com.minelife.gun.item.attachments.ItemHolographicSite;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketBullet;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.Animation;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.ModelBipedCustom;
import com.minelife.util.client.render.RenderPlayerCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GunClientAK47 extends ItemGunClient {

    private Attachment holographic;
    private ResourceLocation holographic_reticle = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/holographic_reticle.png");

    public GunClientAK47(ItemGun gun) {
        super(gun);
        holographic = new Attachment("holographic");
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
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(0.3f, 0.3f, 0.3f);

            getAnimation().animate();
            if (!aimingDownSight) {
                GL11.glRotatef(310f, 0, 1, 0);
                GL11.glTranslatef(0.2f + getAnimation().posX(), -0.5f + getAnimation().posY(), 2f + getAnimation().posZ());
            } else {
                if (ItemHolographicSite.hasHolographic(item)) {
                    GL11.glRotatef(315f, 0, 1, 0);
                    GL11.glTranslatef(-2.8f + getAnimation().posX(), -0.2f + getAnimation().posY(), 2.5f + getAnimation().posZ());
                } else {
                    GL11.glRotatef(315f, 0, 1, 0);
                    GL11.glTranslatef(-2.8f + getAnimation().posX(), 0.6f + getAnimation().posY(), 2f + getAnimation().posZ());
                }
            }

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

        getModel().renderAll();

        if (ItemHolographicSite.hasHolographic(item)) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.7f, 5f, 0f);
            GL11.glScalef(0.5f, 0.5f, 0.5f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(holographic.getTexture());
            holographic.getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(holographic_reticle);
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemHolographicSite.getHolographicColor(item);
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, colorArray[3] / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-15.55f, 39f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public void renderFirstPerson(Minecraft mc, EntityPlayer player) {
        if (aimingDownSight) return;
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());

        GL11.glTranslatef(0.2F + (getAnimation().posX() / 5), getAnimation().posY() / 5, 0.1f + (getAnimation().posZ() / 5));
        float scale = 10f;
        GL11.glScalef(scale, scale, scale);

        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        renderer.modelBipedMain.onGround = 0.0F;
        renderer.modelBipedMain.setRotationAngles(0.0F, 0.0F, 2.0F, 285.0F, 28.0F, 0.0625F, player);
        renderer.modelBipedMain.bipedRightArm.offsetY = 0.01F;
        renderer.modelBipedMain.bipedRightArm.render(0.0095F);
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
        setAnimation(new Animation(0, 0, 0).translateTo((float) (Math.random() / (aimingDownSight ? 6f : 3f)) * PacketBullet.Handler.getLeftOrRight(random), (float) (Math.random() / (aimingDownSight ? 6f : 3f)), (float) (Math.random() / (aimingDownSight ? 6f : 3f)) * PacketBullet.Handler.getLeftOrRight(random), 0.18f).translateTo(0, 0, 0, 0.2f));
    }

    @Override
    public int getReboundSpeed() {
        return 8;
    }

    @Override
    public int[] yawSpread() {
        return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
    }

    @Override
    public int[] pitchSpread() {
        return !aimingDownSight ? new int[]{5, 20} : new int[]{1, 10};
    }

}
