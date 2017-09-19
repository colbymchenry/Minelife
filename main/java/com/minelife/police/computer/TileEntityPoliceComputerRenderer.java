package com.minelife.police.computer;

import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class TileEntityPoliceComputerRenderer extends TileEntitySpecialRenderer {

    private ResourceLocation texture;
    private ResourceLocation objModelLocation;
    private IModelCustom model;

    public TileEntityPoliceComputerRenderer() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/police_computer.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/police_computer.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        TileEntityPoliceComputer tileComputer = (TileEntityPoliceComputer) tileEntity;

        bindTexture(texture);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            float xOffset = tileComputer.getFacing() == EnumFacing.NORTH ? 0F :
                    tileComputer.getFacing() == EnumFacing.EAST ? 0F :
                            tileComputer.getFacing() == EnumFacing.WEST ? 1F : 1F;

            float zOffset = tileComputer.getFacing() == EnumFacing.NORTH ? 1F :
                    tileComputer.getFacing() == EnumFacing.EAST ? 0F :
                            tileComputer.getFacing() == EnumFacing.WEST ? 1F : 0F;

            GL11.glTranslated(x + xOffset, y, z + zOffset);

//            GL11.glScalef(0.6f, 0.8f, 0.6f);

            GL11.glRotatef(tileComputer.getRotationDegree(), 0, 1, 0);

            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
