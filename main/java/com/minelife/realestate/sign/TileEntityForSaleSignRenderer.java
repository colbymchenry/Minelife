package com.minelife.realestate.sign;

import com.minelife.Minelife;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityForSaleSignRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation textureForRent = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_ForRent.png");
    private static final ResourceLocation textureForSale = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_ForSale.png");
    private static final ResourceLocation textureSold = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_Sold.png");
    private final ModelSign modelSign = new ModelSign();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double xCoord, double yCoord, double zCoord, float partialTickTime)
    {
        GL11.glPushMatrix();
        {
            float f1 = 0.6666667F;
            float f3;

            if (tileEntity.getBlockType() == BlockForSaleSign.getBlock(true)) {
                GL11.glTranslatef((float) xCoord + 0.5F, (float) yCoord + 0.75F * f1, (float) zCoord + 0.5F);
                float f2 = (float) (tileEntity.getBlockMetadata() * 360) / 16.0F;
                GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
                this.modelSign.signStick.showModel = true;
            } else {
                int j = tileEntity.getBlockMetadata();
                f3 = 0.0F;

                if (j == 2) {
                    f3 = 180.0F;
                }

                if (j == 4) {
                    f3 = 90.0F;
                }

                if (j == 5) {
                    f3 = -90.0F;
                }

                GL11.glTranslatef((float) xCoord + 0.5F, (float) yCoord + 0.75F * f1, (float) zCoord + 0.5F);
                GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
                this.modelSign.signStick.showModel = false;
            }

            TileEntityForSaleSign tileEntityForSaleSign = (TileEntityForSaleSign) tileEntity;

            this.bindTexture(tileEntityForSaleSign.isOccupied() ? textureSold : tileEntityForSaleSign.isRentable() ? textureForRent : textureForSale);
            GL11.glScalef(f1, -f1, -f1);
            this.modelSign.renderSign();
        }
        GL11.glPopMatrix();
    }

}
