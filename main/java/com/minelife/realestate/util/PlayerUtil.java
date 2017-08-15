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
    }

}