package com.minelife.shop.client;

import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import com.minelife.shop.TileEntityShopBlock;
import com.minelife.util.NumberConversions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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

            if (tileEntityATM.getStackToSale() != null) {
                GL11.glTranslated(1.01, 0.48, 0.38);

                renderText("$" + NumberConversions.formatter.format(tileEntityATM.getPrice()), 0xFFFFFF);

                GL11.glPushMatrix();
                GL11.glTranslated(0.0, 0.27, -0.28);
                renderText("x" + tileEntityATM.getStackToSale().stackSize, 0xFFFFFF);
                GL11.glPopMatrix();

                if(!(tileEntityATM.getStackToSale().getItem() instanceof ItemBlock)) GL11.glRotatef(90, 0, 1, 0);
                GL11.glTranslated(0, 0.05, 0);
                GL11.glScalef(0.6f, 0.6f, 0.6f);
                EntityItem entItem = new EntityItem(Minecraft.getMinecraft().theWorld, 0D, 0D, 0D, tileEntityATM.getStackToSale());
                entItem.getEntityItem().stackSize = 1;
                entItem.hoverStart = 0.0F;
                RenderItem.renderInFrame = true;
                RenderManager.instance.renderEntityWithPosYaw(entItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                RenderItem.renderInFrame = false;
            }
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private void renderText(String s, int color) {
        FontRenderer fontrenderer = this.func_147498_b();
        float f1 = 0.6666667F;
        float f3 = 0.016666668F * f1;
        GL11.glPushMatrix();
        GL11.glRotatef(90f, 0f, 1f, 0f);
        GL11.glTranslatef(0.0F, -0.2f, 0.05F * f1);
        GL11.glScalef(f3, -f3, f3);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
        GL11.glDepthMask(false);
        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 1 * 10 - 1 * 5, color);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}

