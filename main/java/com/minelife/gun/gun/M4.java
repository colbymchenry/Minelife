package com.minelife.gun.gun;

import com.minelife.Minelife;
import com.minelife.gun.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class M4 extends Gun {

    public M4(FMLPreInitializationEvent event) {
        super("m4", 65, event);
    }


    @Override
    public Ammo[] getPossibleAmmo() {
        return new Ammo[0];
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    private Animation animation = new Animation(0, 0, 0);

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        {
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(310f, 0, 1, 0);

                animation.animate();
                GL11.glTranslatef(0.1f + animation.posX(), 0f + animation.posY(), 1f + animation.posZ());


            }

            if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                GL11.glTranslatef(0.5f, -1.25f, 0f);
            }

            if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                GL11.glScalef(1.5f, 1.5f, 1.5f);
                GL11.glTranslatef(0.5f, 0f, 0.1f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();
    }

    @Override
    public void fire() {

        Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + "gun." + ((Gun) Minecraft.getMinecraft().thePlayer.getHeldItem().getItem()).name + ".shot", 0.5F, 1.0F);

        animation = new Animation(0, 0, 0).translateTo((float) (Math.random() / 7f), (float) (Math.random() / 7f), 2, 0.2f).translateTo(0, 0, 0, 0.2f);
    }
}
