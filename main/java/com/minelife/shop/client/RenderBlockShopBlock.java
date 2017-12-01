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

            GL11.glTranslated(x, y, z);

            if (tileEntityATM != null && tileEntityATM.getFacing() != null) {
                GL11.glTranslated(0.5, 0.5, 0.5);
                switch (tileEntityATM.getFacing()) {
                    case NORTH:
                        GL11.glTranslated(0.11, 0, 0);
                        GL11.glRotatef(90, 0, 1, 0);
                        break;
                    case SOUTH:
                        GL11.glTranslated(-0.11, 0, 0);
                        GL11.glRotatef(270, 0, 1, 0);
                        break;
                    case WEST:
                        GL11.glTranslated(0, 0, -0.11);
                        GL11.glRotatef(180, 0, 1, 0);
                        break;
                    case EAST:
                        GL11.glTranslated(0, 0, 0.11);
                        break;
                    default:
                        GL11.glTranslated(0, 0, 0.11);
                        break;
                }
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            } else {
                GL11.glTranslatef(0, 0, 0.11f);
            }

            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}

