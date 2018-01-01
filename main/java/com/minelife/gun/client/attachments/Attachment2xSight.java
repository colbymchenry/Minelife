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

public class Attachment2xSight extends Attachment {

    public Attachment2xSight() {
        super("2x");
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

        if(type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
            GL11.glTranslatef(1f, -0f, 0f);
        }

        if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
            GL11.glTranslatef(4f, 4f, 4f);
        }
    }

    @Override
    public void applyTransformationsAttached(ItemStack gun) {
        if(gun.getItem() == MLItems.ak47) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.1f, 6f, 1f);
            GL11.glRotatef(180f, 0f, 1f, 0f);
            GL11.glScalef(0.35f, 0.35f, 0.35f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getTwoXSite().getTexture());
            Attachment.getTwoXSite().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getTwoXSite().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSite.getSiteColor(ItemGun.getSite(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-30.55f, -9f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        } else if (gun.getItem() == MLItems.m4a4) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.15f, 3f, 0f);
            GL11.glRotatef(181f, 0f, 1f, 0f);
            GL11.glScalef(0.35f, 0.35f, 0.35f);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getTwoXSite().getTexture());
            Attachment.getTwoXSite().getModel().renderAll();

            Minecraft.getMinecraft().getTextureManager().bindTexture(Attachment.getTwoXSite().getReticleTexture());
            RenderHelper.enableGUIStandardItemLighting();
            int[] colorArray = ItemSite.getSiteColor(ItemGun.getSite(gun));
            GL11.glColor4f(colorArray[0] / 255f, colorArray[1] / 255f, colorArray[2] / 255f, 200f / 255f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glScalef(0.05f, 0.05f, 0.05f);
            GL11.glTranslatef(-32.55f, -9f, -7f);
            GuiUtil.drawImage(0, 0, 16, 16);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        }
    }
}
