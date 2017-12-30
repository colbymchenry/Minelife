package com.minelife.gun.client.attachments;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class AttachmentHolographicSite extends Attachment {

    public AttachmentHolographicSite() {
        super("holographic");
    }

    @Override
    public void applyTransformations(IItemRenderer.ItemRenderType type, ItemStack item) {
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glScalef(0.15f, 0.15f, 0.15f);
        }

        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glScalef(0.25f, 0.25f, 0.25f);
            GL11.glRotatef(-40f, 0, 1, 0);
            GL11.glTranslatef(1f, 3f, 1f);
        }

        if(type == IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(0.35f, 0.35f, 0.35f);
            GL11.glTranslatef(1f, -1f, 0f);
        }

    }
}
