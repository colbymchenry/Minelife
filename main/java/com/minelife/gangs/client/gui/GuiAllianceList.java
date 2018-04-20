package com.minelife.gangs.client.gui;

import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.network.PacketRemoveAlliance;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class GuiAllianceList extends GuiScrollableContent {

    private static final ResourceLocation textureX = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x.png");

    private boolean canManageAlliances;
    private List<Gang> alliances;

    public GuiAllianceList(Minecraft mc, int x, int y, int width, int height, Gang gang, List<Gang> alliances) {
        super(mc, x, y, width, height);
        this.alliances = alliances;
        this.canManageAlliances = gang.hasPermission(mc.player.getUniqueID(), GangPermission.MANAGE_ALLIANCES);
    }

    @Override
    public int getObjectHeight(int index) {
        return 16;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        GlStateManager.color(175f / 255f, 175f / 255f, 175f / 255f, 1f);
        GlStateManager.disableTexture2D();
        GuiHelper.drawRect(0, 0, width, getObjectHeight(index));
        GlStateManager.color(0, 0, 0, 1);
        GuiHelper.drawRect(1, 1, width - 7, getObjectHeight(index) - 2);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        mc.fontRenderer.drawString(alliances.get(index).getName(), 4, 4, 0xFFFFFF);

        if(canManageAlliances && isHovering) {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(textureX);
            GuiHelper.drawImage(width - 32, 3, 8, 8, textureX);
        }
    }

    @Override
    public int getSize() {
        return alliances.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
        if(mouseX >= width - 32 && mouseX <= width - 32 + 8 && mouseY >= 3 && mouseY <= 3 + 8 && canManageAlliances) {
            Minelife.getNetwork().sendToServer(new PacketRemoveAlliance(alliances.get(index).getName()));
            alliances.remove(index);
        }
    }
}
