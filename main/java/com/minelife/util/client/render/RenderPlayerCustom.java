package com.minelife.util.client.render;

import com.minelife.gangs.client.ClientProxy;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;

@SideOnly(Side.CLIENT)
public class RenderPlayerCustom extends RenderPlayer {

    public RenderPlayerCustom(RenderManager renderManager) {
        super(renderManager);
        this.addLayer(new CustomLayerCape(this));
        this.mainModel = new ModelPlayerCustom(0.0F, false);
    }

    @Override
    public void renderName(AbstractClientPlayer entity, double x, double y, double z) {
        if (ClientProxy.gangMembers.containsKey(entity.getUniqueID())) {
            if (this.canRenderName(entity)) {
                double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
                float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

                if (d0 < (double) (f * f)) {
                    String s = ClientProxy.gangMembers.get(entity.getUniqueID()).color + "[" + WordUtils.capitalizeFully(ClientProxy.gangMembers.get(entity.getUniqueID()).name().replace("_", " ")) + "] " + TextFormatting.GOLD + entity.getName();
                    GlStateManager.alphaFunc(516, 0.1F);
                    this.renderEntityName(entity, x, y, z, s, d0);
                }
            }
//            super.renderName(entity, x, y, z);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        SkinChanger.setPlayerTexture(entity);
        return entity.getLocationSkin();
    }


}