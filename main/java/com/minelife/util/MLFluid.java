package com.minelife.util;

import com.minelife.Minelife;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class MLFluid extends Fluid {

    protected int mapColor = 0xFFFFFFFF;
    protected float overlayAlpha = 0.2F;
    protected SoundEvent emptySound = SoundEvents.ITEM_BUCKET_EMPTY;
    protected SoundEvent fillSound = SoundEvents.ITEM_BUCKET_FILL;

    /**
     * Instantiates a new mod fluid.
     *
     * @param fluidName the fluid name
     */
    public MLFluid(String fluidName) {
        super(fluidName, new ResourceLocation(Minelife.MOD_ID, "block/fluid/" + fluidName + "_still"),
                new ResourceLocation(Minelife.MOD_ID, "block/fluid/"  + fluidName + "_flow"));
        FluidRegistry.addBucketForFluid(this);
        setUnlocalizedName(fluidName);
    }

    /**
     * Instantiates a new mod fluid.
     *
     * @param fluidName the fluid name
     * @param mapColor  the map color
     */
    public MLFluid(String fluidName, int mapColor) {
        this(fluidName);
        setColor(mapColor);
    }

    /**
     * Instantiates a new mod fluid.
     *
     * @param fluidName    the fluid name
     * @param mapColor     the map color
     * @param overlayAlpha the overlay alpha
     */
    public MLFluid(String fluidName, int mapColor, float overlayAlpha) {
        this(fluidName, mapColor);
        setAlpha(overlayAlpha);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.Fluid#getColor()
     */
    @Override
    public int getColor() {
        return mapColor;
    }

    /**
     * Sets the color.
     *
     * @param parColor the par color
     * @return the fluid
     */
    @Override
    public MLFluid setColor(int parColor) {
        mapColor = parColor;
        return this;
    }

    /**
     * Gets the alpha.
     *
     * @return the alpha
     */
    public float getAlpha() {
        return overlayAlpha;
    }

    /**
     * Sets the alpha.
     *
     * @param parOverlayAlpha the par overlay alpha
     * @return the fluid
     */
    public MLFluid setAlpha(float parOverlayAlpha) {
        overlayAlpha = parOverlayAlpha;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.Fluid#setEmptySound(net.minecraft.util.SoundEvent)
     */
    @Override
    public MLFluid setEmptySound(SoundEvent parSound) {
        emptySound = parSound;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.Fluid#getEmptySound()
     */
    @Override
    public SoundEvent getEmptySound() {
        return emptySound;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.Fluid#setFillSound(net.minecraft.util.SoundEvent)
     */
    @Override
    public MLFluid setFillSound(SoundEvent parSound) {
        fillSound = parSound;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraftforge.fluids.Fluid#getFillSound()
     */
    @Override
    public SoundEvent getFillSound() {
        return fillSound;
    }
}