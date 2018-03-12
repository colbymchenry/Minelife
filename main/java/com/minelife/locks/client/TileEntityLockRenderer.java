package com.minelife.locks.client;

import com.minelife.Minelife;
import com.minelife.locks.LockType;
import com.minelife.locks.TileEntityLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
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

        if (tileLock.lockType != null) {
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

        // TODO: Need to prevent levers from opening locked doors


        // https://minecraft.gamepedia.com/Java_Edition_data_values#Door
        boolean isDoor = tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ) == Blocks.wooden_door;
        boolean isTopDoor = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x8) != (byte) 0;
        boolean isDoorOpen = false;
        boolean hingeOnTheLeft = false;

        if(isDoor) {
            if(isTopDoor) {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY - 1, tileLock.protectZ) & 0x4) == (byte) 4;
                hingeOnTheLeft = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x1) == (byte) 0;
            } else {
                isDoorOpen = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY, tileLock.protectZ) & 0x4) == (byte) 4;
                hingeOnTheLeft = (tileLock.getWorldObj().getBlockMetadata(tileLock.protectX, tileLock.protectY + 1, tileLock.protectZ) & 0x1) == (byte) 0;
            }
        }

        Block block = tileLock.getWorldObj().getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ);
        Block blockLock = tileLock.getWorldObj().getBlock(tileLock.xCoord, tileLock.yCoord, tileLock.zCoord);

        AxisAlignedBB bounds = block.getSelectedBoundingBoxFromPool(tileLock.getWorldObj(), tileLock.protectX, tileLock.protectY, tileLock.protectZ);
        AxisAlignedBB lockBounds = blockLock.getSelectedBoundingBoxFromPool(tileLock.getWorldObj(), tileLock.xCoord, tileLock.yCoord, tileLock.zCoord);

        float widthX = (float) (bounds.maxX - bounds.minX);
        float widthZ = (float) (bounds.maxZ - bounds.minZ);
        float lockWidthX = (float) (lockBounds.maxX - lockBounds.minX);
        float lockWidthZ = (float) (lockBounds.maxZ - lockBounds.minZ);
        float lockHeight = (float) (lockBounds.maxY - lockBounds.minY);
        float distanceZ = (float) (tileLock.zCoord - bounds.minZ);
        float lastDistanceZ = (float) ((tileLock.zCoord + 0.5) - bounds.maxZ);
        float lastDistanceX = (float) ((tileLock.xCoord + 0.5) - bounds.maxX);
        float lastDistanceXX = (float) ((tileLock.xCoord - 0.5) - bounds.minX);
        float lastDistanceZZ = (float) ((tileLock.zCoord - 0.5) - bounds.minZ);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z);

            if (tileLock.protectZ == tileLock.zCoord - 1) {
                GL11.glTranslatef(0f, 0f, -lastDistanceZ);
                GL11.glRotatef(-90f, 0f, 1f, 0f);
                GL11.glTranslatef(0.03f, 0f, -1f);
            } else if (tileLock.protectX == tileLock.xCoord - 1) {
                GL11.glTranslatef(-lastDistanceX, 0f, 0);
                GL11.glRotatef(0f, 0f, 1f, 0f);
                GL11.glTranslatef(0.03f, 0f, 0f);
            } else if (tileLock.protectZ == tileLock.zCoord + 1) {
                GL11.glTranslatef(0f, 0f, -lastDistanceZZ);
                GL11.glRotatef(90f, 0f, 1f, 0f);
                GL11.glTranslatef(0.03f, 0f, 0f);
            } else if (tileLock.protectX == tileLock.xCoord + 1) {
                GL11.glTranslatef(-lastDistanceXX, 0f, 0f);
                GL11.glRotatef(180f, 0f, 1f, 0f);
                GL11.glTranslatef(0.03f, 0f, -1f);
            }

            if(isDoorOpen) {
                if(hingeOnTheLeft) {
                    GL11.glRotatef(90f, 0f, 1f, 0f);
                    GL11.glTranslatef(-1.3f, 0f, -0.5f);
                } else {
                    GL11.glRotatef(-90f, 0f, 1f, 0f);
                    GL11.glTranslatef(-0.3f, 0f, -0.46f);
                }
            }
            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
