package com.minelife.gun.client.attachments;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class Attachment2xSite extends Attachment {

    public Attachment2xSite() {
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
    }
}
