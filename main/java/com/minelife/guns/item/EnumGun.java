package com.minelife.guns.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.client.RenderGun;
import com.minelife.permission.ModPermission;
import com.minelife.util.client.Animation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.List;

public enum EnumGun {

    AK47(null, 70, 13, 30, 2500, 6, 7, 387, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.68, 0, 1, 0), new Translation(-0.9, -0.05, -0.58)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.5, -0.6, -0.35)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.785, 0, 1, 0), new Translation(-0.9, -0.03, -0.735)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(1.1, 0.1, 0))),

    AK47_BLOODBATH(AK47, 70, 8, 30, 2500, 6, 7, 387, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.68, 0, 1, 0), new Translation(-0.9, -0.05, -0.58)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.5, -0.6, -0.35)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.785, 0, 1, 0), new Translation(-0.9, -0.03, -0.735)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(1.1, 0.1, 0))),

    AK47_BLUE_STEEL(AK47, 70, 8, 30, 2500, 6, 7, 387, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.68, 0, 1, 0), new Translation(-0.9, -0.05, -0.58)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.5, -0.6, -0.35)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.785, 0, 1, 0), new Translation(-0.9, -0.03, -0.735)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(1.1, 0.1, 0))),

    AK47_GOLD(AK47, 70, 8, 30, 2500, 6, 7, 387, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.68, 0, 1, 0), new Translation(-0.9, -0.05, -0.58)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.5, -0.6, -0.35)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.785, 0, 1, 0), new Translation(-0.9, -0.03, -0.735)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(1.1, 0.1, 0))),

    AK47_SPEC_OPS(AK47, 70, 8, 30, 2500, 6, 7, 387, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.68, 0, 1, 0), new Translation(-0.9, -0.05, -0.58)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.5, -0.6, -0.35)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.785, 0, 1, 0), new Translation(-0.9, -0.03, -0.735)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(1.1, 0.1, 0))),

    M4A4(null, 70, 4, 40, 1300, 8, 8, 386, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.85, 0, 1, 0), new Translation(-0.15, -0.05, -0.45)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.5, -0.6, -0.65)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.15, -0.05, -0.274)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(0.8, 0.5, 0))),

    M4A4_BLAZZE(M4A4, 70, 4, 40, 1300, 8, 8, 386, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.85, 0, 1, 0), new Translation(-0.15, -0.05, -0.45)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.5, -0.6, -0.65)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.15, -0.05, -0.274)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(0.8, 0.5, 0))),

    M4A4_BUMBLEBEE(M4A4, 70, 4, 40, 1300, 8, 8, 386, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.85, 0, 1, 0), new Translation(-0.15, -0.05, -0.45)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.5, -0.6, -0.65)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.15, -0.05, -0.274)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(0.8, 0.5, 0))),

    M4A4_PINEAPPLE(M4A4, 70, 4, 40, 1300, 8, 8, 386, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.85, 0, 1, 0), new Translation(-0.15, -0.05, -0.45)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.5, -0.6, -0.65)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.15, -0.05, -0.274)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(0.8, 0.5, 0))),

    M4A4_BLACK_MESA(M4A4, 70, 4, 40, 1300, 8, 8, 386, 2.8, true,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.85, 0, 1, 0), new Translation(-0.15, -0.05, -0.45)),
            Lists.newArrayList(new Scale(5, 5, 5), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.5, -0.6, -0.65)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.15, -0.05, -0.274)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.4), new Translation(0.8, 0.5, 0))),

    AWP(null, 1200, 35, 5, 3100, 8, 7, 389, 4.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.53, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    AWP_GHOST(AWP, 1200, 35, 5, 3100, 8, 7, 389, 4.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.53, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    AWP_SPEC_OPS(AWP, 1200, 35, 5, 3100, 8, 7, 389, 4.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.53, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    BARRETT(null, 400, 25, 5, 20, 6, 8, 392, 3.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.55, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    BARRETT_HOT_ROD(BARRETT, 400, 25, 5, 20, 6, 8, 392, 3.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.55, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    BARRETT_PINEAPPLE(BARRETT, 400, 25, 5, 20, 6, 8, 392, 3.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(0.71, 0, 1, 0), new Translation(-0.7, -0.02, -0.58)),
            Lists.newArrayList(new Scale(6, 6, 6), new Rotation(0.9, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.2, 1, 0, 0), new Translation(-0.4, -0.55, -0.38)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.3), new Translation(1.2, 0.5, 0))),

    DESERT_EAGLE(null, 50, 15, 10, 2400, 5, 7, 385, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.2, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.4, -0.12, -0.275)),
            Lists.newArrayList(new Translation(0.2, -0.65, 0))),

    DESERT_EAGLE_SHOCK(DESERT_EAGLE, 50, 15, 10, 2400, 5, 7, 385, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.2, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.4, -0.12, -0.275)),
            Lists.newArrayList(new Translation(0.2, -0.65, 0))),

    DESERT_EAGLE_BLACK_MESA(DESERT_EAGLE, 50, 15, 10, 2400, 5, 7, 385, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.2, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.4, -0.12, -0.275)),
            Lists.newArrayList(new Translation(0.2, -0.65, 0))),

    DESERT_EAGLE_PINEAPPLE(DESERT_EAGLE, 50, 15, 10, 2400, 5, 7, 385, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.2, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Translation(-0.4, -0.12, -0.275)),
            Lists.newArrayList(new Translation(0.2, -0.65, 0))),

    MAGNUM(null, 100, 17, 6, 20, 6, 9, 380, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.3, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Rotation(0.01, 0, 0, 1), new Translation(-0.4, -0.185, -0.275)),
            Lists.newArrayList(new Translation(-0.1, -0.6, 0))),

    MAGNUM_KINGPIN(MAGNUM, 100, 17, 6, 20, 6, 9, 380, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.3, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Rotation(0.01, 0, 0, 1), new Translation(-0.4, -0.185, -0.275)),
            Lists.newArrayList(new Translation(-0.1, -0.6, 0))),

    MAGNUM_RUST_COAT(MAGNUM, 100, 17, 6, 20, 6, 9, 380, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.3, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Rotation(0.01, 0, 0, 1), new Translation(-0.4, -0.185, -0.275)),
            Lists.newArrayList(new Translation(-0.1, -0.6, 0))),

    MAGNUM_SPEC_OPS(MAGNUM, 100, 17, 6, 20, 6, 9, 380, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.3, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Rotation(0.01, 0, 0, 1), new Translation(-0.4, -0.185, -0.275)),
            Lists.newArrayList(new Translation(-0.1, -0.6, 0))),

    MAGNUM_BLACK_MESA(MAGNUM, 100, 17, 6, 20, 6, 9, 380, 2.8, false,
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.8, 0, 1, 0), new Translation(-0.5, -0.3, -0.58)),
            Lists.newArrayList(new Scale(2, 2, 2), new Rotation(1.4, 0, 1, 0), new Rotation(-1.3, 0, 0, 1), new Rotation(0.6, 1, 0, 0), new Rotation(3, 0, 1, 0), new Translation(-0.3, -0.7, -0.9)),
            Lists.newArrayList(new Scale(3, 3, 3), new Rotation(3.925, 0, 1, 0), new Rotation(0.01, 0, 0, 1), new Translation(-0.4, -0.185, -0.275)),
            Lists.newArrayList(new Translation(-0.1, -0.6, 0)));

    public ResourceLocation texture, soundShot, soundReload, soundEmpty;
    public CCModel model;
    public final int fireRate, damage, clipSize, reloadTime;
    public final double bulletSpeed;
    public final boolean isFullAuto;
    public List<Transformation> firstPersonTransformations;
    public List<Transformation> thirdPersonTransformations;
    public List<Transformation> adsTransformations;
    public List<Transformation> guiTransformations;
    public Animation shotAnimation;
    public double width, height, length;
    public EnumGun defaultSkin;

    EnumGun(EnumGun defaultSkin, int fireRate, int damage, int clipSize, int reloadTime, double width, double height, double length, double bulletSpeed, boolean isFullAuto,
            List<Transformation> firstPersonTransformations, List<Transformation> thirdPersonTransformations,
            List<Transformation> adsTransformations,
            List<Transformation> guiTransformations) {
        this.defaultSkin = defaultSkin;
        this.fireRate = fireRate;
        this.damage = damage;
        this.clipSize = clipSize;
        this.reloadTime = reloadTime;
        this.width = width;
        this.height = height;
        this.length = length;
        this.bulletSpeed = bulletSpeed;
        this.isFullAuto = isFullAuto;
        this.firstPersonTransformations = firstPersonTransformations;
        this.thirdPersonTransformations = thirdPersonTransformations;
        this.adsTransformations = adsTransformations;
        this.guiTransformations = guiTransformations;
        this.shotAnimation = new Animation(0, 0, 0);
        this.soundShot = new ResourceLocation(Minelife.MOD_ID, "guns." + name() + ".shot");
        this.soundReload = new ResourceLocation(Minelife.MOD_ID, "guns." + name() + ".reload");
        this.soundEmpty = new ResourceLocation(Minelife.MOD_ID, "guns.empty");
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (int i = 0; i < EnumGun.values().length; i++) {
            ModelResourceLocation model = new ModelResourceLocation("minelife:" + EnumGun.values()[i].name(), "inventory");
            ModelLoader.setCustomModelResourceLocation(ModGuns.itemGun, i, model);
            ModelRegistryHelper.register(model, new RenderGun(() -> model));
        }
    }

    public void resetAnimation() {
        boolean mouseDown = Mouse.isButtonDown(1);
        switch (this) {
            case AK47:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case AK47_BLOODBATH:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case AK47_BLUE_STEEL:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case AK47_SPEC_OPS:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case AK47_GOLD:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case M4A4:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case M4A4_BLAZZE:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case M4A4_PINEAPPLE:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case M4A4_BUMBLEBEE:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case M4A4_BLACK_MESA:
                if (mouseDown)
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                else
                    this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.09f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case AWP:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.6f, 0, 0, 0.05f).translateTo(0, 0, 0f, 0.05f);
                break;
            case AWP_GHOST:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.6f, 0, 0, 0.05f).translateTo(0, 0, 0f, 0.05f);
                break;
            case AWP_SPEC_OPS:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.6f, 0, 0, 0.05f).translateTo(0, 0, 0f, 0.05f);
                break;
            case BARRETT:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.4f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case BARRETT_HOT_ROD:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.4f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case BARRETT_PINEAPPLE:
                this.shotAnimation = new Animation(0, 0, 0f).translateTo(-0.4f, 0, 0, 0.03f).translateTo(0, 0, 0f, 0.03f);
                break;
            case MAGNUM:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case MAGNUM_KINGPIN:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case MAGNUM_RUST_COAT:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case MAGNUM_SPEC_OPS:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case MAGNUM_BLACK_MESA:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case DESERT_EAGLE:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case DESERT_EAGLE_SHOCK:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case DESERT_EAGLE_BLACK_MESA:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
            case DESERT_EAGLE_PINEAPPLE:
                this.shotAnimation = new Animation(0, 0, 0).rotateTo(Animation.EnumRotation.z, -35, 3.5f).rotateTo(Animation.EnumRotation.z, 0, 4.5f);
                break;
        }
    }

    public int[] getRecoilYaw() {
        boolean aimingDownSight = Mouse.isButtonDown(1);
        switch (this) {
            case AK47:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
            case AK47_BLOODBATH:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
            case AK47_BLUE_STEEL:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
            case AK47_SPEC_OPS:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
            case AK47_GOLD:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{1, 5};
            case AWP:
                return new int[]{1, 10};
            case AWP_GHOST:
                return new int[]{1, 10};
            case AWP_SPEC_OPS:
                return new int[]{1, 10};
            case BARRETT:
                return new int[]{5, 10};
            case BARRETT_HOT_ROD:
                return new int[]{5, 10};
            case BARRETT_PINEAPPLE:
                return new int[]{5, 10};
            case DESERT_EAGLE:
                return new int[]{1, 10};
            case DESERT_EAGLE_SHOCK:
                return new int[]{1, 10};
            case DESERT_EAGLE_BLACK_MESA:
                return new int[]{1, 10};
            case DESERT_EAGLE_PINEAPPLE:
                return new int[]{1, 10};
            case M4A4:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{2, 8};
            case M4A4_BLAZZE:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{2, 8};
            case M4A4_BUMBLEBEE:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{2, 8};
            case M4A4_PINEAPPLE:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{2, 8};
            case M4A4_BLACK_MESA:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{2, 8};
            case MAGNUM:
                return new int[]{10, 20};
            case MAGNUM_BLACK_MESA:
                return new int[]{10, 20};
            case MAGNUM_KINGPIN:
                return new int[]{10, 20};
            case MAGNUM_RUST_COAT:
                return new int[]{10, 20};
            case MAGNUM_SPEC_OPS:
                return new int[]{10, 20};
        }
        return new int[]{0, 0};
    }

    public int[] getRecoilPitch() {
        boolean aimingDownSight = Mouse.isButtonDown(1);
        switch (this) {
            case AK47:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{5, 10};
            case AK47_BLOODBATH:
                return !aimingDownSight ? new int[]{10, 20} : new int[]{5, 10};
            case AWP:
                return new int[]{20, 60};
            case AWP_GHOST:
                return new int[]{20, 60};
            case AWP_SPEC_OPS:
                return new int[]{20, 60};
            case BARRETT:
                return new int[]{20, 90};
            case BARRETT_HOT_ROD:
                return new int[]{20, 90};
            case BARRETT_PINEAPPLE:
                return new int[]{20, 90};
            case DESERT_EAGLE:
                return new int[]{150, 200};
            case DESERT_EAGLE_SHOCK:
                return new int[]{150, 200};
            case DESERT_EAGLE_BLACK_MESA:
                return new int[]{150, 200};
            case DESERT_EAGLE_PINEAPPLE:
                return new int[]{150, 200};
            case M4A4:
                return !aimingDownSight ? new int[]{1, 10} : new int[]{1, 4};
            case M4A4_BLAZZE:
                return !aimingDownSight ? new int[]{1, 10} : new int[]{1, 4};
            case M4A4_BUMBLEBEE:
                return !aimingDownSight ? new int[]{1, 10} : new int[]{1, 4};
            case M4A4_PINEAPPLE:
                return !aimingDownSight ? new int[]{1, 10} : new int[]{1, 4};
            case M4A4_BLACK_MESA:
                return !aimingDownSight ? new int[]{1, 10} : new int[]{1, 4};
            case MAGNUM:
                return new int[]{150, 200};
            case MAGNUM_BLACK_MESA:
                return new int[]{150, 200};
            case MAGNUM_KINGPIN:
                return new int[]{150, 200};
            case MAGNUM_RUST_COAT:
                return new int[]{150, 200};
            case MAGNUM_SPEC_OPS:
                return new int[]{150, 200};
        }
        return new int[]{0, 0};
    }

    public int getReboundSpeed() {
        switch (this) {
            case AK47:
                return 8;
            case AK47_BLOODBATH:
                return 8;
            case AK47_BLUE_STEEL:
                return 8;
            case AK47_GOLD:
                return 8;
            case AK47_SPEC_OPS:
                return 8;
            case AWP:
                return 8;
            case AWP_GHOST:
                return 8;
            case AWP_SPEC_OPS:
                return 8;
            case BARRETT:
                return 8;
            case BARRETT_HOT_ROD:
                return 8;
            case BARRETT_PINEAPPLE:
                return 8;
            case DESERT_EAGLE:
                return 2;
            case DESERT_EAGLE_SHOCK:
                return 2;
            case DESERT_EAGLE_BLACK_MESA:
                return 2;
            case DESERT_EAGLE_PINEAPPLE:
                return 2;
            case M4A4:
                return 8;
            case M4A4_BLAZZE:
                return 8;
            case M4A4_BUMBLEBEE:
                return 8;
            case M4A4_PINEAPPLE:
                return 8;
            case M4A4_BLACK_MESA:
                return 8;
            case MAGNUM:
                return 2;
            case MAGNUM_BLACK_MESA:
                return 2;
            case MAGNUM_KINGPIN:
                return 2;
            case MAGNUM_RUST_COAT:
                return 2;
            case MAGNUM_SPEC_OPS:
                return 2;
        }
        return 10;
    }

    public static List<EnumGun> getGunSkins(EntityPlayerMP player, ItemStack gunStack) {
        if (gunStack.getItem() != ModGuns.itemGun) return Lists.newArrayList();

        List<EnumGun> availableSkins = Lists.newArrayList();
        EnumGun gunType = EnumGun.values()[gunStack.getMetadata()];

        for (EnumGun gun : EnumGun.values()) {
            boolean sameGunType = gunType.name().contains(gun.name().contains("_") ? gun.name().split("_")[0] : gun.name());
            if (sameGunType && (gun.defaultSkin == null || (gun.defaultSkin != null && ModPermission.hasPermission(player.getUniqueID(), "gun.skin." + gun.name().toLowerCase()))))
                availableSkins.add(gun);
        }

        return availableSkins;
    }

}
