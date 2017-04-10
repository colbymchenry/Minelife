package com.minelife.gun.gun;

import com.minelife.gun.Ammo;
import com.minelife.gun.Gun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class M4 extends Gun {

    public M4(FMLPreInitializationEvent event) {
        super("m4", 4, event);
    }


    @Override
    public Ammo[] getPossibleAmmo() {
        return new Ammo[0];
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
                GL11.glScalef(0.5f,0.5f, 0.5f);
                GL11.glRotatef(310f, 0, 1, 0);
                GL11.glTranslatef(0.1f, 0f, 1f);
            }

            if(type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslatef(0.5f, -1.25f, 0f);
            }

            if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(0.5f, 0f, 0.1f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();
    }
}
