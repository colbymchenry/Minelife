package com.minelife.locks.client;

import com.minelife.Minelife;
import com.minelife.locks.LockType;
import com.minelife.locks.TileEntityLock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class TileEntityLockRenderer extends TileEntitySpecialRenderer {

    ResourceLocation texIron, texGold, texDiamond, texObsidian;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public TileEntityLockRenderer() {
        texIron = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/iron_lock.png");
        texGold = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/gold_lock.png");
        texDiamond = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/diamond_lock.png");
        texObsidian = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/obsidian_lock.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/lock.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        TileEntityLock tileLock = (TileEntityLock) tileEntity;

        if(tileLock.lockType != null) {
            switch (tileLock.lockType) {
                case IRON: {
                    bindTexture(texIron);
                    break;
                }
                case GOLD: {
                    bindTexture(texGold);
                    break;
                }
                case DIAMOND: {
                    bindTexture(texDiamond);
                    break;
                }
                case OBSIDIAN: {
                    bindTexture(texObsidian);
                    break;
                }
            }
        }

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z);
            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
