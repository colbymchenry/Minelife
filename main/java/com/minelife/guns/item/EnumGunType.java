package com.minelife.guns.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

import java.util.List;
import java.util.Map;

public enum EnumGunType {

    AK47(70, 6, 30, 2500, 2.8, true,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -1.6, -0.6)),
            Lists.newArrayList(new Rotation(-44.77, 0, 1, 0), new Rotation(0.03, 1, 0, 0), new Translation(-1.34, -0.32, 0.2))),
    M4A4(70, 4, 40, 1300, 2.8, true,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-1, 0.4, 0.7)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -0.9, -0.3)),
            Lists.newArrayList(new Rotation(-44.77, 0, 1, 0), new Rotation(0.02, 1, 0, 0), new Translation(-1.34, 0.48, 0.2))),
    AWP(1200, 15, 5, 3100, 4.8, false,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, 0, 0.2)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -1.5, -0.1)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2))),
    BARRETT(400, 20, 5, 20, 3.8, false,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.6, -0.5, 0.2)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -1.5, 0.3)),
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.8, -0.5, 0.2))),
    DESERT_EAGLE(50, 7, 10, 2400, 2.8, false,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.4, -0.5, -0.5)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -1.6, 0.3)),
            Lists.newArrayList(new Rotation(-44.77, 0, 1, 0), new Rotation(0.04, 1, 0, 0), new Translation(-1.34, -0.12, 0.2))),
    MAGNUM(100, 5, 6, 20, 2.8, false,
            Lists.newArrayList(new Rotation(-44.75, 0, 1, 0), new Translation(-0.65, -0.1, -0.1)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-44.75, 0, 1, 0), new Rotation(5, 1, 0, 0), new Translation(0.2, -1.4, 0.1)),
            Lists.newArrayList(new Rotation(-44.77, 0, 1, 0), new Rotation(0.02, 1, 0, 0), new Translation(-1.34, 0.18, 0.2)));

    public ResourceLocation texture;
    public CCModel model;
    public final int fireRate, damage, clipSize, reloadTime;
    public final double bulletSpeed;
    public final boolean isFullAuto;
    public List<Transformation> firstPersonTransformations;
    public List<Transformation> thirdPersonTransformations;
    public List<Transformation> adsTransformations;

    EnumGunType(int fireRate, int damage, int clipSize, int reloadTime, double bulletSpeed, boolean isFullAuto,
                List<Transformation> firstPersonTransformations, List<Transformation> thirdPersonTransformations,
                List<Transformation> adsTransformations) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) loadClientStuff();
        this.fireRate = fireRate;
        this.damage = damage;
        this.clipSize = clipSize;
        this.reloadTime = reloadTime;
        this.bulletSpeed = bulletSpeed;
        this.isFullAuto = isFullAuto;
        this.firstPersonTransformations = firstPersonTransformations;
        this.thirdPersonTransformations = thirdPersonTransformations;
        this.adsTransformations = adsTransformations;
    }

    @SideOnly(Side.CLIENT)
    private void loadClientStuff() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/" + name() + ".png");
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/guns/" + name() + ".obj"));
        model = CCModel.combine(map.values());
        model.apply(new Scale(0.3, 0.3, 0.3));
        model.apply(new Translation(0.5, 0.5, 0.5));
        model.computeNormals();
    }

}
