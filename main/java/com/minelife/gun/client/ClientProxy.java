package com.minelife.gun.client;

import com.minelife.MLItems;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.gun.bullets.BulletRenderer;
import com.minelife.gun.client.attachments.Attachment;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.attachments.ItemSite;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketMouseClick;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new KeyStrokeListener());
        FMLCommonHandler.instance().bus().register(new TickHandler());
        MinecraftForge.EVENT_BUS.register(new OverlayRenderer());
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        ItemGun.registerRenderers();
        MinecraftForge.EVENT_BUS.register(new BulletRenderer());
        Attachment.registerRenderers();
    }

    /**
     * This method is put in place to prevent the player from interacting with blocks while holding a guns
     */
    @SubscribeEvent
    public void onClick(MouseEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return;

        if (ItemGunClient.modifying) return;

        if (event.button == 1) {
            ItemGunClient.aimingDownSight = !ItemGunClient.aimingDownSight;
            event.setCanceled(true);
            return;
        }

        if (event.dwheel != 0) {
            ItemGunClient.aimingDownSight = false;
            return;
        }

        event.setCanceled(true);

        if (event.button == 0 && event.buttonstate && !((ItemGun) heldItem.getItem()).isFullAuto()) {
            Minelife.NETWORK.sendToServer(new PacketMouseClick());
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return;

        if (ItemGunClient.modifying) return;

        if (Minecraft.getMinecraft().currentScreen != null) {
            ItemGunClient.aimingDownSight = false;
            return;
        }

        if (Mouse.isButtonDown(0) && ((ItemGun) heldItem.getItem()).isFullAuto()) {
            Minelife.NETWORK.sendToServer(new PacketMouseClick());
        }

    }

    @SubscribeEvent
    public void fovUpdate(FOVUpdateEvent event) {
        if (event.entity.getHeldItem() != null && event.entity.getHeldItem().getItem() instanceof ItemGun) {
            if (ItemGunClient.aimingDownSight) {
                if (ItemGun.getSite(event.entity.getHeldItem()) != null) {
                    ItemSite site = (ItemSite) ItemGun.getSite(event.entity.getHeldItem()).getItem();
                    if (site == MLItems.holographicSite) {
                        event.newfov = 0.7F;
                    } else if (site == MLItems.twoXSite) {
                        event.newfov = 0.5F;
                    } else if (site == MLItems.acogSite) {
                        event.newfov = 0.33F;
                    }
                } else {
                    event.newfov = 0.9f;
                }
            }
        }
    }

}
