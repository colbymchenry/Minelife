package com.minelife.gun.client;

import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.client.guns.ItemGunClient;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

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
        Minecraft.getMinecraft().getTextureManager().bindTexture(client.getTexture());
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            if(type == ItemRenderType.ENTITY) GL11.glScalef(0.15f, 0.15f, 0.15f);
            client.renderItem(type, item, data);
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}