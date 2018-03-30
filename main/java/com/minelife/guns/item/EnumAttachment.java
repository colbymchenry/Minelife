package com.minelife.guns.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum EnumAttachment {

    REDDOT(new ItemStack(ModGuns.itemAttachment, 1, 0),
            Lists.newArrayList(new Scale(0.01f, 0.01f, 0.01f), new Translation(-42, 52, 150)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-3.9, 0, 1, 0), new Translation(-1.6, 0.4, -0.2)),
            Lists.newArrayList(new Scale(0.5, 0.5, 0.5), new Rotation(-3.3, 0, 1, 0), new Translation(-0.5, 1, -1)),
            Lists.newArrayList(new Scale(0.6, 0.6, 0.6), new Translation(1.3, 0.7, -0.2)),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(1.1, 1.1, 1.1), new Translation(0.33, 1.81, 0), new Rotation(-0.03, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Scale(1, 1, 1), new Translation(0.39, 1.67, 0.3), new Rotation(-0.03, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Scale(1, 1, 1), new Translation(0.39, 2.15, 0.7), new Rotation(-0.03, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Scale(1, 1, 1), new Translation(0.39, 1.25, 0.7), new Rotation(-0.03, 0, 1, 0))
            ),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0.002, -0.28, 0.5)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Translation(0, -0.238, 0.5)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Translation(0, -0.202, 0.3)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Translation(0, -0.112, 0.3))
            )),
    HOLOGRAPHIC(new ItemStack(ModGuns.itemAttachment, 1, 1),
            Lists.newArrayList(new Scale(0.005f, 0.005f, 0.005f), new Translation(49, 206, 120)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 1, 0), new Translation(0.2, 0.4, -0.2)),
            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 0, 0), new Translation(0, 0.1, 0.2)),
            Lists.newArrayList(new Translation(0, -0.5, -0.2)),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(1.3, 1.3, 1.3), new Translation(0.59, 1.31, -0.3), new Rotation(-0.08, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM,new Scale(1.1, 1.1, 1.1), new Translation(0.682, 1.3, 0.2), new Rotation(-0.09, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47,  new Scale(1.3, 1.3, 1.3), new Translation(0.59, 1.45, 0.8), new Rotation(-0.07, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4,  new Scale(1.3, 1.3, 1.3), new Translation(0.59, 0.75, 0.2), new Rotation(-0.08, 0, 1, 0))
            ),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0.002, -0.165, 0.5)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Translation(-0.001, -0.135, 0.2)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Translation(-0.001, -0.135, -0.4)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Translation(-0.001, -0.045, 0.2))
            ));

    public ItemStack stack;
    public ResourceLocation textureModel, textureReticle;
    public CCModel model;
    public TransformationList transformations, gunADSTransformation;
    public List<Transformation> reticleTransformations, firstPersonTransformations, thirdPersonTransformations, guiTransformations;

    EnumAttachment(ItemStack stack, List<Transformation> reticleTransformations, List<Transformation> firstPersonTransformations,
                   List<Transformation> thirdPersonTransformations, List<Transformation> guiTransformations,
                   TransformationList transformations, TransformationList gunADSTransformation) {
        textureModel = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name() + ".png");
        textureReticle = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name() + "_reticle.png");
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            this.stack = stack;
            this.reticleTransformations = reticleTransformations;
            this.firstPersonTransformations = firstPersonTransformations;
            this.thirdPersonTransformations = thirdPersonTransformations;
            this.guiTransformations = guiTransformations;
            this.transformations = transformations;
            this.gunADSTransformation = gunADSTransformation;
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
            TransformationList list = new TransformationList();
            list.addAll(Arrays.asList(objects));
            return list;
        }
    }

}
