package com.minelife.gun.client.gui;

import com.google.common.collect.Sets;
import com.minelife.gun.item.attachments.ItemSite;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class GuiSiteDropDown extends GuiDropDown {

    private Set<Integer> slots = Sets.newTreeSet();

    private Minecraft mc;

    public GuiSiteDropDown(int xPosition, int yPosition, int width, int height, Minecraft mc, String... options) {
        super(xPosition, yPosition, width, height, options);
        this.mc = mc;
        if (ItemGun.getSite(mc.thePlayer.getHeldItem()) != null) slots.add(-1);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.getStackInSlot(i) != null &&
                    player.inventory.getStackInSlot(i).getItem() instanceof ItemSite)
                slots.add(i);
        }

    }

    public void update() {
        slots.clear();
        selected = 0;
        if (ItemGun.getSite(mc.thePlayer.getHeldItem()) != null) slots.add(-1);
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.getStackInSlot(i) != null &&
                    player.inventory.getStackInSlot(i).getItem() instanceof ItemSite)
                slots.add(i);
        }
    }


    public int getSlot() {
        return (int) slots.toArray()[selected];
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

        if (slots.isEmpty()) return;

        if (selected > slots.size()) return;

        ItemStack selectedStack = ItemGun.getSite(mc.thePlayer.getHeldItem()) != null ? ItemGun.getSite(mc.thePlayer.getHeldItem()) : mc.thePlayer.inventory.getStackInSlot((Integer) slots.toArray()[selected]);

        if (selectedStack == null) return;

        int[] colors = ItemSite.getSiteColor(selectedStack);

        mc.fontRenderer.drawString(selectedStack.getDisplayName().replaceAll("item.", "").replaceAll(".name", "").replaceAll("_", " ").replaceAll("site", ""), xPosition + 2, yPosition + ((height - mc.fontRenderer.FONT_HEIGHT) / 2) + 1, (colors[0] << 16 | colors[1] << 8 | colors[2]));

        if (drop_down_active) {
            GL11.glColor4f(1, 1, 1, 1);
            drawRect(xPosition, yPosition + height, xPosition + width, yPosition + height + ((slots.size() - 1) * (mc.fontRenderer.FONT_HEIGHT + 5) + 1), color_bg.hashCode());
            int y = yPosition + 3;
            for (int i = 0; i < slots.size(); i++) {
                if (i != selected) {
                    y += mc.fontRenderer.FONT_HEIGHT + 5;
                    boolean hovering = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                    if (hovering)
                        drawRect(xPosition, y - 3, xPosition + width, y + mc.fontRenderer.FONT_HEIGHT + 2, color_highlight.hashCode());

                    if ((Integer) slots.toArray()[i] > -1) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot((Integer) slots.toArray()[i]);
                        colors = ItemSite.getSiteColor(stack);
                        mc.fontRenderer.drawString(stack.getDisplayName().replaceAll("item.", "").replaceAll(".name", "").replaceAll("_", " ").replaceAll("site", ""), xPosition + 2, y, (colors[0] << 16 | colors[1] << 8 | colors[2]));
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(Minecraft mc, int mouse_x, int mouse_y) {
        if (slots.isEmpty()) return true;

        if (mouse_x >= xPosition && mouse_x <= xPosition + width) {
            if (mouse_y >= yPosition && mouse_y <= yPosition + height) {
                drop_down_active = !drop_down_active;
                return true;
            } else if (mouse_y >= yPosition && drop_down_active) {
                int y = yPosition + 3;
                for (int i = 0; i < slots.size(); i++) {
                    if (i != selected) {
                        y += mc.fontRenderer.FONT_HEIGHT + 5;
                        boolean hovering = mouse_x >= xPosition && mouse_x <= xPosition + width && mouse_y >= y && mouse_y <= y + mc.fontRenderer.FONT_HEIGHT;
                        if (hovering) {
                            selected = i;
                            drop_down_active = !drop_down_active;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
