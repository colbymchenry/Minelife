package com.minelife.realestate.util;

import com.minelife.util.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;

@SideOnly(Side.CLIENT)
public class PlayerUtil {

    public static void sendTo(EntityPlayer player, String message) {

        player.addChatComponentMessage(new ChatComponentText(message));

    }

    public static Vector getBlockCoordinatesInFocus() {

        MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;

        return (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) ? new Vector(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ) : null;

//        Vec3 vec3 = player.getPosition(1.0F);
//        Vec3 lookVec = player.getLook(1.0F);
//        Vec3 addedVector = vec3.addVector(lookVec.xCoord * 10, lookVec.yCoord * 10, lookVec.zCoord * 10);
//
//        MovingObjectPosition movingObjPos = player.worldObj.func_147447_a(vec3, addedVector, false, false, true);
//
//        if (movingObjPos != null && movingObjPos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) return new Vector(movingObjPos.blockX, movingObjPos.blockY, movingObjPos.blockZ);
//
//        return null;

    }

}
