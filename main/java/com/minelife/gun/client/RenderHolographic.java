package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.client.guns.Attachment;
import com.minelife.gun.item.attachments.ItemHolographicSite;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderHolographic implements IItemRenderer {

    private Attachment holographic;
    private ResourceLocation holographic_reticle = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/holographic_reticle.png");

    public RenderHolographic() {
        holographic = new Attachment("holographic");
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        if (type == ItemRenderType.ENTITY) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(0.25f, 0.25f, 0.25f);
            GL11.glRotatef(-40f, 0, 1, 0);
            GL11.glTranslatef(1f, 3f, 1f);
        }

        if(type == ItemRenderType.INVENTORY) {
            GL11.glScalef(0.35f, 0.35f, 0.35f);
            GL11.glTranslatef(1f, -1f, 0f);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(holographic.getTexture());
        RenderHelper.enableStandardItemLighting();
        holographic.getModel().renderAll();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}