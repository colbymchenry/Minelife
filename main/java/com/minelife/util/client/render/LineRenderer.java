package com.minelife.util.client.render;

import com.minelife.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class LineRenderer {

    public static void drawCuboidAroundsBlocks(Minecraft minecraft, AxisAlignedBB bounds, float partialTickTime, Color color, boolean depth_test_faces, boolean depth_test_borders) {
        Vector start = new Vector(bounds.minX, bounds.minY, bounds.minZ);
        Vector end = new Vector(bounds.maxX, bounds.maxY, bounds.maxZ);
        drawCuboidAroundBlocks(minecraft, start, end, partialTickTime, color, depth_test_faces, depth_test_borders);
    }

    public static void drawCuboidAroundBlocks(Minecraft minecraft, Vector start, Vector end, float partialTickTime, Color color, boolean depth_test_faces, boolean depth_test_borders) {

        Vector smallest = new Vector(Math.min(start.getBlockX(), end.getBlockX()), Math.min(start.getBlockY(), end.getBlockY()), Math.min(start.getBlockZ(), end.getBlockZ()));

        Vector largest = new Vector(Math.max(start.getBlockX(), end.getBlockX()) + 1, Math.max(start.getBlockY(), end.getBlockY()) + 1, Math.max(start.getBlockZ(), end.getBlockZ()) + 1);

        renderCuboid(minecraft, smallest, largest, partialTickTime, color, depth_test_faces, depth_test_borders);

    }

    public static void drawCuboid(Minecraft minecraft, Vector start, Vector end, float partialTickTime, Color color, boolean depth_test_faces, boolean depth_test_borders) {

        Vector smallest = new Vector(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));

        Vector largest = new Vector(Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));

        renderCuboid(minecraft, smallest, largest, partialTickTime, color, depth_test_faces, depth_test_borders);

    }

    private static void renderCuboid(Minecraft minecraft, Vector start, Vector end, float partialTickTime, Color color, boolean depth_test_faces, boolean depth_test_borders) {

        Vector[] points = new Vector[4];

        // Top
        points[0] = new Vector(start.getX(), end.getY(), start.getZ());
        points[1] = new Vector(start.getX(), end.getY(), end.getZ());
        points[2] = new Vector(end.getX(), end.getY(), end.getZ());
        points[3] = new Vector(end.getX(), end.getY(), start.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

        // Bottom

        points[0] = start;
        points[1] = new Vector(start.getX(), start.getY(), end.getZ());
        points[2] = new Vector(end.getX(), start.getY(), end.getZ());
        points[3] = new Vector(end.getX(), start.getY(), start.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

        // Face 1

        points[0] = start;
        points[1] = new Vector(start.getX(), start.getY(), end.getZ());
        points[2] = new Vector(start.getX(), end.getY(), end.getZ());
        points[3] = new Vector(start.getX(), end.getY(), start.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

        // Face 2

        points[0] = new Vector(start.getX(), start.getY(), end.getZ());
        points[1] = new Vector(end.getX(), start.getY(), end.getZ());
        points[2] = new Vector(end.getX(), end.getY(), end.getZ());
        points[3] = new Vector(start.getX(), end.getY(), end.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

        // Face 3

        points[0] = new Vector(end.getX(), start.getY(), end.getZ());
        points[1] = new Vector(end.getX(), start.getY(), start.getZ());
        points[2] = new Vector(end.getX(), end.getY(), start.getZ());
        points[3] = new Vector(end.getX(), end.getY(), end.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

        // Face 4

        points[0] = new Vector(end.getX(), start.getY(), start.getZ());
        points[1] = new Vector(start.getX(), start.getY(), start.getZ());
        points[2] = new Vector(start.getX(), end.getY(), start.getZ());
        points[3] = new Vector(end.getX(), end.getY(), start.getZ());

        drawRect(minecraft, points[0], points[1], points[2], points[3], partialTickTime, color, depth_test_faces, depth_test_borders);

    }

    public static void drawRect(Minecraft minecraft, Vector topLeft, Vector bottomLeft, Vector bottomRight, Vector topRight, float partialTickTime, Color color, boolean depth_test_faces, boolean depth_test_borders) {

        double playerX = minecraft.thePlayer.lastTickPosX + (minecraft.thePlayer.posX - minecraft.thePlayer.lastTickPosX) * partialTickTime;
        double playerY = minecraft.thePlayer.lastTickPosY + (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY) * partialTickTime;
        double playerZ = minecraft.thePlayer.lastTickPosZ + (minecraft.thePlayer.posZ - minecraft.thePlayer.lastTickPosZ) * partialTickTime;

        double dx = topLeft.getX() - playerX;
        double dy = topLeft.getY() - playerY;
        double dz = topLeft.getZ() - playerZ;

        Vector height = new Vector().copy(bottomLeft).subtract(topLeft);
        Vector length = new Vector().copy(topRight).subtract(topLeft);

        Vector dr = new Vector().copy(height).crossProduct(length).normalize().multiply(1.0 / 1000.0);

        Vector translate = new Vector(dx, dy, dz);

        GL11.glPushMatrix();
        drawRectSetUp();

        GL11.glPushMatrix();
        drawRectRenderRect(new Vector().copy(translate).add(dr), length, height, true, color.getRGB(), depth_test_faces, depth_test_borders);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        drawRectRenderRect(new Vector().copy(translate).subtract(dr), length, height, false, color.getRGB(), depth_test_faces, depth_test_borders);
        GL11.glPopMatrix();

        drawRectCleanUp();
        GL11.glPopMatrix();

    }

    private static void drawRectSetUp() {

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
//        if (!depth_test) GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(4);

    }

    private static void drawRectRenderRect(Vector translate, Vector length, Vector height, boolean counterClockwise, int color, boolean depth_test_faces, boolean depth_test_borders) {
        double red = (color >> 16 & 255) / 255.0;
        double green = (color >> 8 & 255) / 255.0;
        double blue = (color & 255) / 255.0;
        double alpha = (color >> 24 & 255) / 255.0;

        GL11.glColor4d(red, green, blue, alpha);

        GL11.glTranslated(translate.getX(), translate.getY(), translate.getZ());

        if(!depth_test_faces) GL11.glDisable(GL11.GL_DEPTH_TEST);

        // Draw Rect
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(0, 0, 0);
        if (counterClockwise) GL11.glVertex3d(height.getX(), height.getY(), height.getZ());
        else GL11.glVertex3d(length.getX(), length.getY(), length.getZ());
        GL11.glVertex3d(height.getX() + length.getX(), height.getY() + length.getY(), height.getZ() + length.getZ());
        if (counterClockwise) GL11.glVertex3d(length.getX(), length.getY(), length.getZ());
        else GL11.glVertex3d(height.getX(), height.getY(), height.getZ());
        GL11.glEnd();

        if(!depth_test_faces) GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glColor4d(red, green, blue, 2 * alpha);

        if(!depth_test_borders) GL11.glDisable(GL11.GL_DEPTH_TEST);
        // Draw Border
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, 0, 0);
        for (int i = 0; i < 2; i ++)
            if (counterClockwise) GL11.glVertex3d(height.getX(), height.getY(), height.getZ());
            else GL11.glVertex3d(length.getX(), length.getY(), length.getZ());
        for (int i = 0; i < 2; i ++)
            GL11.glVertex3d(height.getX() + length.getX(), height.getY() + length.getY(), height.getZ() + length.getZ());
        for (int i = 0; i < 2; i ++)
            if (counterClockwise) GL11.glVertex3d(length.getX(), length.getY(), length.getZ());
            else GL11.glVertex3d(height.getX(), height.getY(), height.getZ());
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();

        if(!depth_test_borders) GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static void drawRectCleanUp() {

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(1, 1, 1, 1);
        GL11.glColor4d(1, 1, 1, 1);
        GL11.glDepthMask(true);
//        if (!depth_test) GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

}
