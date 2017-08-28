package com.minelife.gun.client;

import com.google.common.collect.Lists;
import com.minelife.realestate.util.GUIUtil;
import com.minelife.util.Vector;
import com.minelife.util.server.Callback;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.Color;
import java.util.List;
import java.util.ListIterator;

public class RenderBulletLine {

    private RenderThread renderThread;
    private volatile List<Bullet> bulletList = Lists.newArrayList();

    public RenderBulletLine() {
        new Thread(renderThread = new RenderThread()).start();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {

        ListIterator iterator = bulletList.listIterator();

        Bullet bullet;
        while(iterator.hasNext()) {
            bullet = (Bullet) iterator.next();
            Vector topLeft = new Vector(bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord);
            Vector bottomLeft = new Vector(bullet.origin.xCoord, bullet.origin.yCoord - 0.01, bullet.origin.zCoord);
            Vector topRight = new Vector(bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord);
            GUIUtil.drawRect(Minecraft.getMinecraft(), topLeft, bottomLeft, null, topRight, event.partialTicks, Color.red, true);
        }

    }

    public void shot(Vec3 origin, Vec3 target, Vec3 lookVec) {
        bulletList.add(new Bullet(origin, target, lookVec));
    }

    class RenderThread implements Runnable {

        @Override
        public void run() {

            while (true) {
                ListIterator iterator = bulletList.listIterator();

                Bullet bullet;
                while (iterator.hasNext()) {
                    bullet = (Bullet) iterator.next();
                    bullet.origin = bullet.origin.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);
                    bullet.target = bullet.target.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);
                }

                try {
                    Thread.sleep(5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Bullet {
        public Vec3 origin, target, lookVec;

        public Bullet(Vec3 origin, Vec3 target, Vec3 lookVec) {
            this.origin = origin;
            this.target = target;
            this.lookVec = lookVec;
        }
    }

}
