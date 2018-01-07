package com.minelife.gun.client.attachments;

import com.minelife.MLItems;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.attachments.ItemSight;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class AttachmentAcogSight extends Attachment {

    public AttachmentAcogSight() {
        super("acog");
    }

    @Override
    public void applyTransformations(IItemRenderer.ItemRenderType type, ItemStack item) {
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glScalef(0.1f, 0.1f, 0.1f);
        }

        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(0.25f, 0.25f, 0.25f);
            GL11.glRotatef(-40f, 0, 1, 0);
            GL11.glTranslatef(3f, 3f, 1f);
        }

        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
            GL11.glTranslatef(1f, -0f, 0f);
        }

        if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
            GL11.glTranslatef(4f, 5f, 4f);
        }
    }

    @Override
    public void applyTransformationsAttached(ItemStack gun) {
        if (gun.getItem() == MLItems.ak47) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.5f, 5.7f, 1.2f);
            GL11.glScalef(0.35f, 0.35f, 0.35f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getAcogSite().getTexture());
            Attachment.getAcogSite().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getAcogSite().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSight.getSightColor(ItemGun.getSight(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-8.55f, -1f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
        } else if (gun.getItem() == MLItems.m4a4) {
            GL11.glPushMatrix();

            if(ItemGunClient.aimingDownSight) {
                GL11.glTranslatef(0.5f, 3.15f, 1f);
            } else {
                GL11.glTranslatef(0.5f, 2.7f, 1f);
            }
            GL11.glScalef(0.35f, 0.35f, 0.35f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getAcogSite().getTexture());
            Attachment.getAcogSite().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getAcogSite().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSight.getSightColor(ItemGun.getSight(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-8.55f, -1f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
