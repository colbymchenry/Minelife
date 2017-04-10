package com.minelife.economy.client;

import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderATMBlock extends TileEntitySpecialRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public RenderATMBlock() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/atm.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/atm.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        TileEntityATM tileEntityATM = (TileEntityATM) tileEntity;

        bindTexture(texture);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            float xOffset = tileEntityATM.getFacing() == EnumFacing.NORTH ? 0.2F :
                    tileEntityATM.getFacing() == EnumFacing.EAST ? 0.3F :
                    tileEntityATM.getFacing() == EnumFacing.WEST ? 0.7F : 0.8F;

            float zOffset = tileEntityATM.getFacing() == EnumFacing.NORTH ? 0.7F :
                    tileEntityATM.getFacing() == EnumFacing.EAST ? 0.2F :
                    tileEntityATM.getFacing() == EnumFacing.WEST ? 0.8F : 0.3F;

            GL11.glTranslated(x + xOffset, y + 0.05, z + zOffset);

            GL11.glScalef(0.6f, 0.8f, 0.6f);

            GL11.glRotatef(tileEntityATM.getRotationDegree(), 0, 1, 0);

            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
