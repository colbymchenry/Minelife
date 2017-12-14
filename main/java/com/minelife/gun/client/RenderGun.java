package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.RenderPlayerCustom;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.Iterator;
import java.util.Map;

public class RenderGun implements IItemRenderer {

    private ItemGunClient client;

    public RenderGun(ItemGun gun) {
        this.client = gun.getClientHandler();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return client.handleRenderType(item, type);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return client.shouldUseRenderHelper(type, item, helper);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        if (type == ItemRenderType.ENTITY) GL11.glScalef(0.15f, 0.15f, 0.15f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(client.getTexture());
        client.renderItem(type, item, data);
        GL11.glPopMatrix();


        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            client.renderFirstPerson(Minecraft.getMinecraft(), Minecraft.getMinecraft().thePlayer);
            GL11.glPopMatrix();
        }

        handleRecoil(client);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public static void handleRecoil(ItemGunClient client) {
        if (client.shot) {
            float reboundSpeed = client.getReboundSpeed();
            float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
            float yawDiff = Math.abs(client.ogYaw - yaw);
            float yawInc = client.ogYaw > yaw ? (yawDiff / reboundSpeed) : client.ogYaw < yaw ? -(yawDiff / reboundSpeed) : 0.0F;
            if (yawDiff < 0.2) yawInc = 0.0F;
            float pitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
            float pitchDiff = Math.abs(client.ogPitch - pitch);
            float pitchInc = client.ogPitch > pitch ? -(pitchDiff / reboundSpeed) : client.ogPitch < pitch ? (pitchDiff / reboundSpeed) : 0.0F;
            if (pitchDiff < 0.2) pitchInc = 0.0F;
            if (yawInc == 0 && pitchInc == 0) client.shot = false;
            Minecraft.getMinecraft().thePlayer.setAngles(yawInc, pitchInc);
        }

        if (Math.abs(Mouse.getEventDX()) > 1 || Math.abs(Mouse.getEventDY()) > 1) {
            client.shot = false;
            client.ogYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
            client.ogPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
        }
    }

}