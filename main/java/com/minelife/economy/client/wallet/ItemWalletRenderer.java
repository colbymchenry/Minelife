package com.minelife.economy.client.wallet;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.economy.ItemWallet;
import com.minelife.util.DyeColor;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;


public class ItemWalletRenderer implements IItemRenderer {

    Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation TexWallet = new ResourceLocation(Minelife.MOD_ID, "textures/items/wallet_empty.png");
    private static final ResourceLocation TexWalletBills = new ResourceLocation(Minelife.MOD_ID, "textures/items/wallet_bills.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Color color = new Color(ItemDye.field_150922_c[item.getItemDamage()]);
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        if (type == ItemRenderType.INVENTORY) {
            mc.getTextureManager().bindTexture(TexWallet);
            GuiUtil.drawImage(0, 0, 16, 16);

            if(ItemWallet.getHoldings(item) > 0) {
                GL11.glColor4f(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(TexWalletBills);
                GuiUtil.drawImage(0, 0, 16, 16);
            }
        }else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            {
                GL11.glRotatef(70f, 0f, 1f, 0f);
                GL11.glTranslatef(-0.7f, 0.8f, 0.2f);
                renderWallet3d(item, MLItems.wallet.icon_empty);

                if(ItemWallet.getHoldings(item) > 0) {
                    GL11.glColor4f(1, 1, 1, 1);
                    renderWallet3d(item, MLItems.wallet.icon_bills);
                }
            }
            GL11.glPopMatrix();
        } else {
            renderWallet3d(item, MLItems.wallet.icon_empty);

            if(ItemWallet.getHoldings(item) > 0) {
                GL11.glColor4f(1, 1, 1, 1);
                renderWallet3d(item, MLItems.wallet.icon_bills);
            }
        }
    }

    private void renderWallet3d(ItemStack item, IIcon iicon) {
        GL11.glPushMatrix();
        {
            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }

            mc.getTextureManager().bindTexture(mc.getTextureManager().getResourceLocation(item.getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            float min_x = iicon.getMinU();
            float max_x = iicon.getMaxU();
            float min_y = iicon.getMinV();
            float max_y = iicon.getMaxV();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-0.5f, 0f, 0.0F);
            float scale = 0.9F;
            GL11.glScalef(scale, scale, scale);
            ItemRenderer.renderItemIn2D(tessellator, max_x, min_y, min_x, max_y, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        GL11.glPopMatrix();
    }
}
