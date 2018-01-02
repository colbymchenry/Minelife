package com.minelife.gun.client.gui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.gun.item.attachments.ItemSite;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Set;

public class GuiSiteDropDown extends GuiDropDown {

    private Map<Integer, String> optionsMap = Maps.newHashMap();

    public GuiSiteDropDown(int xPosition, int yPosition, int width, int height, String... options) {
        super(xPosition, yPosition, width, height, options);
        setupSlots();
    }

    public void update() {
        setupSlots();
    }

    private void setupSlots() {
        optionsMap.clear();
        selected = 0;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        optionsMap.put(-1, ItemGun.getSight(player.getHeldItem()) != null ? ItemGun.getSight(player.getHeldItem()).getDisplayName() : "");

        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.getStackInSlot(i) != null &&
                    player.inventory.getStackInSlot(i).getItem() instanceof ItemSite)
                optionsMap.put(i, player.inventory.getStackInSlot(i).getDisplayName());
        }

        optionsMap.put(-2, "Remove");

        options = new String[optionsMap.size()];
    }

    public int getSlot() {
        return (int) optionsMap.keySet().toArray()[selected];
    }

    @Override
    public void draw(Minecraft mc, int mouse_x, int mouse_y) {
        boolean hover = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= yPosition && mouse_y <= yPosition + height;
        GL11.glColor4f(1, 1, 1, 1);
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, color_bg.hashCode());
        GL11.glColor4f(hover ? color_hover.getRed() / 255f : color.getRed() / 255f, hover ? color_hover.getGreen() / 255f : color.getGreen() / 255f, hover ? color_hover.getBlue() / 255f : color.getBlue() / 255f, 1);
        mc.getTextureManager().bindTexture(arrow_texture);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(xPosition + width - 10, yPosition + ((height - 8) / 2), 0);
            GL11.glTranslatef(4, 4, 4);
            GL11.glRotatef(drop_down_active ? 180 : 0, 0, 0, 1);
            GL11.glTranslatef(-4, -4, -4);
            GuiUtil.drawImage(0, 0, 8, 8);
        }
        GL11.glPopMatrix();

        // TODO: Draw colors for options.

        mc.fontRenderer.drawString((String) optionsMap.values().toArray()[selected], xPosition + 2, yPosition + ((height - mc.fontRenderer.FONT_HEIGHT) / 2) + 1, hover ? color_hover.hashCode() : color.hashCode());

        if (drop_down_active) {
            GL11.glColor4f(1, 1, 1, 1);
            drawRect(xPosition, yPosition + height, xPosition + width, yPosition + height + ((options.length - 1) * (mc.fontRenderer.FONT_HEIGHT + 5) + 1), color_bg.hashCode());
            int y = yPosition + 3;
            for (int i = 0; i < optionsMap.size(); i++) {
                if (i != selected) {
                    y += mc.fontRenderer.FONT_HEIGHT + 5;
                    boolean hovering = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                    if (hovering)
                        drawRect(xPosition, y - 3, xPosition + width, y + mc.fontRenderer.FONT_HEIGHT + 2, color_highlight.hashCode());
                    mc.fontRenderer.drawString((String) optionsMap.values().toArray()[i], xPosition + 2, y, hovering ? color_hover.hashCode() : color.hashCode());
                }
            }
        }
    }

}
