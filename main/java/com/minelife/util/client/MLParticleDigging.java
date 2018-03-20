package com.minelife.util.client;

import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MLParticleDigging extends Particle {

    private String texture;
    private final IBlockState sourceState;
    private BlockPos sourcePos;

    public MLParticleDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state, String texture) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.sourceState = state;
        this.setParticleTexture(TextureUtils.getTexture(texture));
        this.texture = texture;
        this.particleGravity = state.getBlock().blockParticleGravity;
        this.particleRed = 0.6F;
        this.particleGreen = 0.6F;
        this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    /**
     * Sets the position of the block that this particle came from. Used for calculating texture and color multiplier.
     */
    public MLParticleDigging setBlockPos(BlockPos pos) {
        this.sourcePos = pos;

        if (this.sourceState.getBlock() == Blocks.GRASS) {
            return this;
        } else {
            this.multiplyColor(pos);
            return this;
        }
    }

    public MLParticleDigging init() {
        this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
        Block block = this.sourceState.getBlock();

        if (block == Blocks.GRASS) {
            return this;
        } else {
            this.multiplyColor(this.sourcePos);
            return this;
        }
    }

    protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
        int i = Minecraft.getMinecraft().getBlockColors().colorMultiplier(this.sourceState, this.world, p_187154_1_, 0);
        this.particleRed *= (float) (i >> 16 & 255) / 255.0F;
        this.particleGreen *= (float) (i >> 8 & 255) / 255.0F;
        this.particleBlue *= (float) (i & 255) / 255.0F;
    }

    /**
     * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet,
     * 1 for the main Texture atlas, and 3 for a custom texture
     */
    public int getFXLayer() {
        return 1;
    }

    /**
     * Renders the particle
     */
    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        TextureUtils.changeTexture(this.texture);
        float f = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float f1 = f + 0.015609375F;
        float f2 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float f3 = f2 + 0.015609375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null) {
            f = this.particleTexture.getInterpolatedU((double) (this.particleTextureJitterX / 4.0F * 16.0F));
            f1 = this.particleTexture.getInterpolatedU((double) ((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            f2 = this.particleTexture.getInterpolatedV((double) (this.particleTextureJitterY / 4.0F * 16.0F));
            f3 = this.particleTexture.getInterpolatedV((double) ((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        buffer.pos((double) (f5 - rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4), (double) (f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double) f, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double) (f5 - rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4), (double) (f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double) f, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double) (f5 + rotationX * f4 + rotationXY * f4), (double) (f6 + rotationZ * f4), (double) (f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double) f1, (double) f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
        buffer.pos((double) (f5 + rotationX * f4 - rotationXY * f4), (double) (f6 - rotationZ * f4), (double) (f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double) f1, (double) f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(j, k).endVertex();
    }

    public int getBrightnessForRender(float p_189214_1_) {
        int i = super.getBrightnessForRender(p_189214_1_);
        int j = 0;

        if (this.world.isBlockLoaded(this.sourcePos)) {
            j = this.world.getCombinedLight(this.sourcePos, 0);
        }

        return i == 0 ? j : i;
    }

    public static void addDestroyEffect(World world, BlockPos pos, ParticleManager manager, String texture) {
        IBlockState state = world.getBlockState(pos);
        if (!state.getBlock().isAir(state, world, pos)) {
            state = state.getActualState(world, pos);

            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        double d0 = ((double) j + 0.5D) / 4.0D;
                        double d1 = ((double) k + 0.5D) / 4.0D;
                        double d2 = ((double) l + 0.5D) / 4.0D;
                        manager.addEffect((new MLParticleDigging(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, state, texture)).setBlockPos(pos));
                    }
                }
            }
        }
    }

    public static void addBreakEffect(World world, BlockPos pos, EnumFacing sideHit, ParticleManager manager, String texture) {
        IBlockState iblockstate = world.getBlockState(pos);

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        float f = 0.1F;
        AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, pos);
        double d0 = (double) i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
        double d1 = (double) j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
        double d2 = (double) k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

        if (sideHit == EnumFacing.DOWN) {
            d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
        }

        if (sideHit == EnumFacing.UP) {
            d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
        }

        if (sideHit == EnumFacing.NORTH) {
            d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
        }

        if (sideHit == EnumFacing.SOUTH) {
            d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
        }

        if (sideHit == EnumFacing.WEST) {
            d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
        }

        if (sideHit == EnumFacing.EAST) {
            d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
        }

        manager.addEffect((new MLParticleDigging(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate, texture)).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
    }
}