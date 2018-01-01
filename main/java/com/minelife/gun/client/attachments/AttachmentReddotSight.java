package com.minelife.gun.client.attachments;

import com.minelife.MLItems;
import com.minelife.gun.item.attachments.ItemSite;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class AttachmentReddotSight extends Attachment {

    public AttachmentReddotSight() {
        super("reddot");
    }

    @Override
    public void applyTransformations(IItemRenderer.ItemRenderType type, ItemStack item) {
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
        }

        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(0.25f, 0.25f, 0.25f);
            GL11.glRotatef(140f, 0, 1, 0);
            GL11.glTranslatef(1f, 3f, -2f);
        }

        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(0.20f, 0.20f, 0.20f);
            GL11.glRotatef(180f, 0f, 1f, 0f);
            GL11.glTranslatef(1f, -0.5f, 0f);
        }

        if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glScalef(0.20f, 0.20f, 0.20f);
            GL11.glRotatef(250f, 0f, 1f, 0f);
            GL11.glTranslatef(5f, 4f, -4f);
        }
    }

    @Override
    public void applyTransformationsAttached(ItemStack gun) {
        if (gun.getItem() == MLItems.ak47) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.7f, 5f, 0f);
            GL11.glScalef(0.5f, 0.5f, 0.5f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getHolographic().getTexture());
            Attachment.getHolographic().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getHolographic().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSite.getSiteColor(ItemGun.getSite(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-15.55f, 39f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        } else if (gun.getItem() == MLItems.m4a4) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.66f, 2f, 0f);
            GL11.glScalef(0.35f, 0.35f, 0.35f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getHolographic().getTexture());
            Attachment.getHolographic().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getHolographic().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSite.getSiteColor(ItemGun.getSite(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-15.55f, 39f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        }
    }
}
