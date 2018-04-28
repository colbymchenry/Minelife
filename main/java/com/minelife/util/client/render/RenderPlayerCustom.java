package com.minelife.util.client.render;

import com.minelife.gangs.client.ClientProxy;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderPlayerCustom extends RenderPlayer {

    public RenderPlayerCustom(RenderManager renderManager) {
        super(renderManager);
        this.addLayer(new CustomLayerCape(this));
        this.mainModel = new ModelPlayerCustom(0.0F, false);
    }

    @Override
    public void renderName(AbstractClientPlayer entity, double x, double y, double z) {
        if(ClientProxy.gangMembers.contains(entity.getUniqueID())) {
            super.renderName(entity, x, y, z);
        }
    }
}