package com.minelife.gun.client;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.gun.bullets.BulletRenderer;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketMouseClick;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
    }

    /**
     * This method is put in place to prevent the player from interacting with blocks while holding a guns
     */
    @SubscribeEvent
    public void onClick(MouseEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return;

        if (event.button == 1) {
            ItemGunClient.aimingDownSight = !ItemGunClient.aimingDownSight;
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

        if(ItemGunClient.aimingDownSight && ItemGunClient.hasHolographic(heldItem)) {
            Minecraft.getMinecraft().thePlayer.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 15 * 20, 0, false));
        }

        if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return;

        if (Minecraft.getMinecraft().currentScreen != null) {
            ItemGunClient.aimingDownSight = false;
            return;
        }

        if (Mouse.isButtonDown(0) && ((ItemGun) heldItem.getItem()).isFullAuto()) {
            Minelife.NETWORK.sendToServer(new PacketMouseClick());
        }

    }


}
