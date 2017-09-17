package com.minelife.police.arresting;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.gun.server.ShootBulletEvent;
import com.minelife.police.ModPolice;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import org.lwjgl.input.Keyboard;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerListener {

    public static final PlayerListener instance;

    static {
        instance = new PlayerListener();
    }

    private PlayerListener() {
    }

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
        if (event.entity instanceof EntityPlayer) {
            if (ArrestingHandler.isArrested((EntityPlayer) event.entity)) {
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

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            ResultSet resultLockup = Minelife.SQLITE.query("SELECT * FROM policelockup WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
            ResultSet resultPardon = Minelife.SQLITE.query("SELECT * FROM policepardon WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
            if (resultPardon.next()) {
                ModPolice.getServerProxy().sendToPrisonExit((EntityPlayerMP) event.player);
                event.player.addChatComponentMessage(new ChatComponentText("You have been pardoned of all crimes!"));
                Minelife.SQLITE.query("DELETE FROM policelockup WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
                Minelife.SQLITE.query("DELETE FROM policepardon WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
            } else if (resultLockup.next()) {
                ModPolice.getServerProxy().sendToPrison((EntityPlayerMP) event.player);
                event.player.addChatComponentMessage(new ChatComponentText("You have been locked up!"));
                Minelife.SQLITE.query("DELETE FROM policelockup WHERE playerUUID='" + event.player.getUniqueID().toString() + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
