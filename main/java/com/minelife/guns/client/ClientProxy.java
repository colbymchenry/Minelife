package com.minelife.guns.client;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGunType;
import com.minelife.guns.item.ItemAmmo;
import com.minelife.guns.item.ItemGun;
import com.minelife.guns.packet.PacketFire;
import com.minelife.guns.packet.PacketReload;
import com.minelife.minebay.client.gui.GuiItemListings;
import com.minelife.util.client.Animation;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.render.AdjustPlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ClientProxy extends MLProxy {

    private KeyBinding reloadKey = new KeyBinding("key." + Minelife.MOD_ID + ".minebay", Keyboard.KEY_R, Minelife.NAME);

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);

        ModGuns.itemAmmo.registerModels();

        RenderGun gunRenderer = new RenderGun();
        for (EnumGunType gunType : EnumGunType.values())
            registerItemRenderer(ModGuns.itemGun, gunType.ordinal(), "minelife:gun", gunRenderer);
    }

    @SubscribeEvent
    public void updateModel(AdjustPlayerModelEvent event) {
        if (event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() != ModGuns.itemGun) return;

        EnumGunType gunType = EnumGunType.values()[event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getMetadata()];

        event.getModel().bipedRightArm.rotateAngleZ = 0.0F;
        event.getModel().bipedRightArm.rotateAngleY = -(0.1F * 0.6F) + event.getModel().bipedHead.rotateAngleY;
        event.getModel().bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + event.getModel().bipedHead.rotateAngleX;
        event.getModel().bipedRightArm.rotateAngleX -= 0.0F;
        event.getModel().bipedRightArm.rotateAngleZ += MathHelper.cos(event.getAgeInTicks() * 0.09F) * 0.05F + 0.05F;
        event.getModel().bipedRightArm.rotateAngleX += MathHelper.sin(event.getAgeInTicks() * 0.067F) * 0.05F;

        if (gunType == EnumGunType.DESERT_EAGLE || gunType == EnumGunType.MAGNUM) return;

        event.getModel().bipedLeftArm.rotateAngleZ = 0.0F;
        event.getModel().bipedLeftArm.rotateAngleY = 0.1F * 0.6F + event.getModel().bipedHead.rotateAngleY + 0.4F;
        event.getModel().bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + event.getModel().bipedHead.rotateAngleX;
        event.getModel().bipedLeftArm.rotateAngleX -= 0;
        event.getModel().bipedLeftArm.rotateAngleZ -= MathHelper.cos(event.getAgeInTicks() * 0.09F) * 0.05F + 0.05F;
        event.getModel().bipedLeftArm.rotateAngleX -= MathHelper.sin(event.getAgeInTicks() * 0.067F) * 0.05F;
    }

    @SubscribeEvent
    public void onClick(MouseEvent event) {
        if (Minecraft.getMinecraft().player == null) return;

        EntityPlayer player = Minecraft.getMinecraft().player;

        if(player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

        EnumGunType gunType = EnumGunType.values()[player.getHeldItemMainhand().getMetadata()];

        if (event.getButton() == 0 && event.isButtonstate() && !gunType.isFullAuto) {
            event.setCanceled(true);

            if(ItemGun.getClipCount(player.getHeldItemMainhand()) <= 0) {
                player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(new ResourceLocation(Minelife.MOD_ID, "guns.empty")), SoundCategory.NEUTRAL, 1, 1);
                return;
            }

            if(ItemGun.isReloading(player.getHeldItemMainhand())) return;

            Minelife.getNetwork().sendToServer(new PacketFire(player.getLookVec()));

            Bullet bullet = new Bullet(player.getEntityWorld(), player.posX, player.posY + player.getEyeHeight(), player.posZ, 0,
                    player.getLookVec(), gunType.bulletSpeed, gunType.damage, player);

            Bullet.BULLETS.add(bullet);

            ItemGun.decreaseAmmo(player.getHeldItemMainhand());

            gunType.resetAnimation();
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gunType.soundShot), SoundCategory.NEUTRAL, 1, 1);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(reloadKey.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if(player == null) return;

            if(player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

            EnumGunType gunType = EnumGunType.values()[player.getHeldItemMainhand().getMetadata()];

            if(ItemGun.getClipCount(player.getHeldItemMainhand()) == gunType.clipSize) return;

            System.out.println(ItemGun.getClipCount(player.getHeldItemMainhand()) + "," + gunType.clipSize);

            if(ItemGun.isReloading(player.getHeldItemMainhand())) return;

            if(ItemAmmo.getAmmoCount(player, player.getHeldItemMainhand()) <= 0) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Guns] " + TextFormatting.GOLD + "No ammo."));
                return;
            }

            Minelife.getNetwork().sendToServer(new PacketReload());
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gunType.soundReload), SoundCategory.NEUTRAL, 1, 1);
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (Minecraft.getMinecraft().player == null) return;

        if(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

        if(event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);

            ItemStack gunStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            EnumGunType gunType = EnumGunType.values()[gunStack.getMetadata()];

            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();

            if(gunStack.hasTagCompound() && gunStack.getTagCompound().hasKey("reloadTime")) {
                long max = gunType.reloadTime;
                long fill = gunStack.getTagCompound().getLong("reloadTime") - System.currentTimeMillis();
                if (fill > -1) {
                    // 20D is the width of the progress bar
                    double toFill = ((double) fill / (double) max) * (30D);

                    GlStateManager.color(1, 1, 1, 0.35f);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((event.getResolution().getScaledWidth() / 2) + 10, (event.getResolution().getScaledHeight() / 2) + 10, 0);
                    GlStateManager.scale(toFill, 1, 1);
                    GuiHelper.drawRect(0, 0, 1, 10);
                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.color(1, 1, 1, 1);

            if(!Mouse.isButtonDown(1)) {
                int centerX = (event.getResolution().getScaledWidth() / 2);
                int centerY = (event.getResolution().getScaledHeight() / 2);

                GuiHelper.drawRect(centerX, centerY, 1, 1);
                GuiHelper.drawRect(centerX, centerY - 6, 1, 3);
                GuiHelper.drawRect(centerX, centerY + 4, 1, 3);
                GuiHelper.drawRect(centerX - 6, centerY, 3, 1);
                GuiHelper.drawRect(centerX + 4, centerY, 3, 1);
            }

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        Bullet.BULLETS.removeIf(bullet -> bullet.tick(event.getPartialTicks()));
    }

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
