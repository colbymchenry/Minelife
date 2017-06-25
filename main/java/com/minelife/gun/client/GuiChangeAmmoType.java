package com.minelife.gun.client;

import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiChangeAmmoType extends GuiScreen {

    private int x, y;
    private ItemGun gun;
    private RenderItem itemRender;

    public GuiChangeAmmoType(ItemGun gun) {
        this.gun = gun;
        this.itemRender = new RenderItem();
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

        renderAmmoTypes();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        this.drawDefaultBackground();
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
    // TODO: Switch between ammo types
    }

    @Override
    public void initGui() {
        super.initGui();
        x = (this.width / 2);
        y = (this.height / 2);
    }

    @Override
    public void updateScreen() {
        if(!Keyboard.isKeyDown(Keyboard.KEY_Q))
            Minecraft.getMinecraft().thePlayer.closeScreen();
    }

    private void renderAmmoTypes() {
        float width = gun.getAmmo().size() * 100f;
        float height = 100f;
        float startX = x - (width / 2), startY = y - (height / 2);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        drawTexturedModalRect((int) startX, (int) startY, 0, 0, (int) width, (int) height);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);

        int offset = 8;

        float gridWidth = width / gun.getAmmo().size();

        // TODO: Fix lighting glitch
        int i = 0;
        for (ItemAmmo ammo : gun.getAmmo()) {
            GL11.glPushMatrix();
            {
                GL11.glTranslatef(startX + (gridWidth * i) + 40, startY + 40, 400f);
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glTranslatef(offset, offset, offset);
                GL11.glScalef(4, 4, 4);
                GL11.glTranslatef(-offset, -offset, -offset);
                drawItemStack(new ItemStack(ammo), 0, 0, "");
            }
            GL11.glPopMatrix();

            i++;
        }
    }

    private void drawItemStack(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        itemRender.zLevel = 40.0F;
        FontRenderer font = null;
        if (p_146982_1_ != null) font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
        if (font == null) font = Minecraft.getMinecraft().fontRenderer;
        itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), p_146982_1_, p_146982_2_, p_146982_3_ - (0), p_146982_4_);
        itemRender.zLevel = 0.0F;
    }
}
