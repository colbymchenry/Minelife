package com.minelife.capes.client;

import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.CapeLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiPreviewCape extends GuiScreen {

    private static ResourceLocation template = new ResourceLocation(Minelife.MOD_ID, "textures/capes/template.png");
    private static ModelBiped modelBipedMain;
    private int xPosition, yPosition, rotX, rotY;

    public GuiPreviewCape() {
        modelBipedMain = new ModelBiped();
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        mc.getTextureManager().bindTexture(template);
        GL11.glPushMatrix();
        GL11.glTranslatef(xPosition, yPosition, zLevel);
        float xOffset = 0;
        float yOffset = 25;
        float zOffset = 0;

        GL11.glTranslatef(xOffset, yOffset, zOffset);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GuiUtil.drawImage(0, 0, 8, 8);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glRotatef(rotY, 0, 1, 0);
        GL11.glRotatef(rotX, 1, 0, 0);
        GL11.glTranslatef(-xOffset, -yOffset, -zOffset);
        GL11.glScalef(81, 81, 81);

        this.modelBipedMain.renderCloak(0.0625F);
        GL11.glPopMatrix();

        handleRotation();
    }



    @Override
    public void initGui() {
        xPosition = (this.width / 2);
        yPosition = (this.height / 2);
    }


    private void handleRotation() {
        int dX = Mouse.getDX();
        int dY = Mouse.getDY();

        if(Mouse.isButtonDown(0)) {
            rotX += dY;
            rotY += dX;
        }
    }
}
