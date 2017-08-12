package com.minelife.util.client;

import com.minelife.util.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.nio.FloatBuffer;

public class GuiUtil {

    private static final Vec3 LIGHT0_POS = (Vec3.createVectorHelper(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    private static final Vec3 LIGHT1_POS = (Vec3.createVectorHelper(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

    public static void drawSquareInWorld(Minecraft mc, int x, int y, int z, float width, float height, float rotY, float partialTickTime, int color)
    {
        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x - playerX;
        float dy = y - playerY;
        float dz = z - playerZ;

        GL11.glColor4f(1f, 1f, 1f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float blue = (float) (color >> 8 & 255) / 255.0F;
        float green = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 0.5F);

        GL11.glVertex3f(0, 0, 0.0F);
        GL11.glVertex3f(0, height, 0.0F);
        GL11.glVertex3f(width, height, 0.0F);
        GL11.glVertex3f(width, 0, 0.0F);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    public static void drawImage(float x, float y, float width, float height)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, 0.0, 1.0);
        tessellator.addVertexWithUV(x + width, y + height, 0, 1.0, 1.0);
        tessellator.addVertexWithUV(x + width, y, 0, 1.0, 0.0);
        tessellator.addVertexWithUV(x, y, 0, 0.0, 0.0);
        tessellator.draw();
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    @SideOnly(Side.CLIENT)
    private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);

    private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_)
    {
        colorBuffer.clear();
        colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
        colorBuffer.flip();
        /** Float buffer used to set OpenGL material colors */
        return colorBuffer;
    }

    public static void setBrightness(float f, float f1, float f2)
    {
        setBrightness((float) LIGHT0_POS.xCoord, (float) LIGHT0_POS.yCoord, (float) LIGHT0_POS.zCoord, (float) LIGHT1_POS.xCoord, (float) LIGHT1_POS.yCoord, (float) LIGHT1_POS.zCoord, f, f1, f2);
    }

    public static void setBrightness(float x1, float y1, float z1, float x2, float y2, float z2, float f, float f1, float f2)
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(1032, 5634);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(x1, y1, z1, 0.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(x2, y2, z2, 0.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, setColorBuffer(f1, f1, f1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(f2, f2, f2, 1.0F));
        GL11.glShadeModel(7424);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(f, f, f, 1.0F));
        GL11.glEnable(GL11.GL_BLEND);
    }

    public static void drawGradientRect(double x, double y, double width, double height, int color1, int color2, double zLevel)
    {
        float f = (float) (color1 >> 24 & 255) / 255.0F;
        float f1 = (float) (color1 >> 16 & 255) / 255.0F;
        float f2 = (float) (color1 >> 8 & 255) / 255.0F;
        float f3 = (float) (color1 & 255) / 255.0F;
        float f4 = (float) (color2 >> 24 & 255) / 255.0F;
        float f5 = (float) (color2 >> 16 & 255) / 255.0F;
        float f6 = (float) (color2 >> 8 & 255) / 255.0F;
        float f7 = (float) (color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(x + width, y, zLevel);
        tessellator.addVertex(x, y, zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(x, y + height, zLevel);
        tessellator.addVertex(x + width, y + height, zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawGradientRect(double x, double y, double width, double height, int color1, int color2)
    {
        drawGradientRect(x, y, width, height, color1, color2, 300);
    }

    public static int[] drawHoveringText(java.util.List<String> textLines, int x, int y, int screenWidth, int screenHeight)
    {
        return drawHoveringText(textLines, x, y, screenWidth, screenHeight, 300.0F);
    }

    public static int[] drawHoveringText(java.util.List<String> textLines, int x, int y, int screenWidth, int screenHeight, float zLevel)
    {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRender = RenderItem.getInstance();

        if (!textLines.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
//            GL11.glEnable(GL11.GL_DEPTH);

            int width = 0;

            for (String s : textLines) {
                int j = mc.fontRenderer.getStringWidth(s);

                if (j > width) {
                    width = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int height = 8;

            if (textLines.size() > 1) {
                height += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + width > screenWidth) {
                l1 -= 28 + width;
            }

            if (i2 + height + 6 > screenHeight) {
                i2 = screenHeight - height - 6;
            }
            int k = 1347420415;
            GuiUtil.drawDefaultBackground(l1 - 6, i2 - 6, width + 10, height + 10, new Color(40, 0, 127, 255));

            GL11.glPushMatrix();
            {
                GL11.glTranslatef(0, 0, zLevel + 400.0F);
                for (int k1 = 0; k1 < textLines.size(); ++k1) {
                    String s1 = textLines.get(k1);
                    mc.fontRenderer.drawStringWithShadow(s1, l1, i2, -1);

                    if (k1 == 0) {
                        i2 += 2;
                    }

                    i2 += 10;
                }
            }
            GL11.glPopMatrix();

            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
//            GL11.glEnable(GL11.GL_DEPTH);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            return new int[]{20, 20};
        }


        return new int[]{0, 0};
    }

    public static void drawSquareInWorld(int x, int y, int z, float width, float height, float rotY, float partialTickTime, int color)
    {
        Minecraft mc = Minecraft.getMinecraft();

        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x - playerX;
        float dy = y - playerY;
        float dz = z - playerZ;

        GL11.glColor4f(1f, 1f, 1f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 0.5F);

        GL11.glVertex3f(0, 0, 0.0F);
        GL11.glVertex3f(0, height, 0.0F);
        GL11.glVertex3f(width, height, 0.0F);
        GL11.glVertex3f(width, 0, 0.0F);

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    /**
     * Draws the entity to the screen. Args: xPos, yPos, scale, mouseX, mouseY, entityLiving
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        mouseX *= -1;
        mouseY *= -1;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) posX, (float) posY, 100.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
//        GL11.glTranslatef(0.0F, ent.yOffset, 0.0F);
        RenderManager rendermanager = RenderManager.instance;
        rendermanager.playerViewY = 180.0F;
        rendermanager.func_147939_a(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static final void drawDefaultBackground(float x, float y, float width, float height) {
        drawDefaultBackground(x, y, width, height, new Color(0xC6C6C6));
    }

    public static final void drawDefaultBackground(float x, float y, float width, float height, Color color) {
        Color bottomBorder = color.darker().darker();
        Color topColor = color.brighter().brighter().brighter().brighter().brighter().brighter().brighter();
        Color border = color.darker().darker().darker().darker().darker().darker().darker();

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // TODO: Seth see if you can make the alpha work correctly. Typically it just makes the thing see through no matter the alpha
//        GL11.glEnable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0f);

        GL11.glColor4f(color.getRed()/ 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        /* draw fill */
        {
            // draw top 1st line fill
            drawImage(2, 1, width - 4, 1);
            // draw top 2nd line fill
            drawImage( 1,  2, width - 2, 1);
            // draw fill to bottom curve
            drawImage(1,  3, width - 1, height - 5);
            // draw bottom 1st line curve
            drawImage(2, height - 2, width - 2, 1);
            // draw bottom 2nd line curve
            drawImage(3, height - 1, width - 4, 1);
        }

        GL11.glColor4f(border.getRed()/ 255f, border.getGreen() / 255f, border.getBlue() / 255f, border.getAlpha() / 255f);
        /* draw outline */
        {
            // draw top line
            drawImage(2, 0, width - 4, 1);
            // draw bottom line
            drawImage(3,  height, width - 4, 1);
            // draw left line
            drawImage(0, 2, 1, height - 3);
            // draw right line
            drawImage(width,  3, 1, height - 4);
            // draw top left pixel curve connector
            drawImage(1, 1, 1, 1);
            // draw bottom left pixel curve connector
            drawImage(1, height - 2, 1, 1);
            // draw bottom left pixel curve connector 2
            drawImage(2, height - 1, 1, 1);
            // draw top right pixel curve connector 1
            drawImage( width - 2, 1, 1, 1);
            // draw top right pixel curve connector 2
            drawImage(width - 1,  2, 1, 1);
            // draw bottom right pixel curve connector
            drawImage( width - 1,  height - 1, 1, 1);
        }

        GL11.glColor4f(topColor.getRed()/ 255f, topColor.getGreen() / 255f, topColor.getBlue() / 255f, topColor.getAlpha() / 255f);
        /* draw top color */
        {
            // draw top block
            drawImage(2, 1, width - 4, 2);
            // draw left block
            drawImage(1, 2, 2, height - 4);
            // draw connector
            drawImage(3,3, 1, 1);
        }

        GL11.glColor4f(bottomBorder.getRed()/ 255f, bottomBorder.getGreen() / 255f, bottomBorder.getBlue() / 255f, bottomBorder.getAlpha() / 255f);
        /* draw bottom color */
        {
            // draw bottom block
            drawImage(3, height - 2, width - 4, 2);
            // draw right block
            drawImage(width -  2, 3, 2, height - 4);
            // draw connector
            drawImage(width - 3,height - 3, 1, 1);
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static int[] transition(Color far, Color close, double ratio) {
        int red = (int) Math.abs((ratio * far.getRed()) + ((1 - ratio) * close.getRed()));
        int green = (int) Math.abs((ratio * far.getGreen()) + ((1 - ratio) * close.getGreen()));
        int blue = (int) Math.abs((ratio * far.getBlue()) + ((1 - ratio) * close.getBlue()));
        return new int[]{red, green, blue};
    }

    public static void render_item_in_world(Minecraft mc, ItemStack item) {
        GL11.glPushMatrix();
        {
            IIcon iicon = mc.thePlayer.getItemIcon(item, 0);

            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }

            mc.getTextureManager().bindTexture(mc.getTextureManager().getResourceLocation(item.getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            float min_x = iicon.getMinU();
            float max_x = iicon.getMaxU();
            float min_y = iicon.getMinV();
            float max_y = iicon.getMaxV();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-0.5f, 0f, 0.0F);
            float scale = 0.9F;
            GL11.glScalef(scale, scale, scale);
            ItemRenderer.renderItemIn2D(tessellator, max_x, min_y, min_x, max_y, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        GL11.glPopMatrix();
    }

}
