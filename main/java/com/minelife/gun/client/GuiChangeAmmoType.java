package com.minelife.gun.client;

import com.google.common.collect.Maps;
import com.minelife.KeyBindings;
import com.minelife.Minelife;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketSetAmmoType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Rectangle;

import java.util.Map;

public class GuiChangeAmmoType extends GuiScreen {

    private int x, y, boxWidth, boxHeight;
    private ItemGun gun;
    private RenderItem itemRender;
    private Map<ItemAmmo, Rectangle> ammoBounds;
    private ItemAmmo selectedAmmo;

    public GuiChangeAmmoType(ItemGun gun) {
        this.gun = gun;
        this.itemRender = new RenderItem();
        this.ammoBounds = Maps.newHashMap();
        selectedAmmo = gun.getAmmo().get(0);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        super.drawScreen(mouseX, mouseY, p_73863_3_);

        renderAmmoTypes();

        if (selectedAmmo == null) return;

        for(ItemAmmo ammo : ammoBounds.keySet()) {
            if(ammoBounds.get(ammo).contains(mouseX, mouseY)) {
                selectedAmmo = ammo;
                break;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        for(ItemAmmo ammo : ammoBounds.keySet()) {
            if(ammoBounds.get(ammo).contains(mouseX, mouseY)) {
                selectedAmmo = ammo;
                break;
            }
        }

        Minelife.NETWORK.sendToServer(new PacketSetAmmoType(selectedAmmo.getAmmoType()));
    }

    @Override
    public void initGui() {
        super.initGui();
        x = (this.width / 2);
        y = (this.height / 2);
        boxWidth = gun.getAmmo().size() * 100;
        boxHeight = 100;
    }

    @Override
    public void updateScreen() {
        if (!Keyboard.isKeyDown(KeyBindings.keyChangeAmmo.getKeyCode()))
            Minecraft.getMinecraft().thePlayer.closeScreen();
    }

    private void renderAmmoTypes() {
        int startX = x - (boxWidth / 2), startY = y - (boxHeight / 2);
        int gridWidth = boxWidth / gun.getAmmo().size();

        // draw BG
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        drawTexturedModalRect(startX, startY, 0, 0, boxWidth, boxHeight);

        GL11.glColor4f(205f/255f, 65f/255f, 42f/255f, 255f/255f);
        Rectangle bounds = ammoBounds.get(selectedAmmo);
        if (bounds != null) {
            drawTexturedModalRect(bounds.getX(), bounds.getY(), 0, 0, bounds.getWidth(), bounds.getHeight());
        }

        if(bounds != null) {
            String ammoName = selectedAmmo.getAmmoType().name().substring(0, 1) + selectedAmmo.getAmmoType().name().substring(1).toLowerCase();
            int stringWidth = fontRendererObj.getStringWidth(ammoName);
            GL11.glColor4f(0, 0, 0, 1);
            int stringX = bounds.getX() + (gridWidth / 2) - (stringWidth / 2);
            int stringY = bounds.getY() - 10;
            drawTexturedModalRect(stringX - 2, stringY - 2, 0, 0, stringWidth + 4, 12);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontRendererObj.drawString(ammoName, stringX, stringY, 0xFFFFFF);
        }


        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(1, 1, 1, 1);

        int offset = 8;
        float scale = 4f;

        int i = 0;
        for (ItemAmmo ammo : gun.getAmmo()) {
            GL11.glPushMatrix();
            {
                GL11.glTranslatef(startX + (gridWidth * i) + 40, startY + 40, 0f);
                ammoBounds.put(ammo, new Rectangle(startX + (gridWidth * i) + 40 - (gridWidth / 2) + 10, startY + 40 - (boxHeight / 2) + 10, gridWidth, boxHeight));
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glTranslatef(offset, offset, offset);
                GL11.glScalef(scale, scale, 1);
                GL11.glTranslatef(-offset, -offset, -offset);
                drawItemStack(new ItemStack(ammo), 0, 0, "");
            }
            GL11.glPopMatrix();

            i++;
        }
    }

    private void drawItemStack(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = fontRendererObj;
        itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_ - (0), p_146982_4_);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }
}
