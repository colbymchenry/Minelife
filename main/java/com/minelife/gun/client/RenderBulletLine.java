package com.minelife.gun.client;

import com.minelife.realestate.util.GUIUtil;
import com.minelife.util.Vector;
import com.minelife.util.server.Callback;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderBulletLine implements Callback {

    private Vec3 origin;
    private Vec3 target;
    private Vec3 lookVec;

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        if (origin != null && target != null && lookVec != null) {
            Vector topLeft = new Vector(origin.xCoord, origin.yCoord, origin.zCoord);
            Vector bottomLeft = new Vector(origin.xCoord, origin.yCoord - 0.01, origin.zCoord);
            Vector topRight = new Vector(target.xCoord, target.yCoord, target.zCoord);
            GUIUtil.drawRect(Minecraft.getMinecraft(), topLeft, bottomLeft, null, topRight, event.partialTicks, Color.red, true);
        }

    }

    @Override
    public void callback(Object... objects) {
        origin = (Vec3) objects[0];
        target = (Vec3) objects[1];
    }

    public void doShot(Vec3 origin, Vec3 target, Vec3 lookVec) {
        this.origin = origin;
        this.target = target;
        this.lookVec = lookVec;
        new Thread(new RenderThread(origin, target, lookVec, this)).start();
    }

    class RenderThread implements Runnable {
        public Vec3 origin;
        public Vec3 target;
        public Vec3 lookVec;
        public Callback callback;

        public RenderThread(Vec3 origin, Vec3 target, Vec3 lookVec, Callback callback) {
            this.origin = origin;
            this.target = target;
            this.lookVec = lookVec;
            this.callback = callback;
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                origin = origin.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
                target = target.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
                callback.callback(origin, target);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    public static Vec3 shotFrom;
//    public static Vec3 currentPosVec;
//    public static float yaw;
//
//    @SubscribeEvent
//    public void onRender(RenderWorldLastEvent event) {
//        if(shotFrom != null && currentPosVec != null) {
//            Minecraft mc = Minecraft.getMinecraft();
////            Vector topLeft = new Vector(0, 0, 0);
////            Vector bottomLeft = new Vector(0,  -0.02, 0);
////            Vector topRight = new Vector(1, 0, 1);
//            Vector topLeft = new Vector(shotFrom.xCoord, shotFrom.zCoord, shotFrom.zCoord);
//            Vector bottomLeft = new Vector(shotFrom.xCoord, shotFrom.zCoord - 0.02, shotFrom.zCoord);
//            Vector topRight = new Vector(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord);
//            GUIUtil.drawRect(mc, topLeft, bottomLeft, null, topRight, event.partialTicks, Color.red, false);
////            GL11.glPushMatrix();
////            {
////                GL11.glTranslated(-mc.thePlayer.posX, -mc.thePlayer.posY, -mc.thePlayer.posZ);
////                GL11.glTranslated(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord);
////                float changeX = 85.7F;
////                float changeZ = -18.3F;
////                GL11.glTranslatef(changeX, 0f, changeZ);
////                double degree = Math.sin(yaw) - 50d;
////                GL11.glRotatef((float) degree, 0f, 1f, 0f);
////                GL11.glTranslatef(-changeX, 0f, -changeZ);
////                GL11.glTranslated(-currentPosVec.xCoord, -currentPosVec.yCoord, -currentPosVec.zCoord);
////                GUIUtil.drawRect(mc, topLeft, bottomLeft, null, topRight, event.partialTicks, Color.red, false);
////            }
////            GL11.glPopMatrix();
////            double doubleX = mc.thePlayer.posX - 0.5;
////            double doubleY = mc.thePlayer.posY + 0.1;
////            double doubleZ = mc.thePlayer.posZ - 0.5;
////
////            for(int i = 0; i < 2; i++) {
////                GL11.glPushMatrix();
////                GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
////                GL11.glColor4f(1f, 0, 0, 1f);
////                double mx = currentPosVec.xCoord;
////                double my = currentPosVec.yCoord;
////                double mz = currentPosVec.zCoord;
////                GL11.glBegin(GL11.GL_LINES);
////
////                GL11.glVertex3d(mx + 0.4f, my, mz + 0.4f);
////                GL11.glVertex3d(mx - 0.4f, my, mz - 0.4f);
////                GL11.glVertex3d(mx + 0.4f, my, mz - 0.4f);
////                GL11.glVertex3d(mx - 0.4f, my, mz + 0.4f);
////                GL11.glEnd();
////
////                GL11.glPopMatrix();
////            }
////            System.out.println("CALLED " + currentPosVec.xCoord + "," + currentPosVec.yCoord + "," + currentPosVec.zCoord);
////            Minecraft.getMinecraft().theWorld.setBlock((int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord, Blocks.diamond_block);
//        }
//    }
//
//    private void drawLine(boolean counterClockwise, float partialTickTime) {
////        GUIUtil.drawRect(Minecraft.getMinecraft());
////        GL11.glDisable(GL11.GL_TEXTURE_2D);
////        Minecraft minecraft = Minecraft.getMinecraft();
////        double playerX = minecraft.thePlayer.lastTickPosX + (minecraft.thePlayer.posX - minecraft.thePlayer.lastTickPosX) * partialTickTime;
////        double playerY = minecraft.thePlayer.lastTickPosY + (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY) * partialTickTime;
////        double playerZ = minecraft.thePlayer.lastTickPosZ + (minecraft.thePlayer.posZ - minecraft.thePlayer.lastTickPosZ) * partialTickTime;
////
////        GL11.glPushMatrix();
////        {
////            GL11.glTranslated(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord);
////
////            GL11.glColor4d(255d, 0d, 0d, 255d);
////            // Draw Border
////            GL11.glBegin(GL11.GL_QUADS);
////            GL11.glVertex3d(0, 0, 0);
////            GL11.glVertex3d(0, 1, 0);
////            GL11.glVertex3d(1, 1, 0);
////            GL11.glVertex3d(1, 0, 0);
//////            for (int z = 0; z < 2; z++)
//////                if (counterClockwise) GL11.glVertex3d(0, 1, 0);
//////                else GL11.glVertex3d(0, 1, 0);
//////            for (int z1 = 0; z1 < 2; z1 ++)
//////                GL11.glVertex3d(height.getX() + length.getX(), height.getY() + length.getY(), height.getZ() + length.getZ());
//////            for (int z2 = 0; z2 < 2; z2 ++)
//////                if (counterClockwise) GL11.glVertex3d(length.getX(), length.getY(), length.getZ());
//////                else GL11.glVertex3d(1, 1, height.getZ());
////            GL11.glVertex3d(0, 0, 0);
////            GL11.glEnd();
////        }
////        GL11.glPopMatrix();
////
////        GL11.glEnable(GL11.GL_TEXTURE_2D);
//    }

}
