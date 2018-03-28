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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum EnumAttachment {

    REDDOT(
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-3.9, 0, 1, 0), new Translation(-1.6, 0.4, -0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.5), new Rotation(-3.3, 0, 1, 0), new Translation(-0.5, 1, -1)),
            Lists.newArrayList(new Scale(0.6, 0.6, 0.6), new Translation(1.3, 0.7, -0.2)),
            TransformationList.newList(new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(0.6, 0.6, 0.6), new Translation(0.68, 2.5, -1))),
            TransformationList.newList(new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0, -0.3, 0)))),
    HOLOGRAPHIC(
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 1, 0), new Translation(0.2, 0.4, -0.2)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 0, 0), new Translation(0, 0.1, 0.2)),
            Lists.newArrayList(new Translation(0, -0.5, -0.2)),
            TransformationList.newList(new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(0.6, 0.6, 0.6), new Translation(0.68, 2.5, -1))),
            TransformationList.newList(new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0, -0.3, 0))));

    public ResourceLocation textureModel, textureReticle;
    public CCModel model;
    public TransformationList transformations, adsTransformations;
    public List<Transformation> firstPersonTransformations, thirdPersonTransformations, guiTransformations;

    EnumAttachment(List<Transformation> firstPersonTransformations, List<Transformation> thirdPersonTransformations,
                   List<Transformation> guiTransformations, TransformationList transformations, TransformationList adsTransformations) {
        textureModel = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name() + ".png");
        textureReticle = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name() + "_reticle.png");
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            this.firstPersonTransformations = firstPersonTransformations;
            this.thirdPersonTransformations = thirdPersonTransformations;
            this.guiTransformations = guiTransformations;
            this.transformations = transformations;
            this.adsTransformations = adsTransformations;
            loadClientStuff();
        }
    }

    @SideOnly(Side.CLIENT)
    public void loadClientStuff() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/guns/attachments/" + name() + ".obj"));
        model = CCModel.combine(map.values());
        model.apply(new Scale(0.3, 0.3, 0.3));
        model.apply(new Translation(0.5, 0.5, 0.5));
        model.computeNormals();
    }

    public static class AttachmentTransformations {
        public EnumGun gunType;
        public List<Transformation> transformations;

        public AttachmentTransformations(EnumGun gunType, Transformation... transformation) {
            this.gunType = gunType;
            this.transformations = Arrays.asList(transformation);
        }
    }

    public static class TransformationList extends ArrayList<AttachmentTransformations> {
        public AttachmentTransformations get(EnumGun gun) {
            return this.stream().filter(attachmentTransformation -> attachmentTransformation.gunType.equals(gun)).findFirst().orElse(null);
        }

        public static TransformationList newList(AttachmentTransformations... objects) {
            TransformationList list =  new TransformationList();
            list.addAll(Arrays.asList(objects));
            return list;
        }
    }

}
