package com.minelife.clothing;

import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiDesigner extends GuiScreen {

    private RenderPlayer renderPlayer = new RenderPlayer();
    private EntityFakePlayer fakePlayer;
    private int xPosition, yPosition, scale, rotX, rotY;

    public GuiDesigner(World world) {
        fakePlayer = new EntityFakePlayer(world);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition, yPosition + 20, zLevel);
            float xOffset = 4;
            float yOffset = -60;
            float zOffset = 50;
            GL11.glTranslatef(xOffset, yOffset, zOffset);
            GL11.glRotatef(rotY, 0, 1, 0);
            GL11.glRotatef(rotX, 1, 0, 0);
            GL11.glTranslatef(-xOffset, -yOffset, -zOffset);
            renderPlayer(0, 0, 60);
        }
        GL11.glPopMatrix();

        handleRotation();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = this.width / 2;
        yPosition = this.height / 2;
        scale = 40;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    private void handleRotation() {
        int dX = Mouse.getDX();
        int dY = Mouse.getDY();

        if(Mouse.isButtonDown(0)) {
            rotX += dY;
            rotY += dX;
        }
    }

    private void renderPlayer(float x, float y, float scale) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, 50.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = fakePlayer.renderYawOffset;
        float f3 = fakePlayer.rotationYaw;
        float f4 = fakePlayer.rotationPitch;
        float f5 = fakePlayer.prevRotationYawHead;
        float f6 = fakePlayer.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan((double) (0 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        fakePlayer.renderYawOffset = (float) Math.atan((double) (0 / 40.0F)) * 20.0F;
        fakePlayer.rotationYaw = (float) Math.atan((double) (0 / 40.0F)) * 40.0F;
        fakePlayer.rotationPitch = -((float) Math.atan((double) (0 / 40.0F))) * 20.0F;
        fakePlayer.rotationYawHead = fakePlayer.rotationYaw;
        fakePlayer.prevRotationYawHead = fakePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, fakePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(fakePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        fakePlayer.renderYawOffset = f2;
        fakePlayer.rotationYaw = f3;
        fakePlayer.rotationPitch = f4;
        fakePlayer.prevRotationYawHead = f5;
        fakePlayer.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
