package com.minelife.shop.client;

import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import com.minelife.shop.TileEntityShopBlock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RenderBlockShopBlock extends TileEntitySpecialRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public RenderBlockShopBlock() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/shop_block.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/shop_block.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        TileEntityShopBlock tileEntityATM = (TileEntityShopBlock) tileEntity;

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

