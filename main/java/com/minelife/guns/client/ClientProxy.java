package com.minelife.guns.client;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemAmmo;
import com.minelife.guns.item.ItemGun;
import com.minelife.guns.packet.PacketReload;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.render.AdjustPlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClientProxy extends MLProxy {

    private KeyBinding reloadKey = new KeyBinding("key." + Minelife.MOD_ID + ".guns", Keyboard.KEY_R, Minelife.NAME);
    private KeyBinding modifyKey = new KeyBinding("key." + Minelife.MOD_ID + ".guns", Keyboard.KEY_Z, Minelife.NAME);

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);

        ModGuns.itemAmmo.registerModels();

        RenderGun gun = new RenderGun();
        for (EnumGun gunType : EnumGun.values())
            registerItemRenderer(ModGuns.itemGun, gunType.ordinal(), "minelife:gun", gun);

        RenderAttachment attachmentRenderer = new RenderAttachment();
        for (EnumAttachment attachment : EnumAttachment.values())
            registerItemRenderer(ModGuns.itemAttachment, attachment.ordinal(), "minelife:gunAttachment", attachmentRenderer);
    }

    @SubscribeEvent
    public void updateModel(AdjustPlayerModelEvent event) {
        if (event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() != ModGuns.itemGun) return;

        EnumGun gunType = EnumGun.values()[event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getMetadata()];

        event.getModel().bipedRightArm.rotateAngleZ = 0.0F;
        event.getModel().bipedRightArm.rotateAngleY = -(0.1F * 0.6F) + event.getModel().bipedHead.rotateAngleY;
        event.getModel().bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + event.getModel().bipedHead.rotateAngleX;
        event.getModel().bipedRightArm.rotateAngleX -= 0.0F;
        event.getModel().bipedRightArm.rotateAngleZ += MathHelper.cos(event.getAgeInTicks() * 0.09F) * 0.05F + 0.05F;
        event.getModel().bipedRightArm.rotateAngleX += MathHelper.sin(event.getAgeInTicks() * 0.067F) * 0.05F;

        if (gunType == EnumGun.DESERT_EAGLE || gunType == EnumGun.MAGNUM) return;

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

        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

        EnumGun gunType = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

        if (event.getButton() == 0 && event.isButtonstate() && !gunType.isFullAuto) {
            event.setCanceled(true);

            ItemGun.fire(player, player.getLookVec(), 0);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (reloadKey.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().player;

            if (player == null) return;

            if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

            EnumGun gunType = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

            if (ItemGun.getClipCount(player.getHeldItemMainhand()) == gunType.clipSize) return;

            if (ItemGun.isReloading(player.getHeldItemMainhand())) return;

            if (ItemAmmo.getAmmoCount(player, player.getHeldItemMainhand()) <= 0) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Guns] " + TextFormatting.GOLD + "No ammo."));
                return;
            }

            Minelife.getNetwork().sendToServer(new PacketReload());
            player.getEntityWorld().playSound(player, player.getPosition(), new SoundEvent(gunType.soundReload), SoundCategory.NEUTRAL, 1, 1);
        }

        if(modifyKey.isPressed() && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == ModGuns.itemGun) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiModifyGun(Minecraft.getMinecraft().player.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (Minecraft.getMinecraft().player == null) return;

        if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true);

            boolean aimingDownSight = Mouse.isButtonDown(1);
            ItemStack gunStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            EnumGun gunType = EnumGun.values()[gunStack.getMetadata()];

            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();

            if (ItemGun.getReloadTime(gunStack) != 0) {
                long max = gunType.reloadTime;
                long fill = ItemGun.getReloadTime(gunStack) - System.currentTimeMillis();
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

            int centerX = (event.getResolution().getScaledWidth() / 2);
            int centerY = (event.getResolution().getScaledHeight() / 2);

            if (!aimingDownSight) {
                GuiHelper.drawRect(centerX, centerY, 1, 1);
                GuiHelper.drawRect(centerX, centerY - 6, 1, 3);
                GuiHelper.drawRect(centerX, centerY + 4, 1, 3);
                GuiHelper.drawRect(centerX - 6, centerY, 3, 1);
                GuiHelper.drawRect(centerX + 4, centerY, 3, 1);
            } else {
                if (gunType == EnumGun.BARRETT || gunType == EnumGun.AWP)
                    drawSniperScope(centerX, centerY, 100);
            }

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        Bullet.BULLETS.removeIf(bullet -> bullet.tick(event.getPartialTicks()));
    }

    @SubscribeEvent
    public void fovUpdate(FOVUpdateEvent event) {
        if (event.getEntity().getHeldItemMainhand().getItem() == ModGuns.itemGun) {
            if (Mouse.isButtonDown(1)) {
                EnumGun gunType = EnumGun.values()[event.getEntity().getHeldItemMainhand().getMetadata()];
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
                if (gunType == EnumGun.AWP || gunType == EnumGun.BARRETT) {
                    event.setNewfov(0.2f);
                } else {
                    event.setNewfov(0.9f);
                }
//                }
            }
        }
    }

    void drawSniperScope(float x, float y, float radius) {
        int i;
        int lineAmount = 500;
        double twicePi = 2.0f * Math.PI;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0, 0, 0, 1);

        GL11.glLineWidth(10);

        /**
         * Draw the circle
         */
        GL11.glBegin(GL11.GL_LINE_LOOP);
        {

            for (i = 0; i <= lineAmount; i++) {
                float edgeX = x + (radius * (float) Math.cos(i * twicePi / lineAmount));
                float edgeY = y + (radius * (float) Math.sin(i * twicePi / lineAmount));

                GL11.glVertex2f(edgeX, edgeY);

            }
        }
        GL11.glEnd();

        /**
         * Fill corners around the circle
         */
        for (i = 0; i <= lineAmount; i++) {
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                float edgeX = x + (radius * (float) Math.cos(i * twicePi / lineAmount));
                float edgeY = y + (radius * (float) Math.sin(i * twicePi / lineAmount));

                if (edgeX < x)
                    GL11.glVertex2f(x - radius, edgeY);

                if (edgeX > x)
                    GL11.glVertex2f(x + radius, edgeY);

                GL11.glVertex2f(edgeX, edgeY);
            }
            GL11.glEnd();
        }

        /**
         * START: Draw the rectangles around the circle
         */

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        float startX = 0, startY = 0;
        float width = x - radius;
        float height = scaledResolution.getScaledHeight();
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = width;
        startY = 0;
        width = scaledResolution.getScaledWidth();
        height = y - radius;
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = 0;
        startY = y + radius;
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        startX = x + radius;
        startY = 0;
        height = scaledResolution.getScaledHeight();
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(startX, startY + height);
            GL11.glVertex2f(startX + width, startY + height);
            GL11.glVertex2f(startX + width, startY);
            GL11.glVertex2f(startX, startY);
        }
        GL11.glEnd();

        /**
         * END: Drawing rectangles around the circle
         */

        GL11.glLineWidth(4);

        /**
         * Draw crosshairs
         */
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex2f(x, y - radius);
            GL11.glVertex2f(x, y + radius);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex2f(x - radius, y);
            GL11.glVertex2f(x + radius, y);
        }
        GL11.glEnd();

        GL11.glLineWidth(1);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

}