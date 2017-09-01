package com.minelife.police.client;

import com.minelife.realestate.util.GUIUtil;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BlurMaster {

    /** A buffer to hold pixel values returned by OpenGL. */
    private static IntBuffer pixelBuffer;
    /** The built-up array that contains all the pixel values returned by OpenGL. */
    private static int[] pixelValues;

    public static void blur(Minecraft mc, int x, int y, int displayWidth, int displayHeight, Framebuffer framebuffer) {
        int width = displayWidth, height = displayHeight;

        if (OpenGlHelper.isFramebufferEnabled()) {
            width = framebuffer.framebufferTextureWidth;
            height = framebuffer.framebufferTextureHeight;
        }

        int k = width * height;

        if (pixelBuffer == null || pixelBuffer.capacity() < k)
        {
            pixelBuffer = BufferUtils.createIntBuffer(k);
        }

        pixelValues = new int[k];

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.func_147953_a(pixelValues, width, height);
        BufferedImage bufferedimage = null;

        if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedimage = new BufferedImage(framebuffer.framebufferWidth, framebuffer.framebufferHeight, 1);
            int l = framebuffer.framebufferTextureHeight - framebuffer.framebufferHeight;

            for (int i1 = l; i1 < framebuffer.framebufferTextureHeight; ++i1) {
                for (int j1 = 0; j1 < framebuffer.framebufferWidth; ++j1) {
                    bufferedimage.setRGB(j1, i1 - l, pixelValues[i1 * framebuffer.framebufferTextureWidth + j1]);
                }
            }
        } else {
            bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
        }

        pixelValues = null;

        try {
            DynamicTexture texture = new DynamicTexture(bufferedimage);

//            GL11.glColor4f(0, 1, 1, 1);
            ScaledResolution scaledResolution = new ScaledResolution(mc, Display.getWidth(), Display.getHeight());
//            GL11.glEnable(GL11.GL_SCISSOR_TEST);
//            GL11.glScissor(x, y, displayWidth, displayHeight);
//            GuiUtil.drawImage(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
//            GL11.glDisable(GL11.GL_SCISSOR_TEST);
//            GL11.glColor4f(1, 1, 1, 1);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColorMask(true, true, true, false);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            byte b0 = 4;

            for (int i = 0; i < b0; ++i) {
                tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (i + 1));
                float tessWidth = scaledResolution.getScaledWidth();
                float tessHeight = scaledResolution.getScaledHeight();
                float zLevel = 0;
                float blueOffset = (float) (i - b0 / 2) / 256.0F;
                tessellator.addVertexWithUV(0.0D, (double) tessHeight, (double) zLevel, (double) (1.0F + blueOffset), 0.0D);
                tessellator.addVertexWithUV((double) tessWidth, (double) tessHeight, (double) zLevel, (double) (0.0F + blueOffset), 0.0D);
                tessellator.addVertexWithUV((double) tessWidth, (double) 0.0D, (double) zLevel, (double) (0.0F + blueOffset), 1.0D);
                tessellator.addVertexWithUV((double) 0.0D, 0.0D, (double) zLevel, (double) (1.0F + blueOffset), 1.0D);

            }

            tessellator.draw();

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glColorMask(true, true, true, true);

            pixelBuffer.reset();
            pixelBuffer.clear();
            pixelBuffer = null;
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
