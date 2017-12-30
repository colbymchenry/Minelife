package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.client.attachments.Attachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderAttachment  implements IItemRenderer {

    private Attachment attachment;
    private ResourceLocation holographic_reticle = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/holographic_reticle.png");

    public RenderAttachment(Attachment attachment) {
        this.attachment = attachment;
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
        attachment.applyTransformations(type, item);
        Minecraft.getMinecraft().getTextureManager().bindTexture(attachment.getTexture());
        RenderHelper.enableStandardItemLighting();
        attachment.getModel().renderAll();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}