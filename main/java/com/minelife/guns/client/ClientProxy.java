package com.minelife.guns.client;

import com.minelife.MLProxy;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGunType;
import com.minelife.util.client.render.AdjustPlayerModelEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.EnumMap;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);

        RenderGun gunRenderer = new RenderGun();
        for (EnumGunType gunType : EnumGunType.values())
            registerItemRenderer(ModGuns.itemGun, gunType.ordinal(), "minelife:gun", gunRenderer);
    }

    @SubscribeEvent
    public void updateModel(AdjustPlayerModelEvent event) {
        if(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() != ModGuns.itemGun) return;

        EnumGunType gunType = EnumGunType.values()[event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getMetadata()];

        event.getModel().bipedRightArm.rotateAngleZ = 0.0F;
        event.getModel().bipedRightArm.rotateAngleY = -(0.1F * 0.6F) + event.getModel().bipedHead.rotateAngleY;
        event.getModel().bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + event.getModel().bipedHead.rotateAngleX;
        event.getModel().bipedRightArm.rotateAngleX -= 0.0F;
        event.getModel().bipedRightArm.rotateAngleZ += MathHelper.cos(event.getAgeInTicks() * 0.09F) * 0.05F + 0.05F;
        event.getModel().bipedRightArm.rotateAngleX += MathHelper.sin(event.getAgeInTicks() * 0.067F) * 0.05F;

        if(gunType == EnumGunType.DESERT_EAGLE || gunType == EnumGunType.MAGNUM) return;

        event.getModel().bipedLeftArm.rotateAngleZ = 0.0F;
        event.getModel().bipedLeftArm.rotateAngleY = 0.1F * 0.6F + event.getModel().bipedHead.rotateAngleY + 0.4F;
        event.getModel().bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + event.getModel().bipedHead.rotateAngleX;
        event.getModel().bipedLeftArm.rotateAngleX -= 0;
        event.getModel().bipedLeftArm.rotateAngleZ -= MathHelper.cos(event.getAgeInTicks() * 0.09F) * 0.05F + 0.05F;
        event.getModel().bipedLeftArm.rotateAngleX -= MathHelper.sin(event.getAgeInTicks() * 0.067F) * 0.05F;
    }

//    @SubscribeEvent
//    public void onClick(MouseEvent event) {
//        if (Minecraft.getMinecraft().thePlayer == null) return;
//
//        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
//
//        if (heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return;
//
//        if (ItemGunClient.modifying) return;
//
//        if (event.button == 1) {
//            ItemGunClient.aimingDownSight = !ItemGunClient.aimingDownSight;
//            event.setCanceled(true);
//            return;
//        }
//
//        if (event.dwheel != 0) {
//            ItemGunClient.aimingDownSight = false;
//            return;
//        }
//
//        event.setCanceled(true);
//
//        if (event.button == 0 && event.buttonstate && !((ItemGun) heldItem.getItem()).isFullAuto()) {
//            Minelife.NETWORK.sendToServer(new PacketMouseClick());
//        }
//    }
//
//    @SubscribeEvent
//    public void onTick(TickEvent.ClientTickEvent event) {
//        if (Minecraft.getMinecraft().thePlayer == null) return;
//
//        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
//
//        if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return;
//
//        if (ItemGunClient.modifying) return;
//
//        if (Minecraft.getMinecraft().currentScreen != null) {
//            ItemGunClient.aimingDownSight = false;
//            return;
//        }
//
//        if (Mouse.isButtonDown(0) && ((ItemGun) heldItem.getItem()).isFullAuto()) {
//            Minelife.NETWORK.sendToServer(new PacketMouseClick());
//        }
//
//    }
//
//    @SubscribeEvent
//    public void fovUpdate(FOVUpdateEvent event) {
//        if (event.entity.getHeldItem() != null && event.entity.getHeldItem().getItem() instanceof ItemGun) {
//            ItemGun gun = (ItemGun) event.entity.getHeldItem().getItem();
//            if (ItemGunClient.aimingDownSight) {
//                if (ItemGun.getSight(event.entity.getHeldItem()) != null) {
//                    ItemSight site = (ItemSight) ItemGun.getSight(event.entity.getHeldItem()).getItem();
//                    if (site == MLItems.holographicSight) {
//                        event.newfov = 0.7F;
//                    } else if (site == MLItems.twoXSight) {
//                        event.newfov = 0.5F;
//                    } else if (site == MLItems.acogSight) {
//                        event.newfov = 0.33F;
//                    }
//
//
//                } else {
//                    if(gun == MLItems.awp || gun == MLItems.barrett) {
//                        event.newfov = 0.2F;
//                    } else {
//                        event.newfov = 0.9f;
//                    }
//                }
//            }
//        }
//    }

}
