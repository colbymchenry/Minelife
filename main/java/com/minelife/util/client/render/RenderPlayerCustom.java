package com.minelife.util.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class RenderPlayerCustom extends RenderPlayer {

    public RenderPlayerCustom() {
        super();
        this.mainModel = new ModelBipedCustom(0.0F);
        this.modelBipedMain = (ModelBiped) this.mainModel;
        this.modelArmorChestplate = new ModelBipedCustom(1.0F);
        this.modelArmor = new ModelBipedCustom(0.5F);
    }

    @Override
    public void doRender(AbstractClientPlayer client, double x, double y, double z, float f, float f1) {

        boolean arrested = client.getEntityData().hasKey("arrested") ? client.getEntityData().getBoolean("arrested") : false;
        if (arrested) {
            GL11.glPushMatrix();
            {
                float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
                GL11.glRotatef(-yaw, 0, 1, 0);
                super.doRender(client, x + 0.5f, y - 2.5f, z + 0.5f, f, f1);
            }
            GL11.glPopMatrix();

            return;
        }

        super.doRender(client, x, y, z, f, f1);
    }

    @Override
    public void renderFirstPersonArm(EntityPlayer player) {
        boolean arrested = player.getEntityData().hasKey("arrested") ? player.getEntityData().getBoolean("arrested") : false;
        if (!arrested) super.renderFirstPersonArm(player);
    }

    @Override
    protected void renderEquippedItems(AbstractClientPlayer p_77029_1_, float p_77029_2_) {
        boolean arrested = p_77029_1_.getEntityData().hasKey("arrested") ? p_77029_1_.getEntityData().getBoolean("arrested") : false;
        if (!arrested) super.renderEquippedItems(p_77029_1_, p_77029_2_);
    }

    protected float renderSwingProgress(EntityLivingBase p_77040_1_, float p_77040_2_) {
        boolean arrested = p_77040_1_.getEntityData().hasKey("arrested") ? p_77040_1_.getEntityData().getBoolean("arrested") : false;
        return arrested ? 0 : p_77040_1_.getSwingProgress(p_77040_2_);
    }

}
