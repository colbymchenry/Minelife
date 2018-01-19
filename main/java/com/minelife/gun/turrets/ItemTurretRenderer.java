package com.minelife.gun.turrets;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class ItemTurretRenderer implements IItemRenderer {


    ResourceLocation TexLegs, TexBodyOn, TexBodyOff;
    ResourceLocation OBJLegs, OBJBody;
    IModelCustom ModelLegs, ModelBody;

    public ItemTurretRenderer() {
        TexLegs = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_legs.png");
        TexBodyOn = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_body_on.png");
        TexBodyOff = new ResourceLocation(Minelife.MOD_ID, "textures/guns/turrets/turret_body_off.png");
        OBJLegs = new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_legs.obj");
        OBJBody = new ResourceLocation(Minelife.MOD_ID, "models/guns/turrets/turret_body.obj");
        ModelLegs = AdvancedModelLoader.loadModel(OBJLegs);
        ModelBody = AdvancedModelLoader.loadModel(OBJBody);
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

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glRotatef(270f, 0, 1, 0);
                GL11.glTranslatef(-0.5f, 0.5f, -0.5f);
            }

            if(type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.8f, 0.8f, 0.8f);
                GL11.glTranslatef(-0.9f, -0.3f, 0f);
            }

            if(type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(-0.1f, 0.2f, 0.5f);
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(TexBodyOff);
            ModelBody.renderAll();

            GL11.glScalef(1.2f, 1.2f, 1.2f);
            GL11.glTranslatef(-0.15f, -0.65f, 0.1f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TexLegs);
            ModelLegs.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}