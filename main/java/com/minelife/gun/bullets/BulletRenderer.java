package com.minelife.gun.bullets;

import com.minelife.util.Vector;
import com.minelife.util.client.render.LineRenderer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.awt.*;
import java.util.Iterator;

public class BulletRenderer {

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        Iterator iterator = BulletHandler.bulletList.iterator();

        Bullet bullet;
        while(iterator.hasNext()) {
            bullet = (Bullet) iterator.next();
            Vector topLeft = new Vector(bullet.originX, bullet.originY, bullet.originZ);
            Vector bottomLeft = new Vector(bullet.originX, bullet.originY - 0.01, bullet.originZ);
            Vector topRight = new Vector(bullet.targetX, bullet.targetY, bullet.targetZ);
            LineRenderer.drawRect(Minecraft.getMinecraft(), topLeft, bottomLeft, null, topRight, event.partialTicks, Color.orange, true, true);
        }

    }

}
