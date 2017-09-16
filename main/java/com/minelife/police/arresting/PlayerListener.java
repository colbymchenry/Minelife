package com.minelife.police.arresting;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.gun.server.ShootBulletEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.lwjgl.input.Keyboard;

public class PlayerListener {

    @SubscribeEvent
    public void onEntityClick(EntityInteractEvent event) {
        if (!(event.target instanceof EntityPlayer)) return;
        if (event.entityPlayer == null) return;

        EntityPlayer officer = event.entityPlayer;
        EntityPlayer target = (EntityPlayer) event.target;

        boolean holdingHandcuffs = officer.getHeldItem() != null && officer.getHeldItem().getItem() == MLItems.handcuff;
        boolean isArrested = ArrestingHandler.isArrested(target);

        if (!holdingHandcuffs && isArrested) {
            target.mountEntity(officer);
        } else {
            if (!isArrested)
                ArrestingHandler.arrestPlayer(target);
            else
                ArrestingHandler.freePlayer(target);
        }
    }
    /*
    Stop damage when dropping players
     */
    @SubscribeEvent
    public void onDamageTaken(LivingFallEvent event) {
        if(event.entity instanceof EntityPlayer) {
            if(ArrestingHandler.isArrested((EntityPlayer) event.entity)) {
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRightClick(MouseEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (event.button == 1 && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Minecraft.getMinecraft().thePlayer.riddenByEntity != null) {
            Minelife.NETWORK.sendToServer(new PacketDropPlayer());
        }
    }

    @SubscribeEvent
    public void onShoot(ShootBulletEvent event) {
        if (event.getEntityShooter() instanceof EntityPlayer)
            if (ArrestingHandler.isArrested((EntityPlayer) event.getEntityShooter())) event.setCanceled(true);
    }

}
