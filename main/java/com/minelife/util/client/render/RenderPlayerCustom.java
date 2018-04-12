package com.minelife.util.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPlayerCustom extends RenderPlayer {

    public RenderPlayerCustom(RenderManager renderManager) {
        super(renderManager);
        this.addLayer(new CustomLayerCape(this));
        this.mainModel = new ModelPlayerCustom(0.0F, false);
    }

}