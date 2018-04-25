package com.minelife.guns.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.client.RenderAttachment;
import com.minelife.guns.client.RenderGun;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum EnumAttachment {

    RED_DOT(new ItemStack(ModGuns.itemAttachment, 1, 0),
            Lists.newArrayList(new Scale(0.005, 0.005, 0.005), new Translation(40, 4, -38), new Rotation(-90f, 0, 1, 0)),
            Lists.newArrayList(new Scale(2, 0.8, 0.8), new Rotation(-3.9, 0, 1, 0), new Translation(-1.6, 0.4, -0.2)),
            Lists.newArrayList(new Scale(2, 0.5, 2), new Rotation(-3.3, 0, 1, 0), new Translation(-0.5, 1, -1)),
            Lists.newArrayList(new Scale(2, 2, 2), new Translation(1.3, 0.7, -0.2)),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Scale(0.1, 0.1, 0.1), new Translation(-0.1, 0.24, 0.018), new Rotation(3.14, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47_BLOODBATH, new Scale(0.1, 0.1, 0.1), new Translation(-0.1, 0.24, 0.018), new Rotation(3.14, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Scale(0.15, 0.15, 0.15), new Translation(-0.01, 0.16, -0.001), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_BLAZZE, new Scale(0.15, 0.15, 0.15), new Translation(-0.01, 0.16, -0.001), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_PINEAPPLE, new Scale(0.15, 0.15, 0.15), new Translation(-0.01, 0.16, -0.001), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_BUMBLEBEE, new Scale(0.15, 0.15, 0.15), new Translation(-0.01, 0.16, -0.001), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(0.25, 0.25, 0.25), new Translation(-0.26, 0.18, -0.005), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE_SHOCK, new Scale(0.25, 0.25, 0.25), new Translation(-0.26, 0.18, -0.005), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Scale(0.25, 0.25, 0.25), new Translation(-0.26, 0.23, -0.005), new Rotation(0, 0, 1, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM_BLACK_MESA, new Scale(0.25, 0.25, 0.25), new Translation(-0.26, 0.23, -0.005), new Rotation(0, 0, 1, 0))
            ),
            TransformationList.newList(
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Translation(0, -0.05, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM_BLACK_MESA, new Translation(0, -0.05, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47_BLOODBATH, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_BLAZZE, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_PINEAPPLE, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4_BUMBLEBEE, new Translation(0, -0.01, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0, -0.07, 0)),
                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE_SHOCK, new Translation(0, -0.07, 0))));
//    HOLOGRAPHIC(new ItemStack(ModGuns.itemAttachment, 1, 1),
//            Lists.newArrayList(new Scale(0.005f, 0.005f, 0.005f), new Translation(49, 206, 120)),
//            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 1, 0), new Translation(0.2, 0.4, -0.2)),
//            Lists.newArrayList(new Scale(0.8, 0.8, 0.8), new Rotation(-0.7, 0, 0, 0), new Translation(0, 0.1, 0.2)),
//            Lists.newArrayList(new Translation(0, -0.5, -0.2)),
//            TransformationList.newList(
//                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Scale(1.3, 1.3, 1.3), new Translation(0.59, 1.31, -0.3), new Rotation(-0.08, 0, 1, 0)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM,new Scale(1.1, 1.1, 1.1), new Translation(0.682, 1.3, 0.2), new Rotation(-0.09, 0, 1, 0)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47,  new Scale(1.3, 1.3, 1.3), new Translation(0.59, 1.45, 0.8), new Rotation(-0.07, 0, 1, 0)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4,  new Scale(1.3, 1.3, 1.3), new Translation(0.59, 0.75, 0.2), new Rotation(-0.08, 0, 1, 0))
//            ),
//            TransformationList.newList(
//                    new EnumAttachment.AttachmentTransformations(EnumGun.DESERT_EAGLE, new Translation(0.002, -0.165, 0.5)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.MAGNUM, new Translation(-0.001, -0.135, 0.2)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.AK47, new Translation(-0.001, -0.135, -0.4)),
//                    new EnumAttachment.AttachmentTransformations(EnumGun.M4A4, new Translation(-0.001, -0.045, 0.2))
//            ));

    public ItemStack stack;
    public ResourceLocation textureReticle;
    public TransformationList transformations, gunADSTransformation;
    public List<Transformation> reticleTransformations, firstPersonTransformations, thirdPersonTransformations, guiTransformations;

    EnumAttachment(ItemStack stack, List<Transformation> reticleTransformations, List<Transformation> firstPersonTransformations,
                   List<Transformation> thirdPersonTransformations, List<Transformation> guiTransformations,
                   TransformationList transformations, TransformationList gunADSTransformation) {
        textureReticle = new ResourceLocation(Minelife.MOD_ID, "textures/item/" + name() + "_reticle.png");
        this.stack = stack;
        this.reticleTransformations = reticleTransformations;
        this.firstPersonTransformations = firstPersonTransformations;
        this.thirdPersonTransformations = thirdPersonTransformations;
        this.guiTransformations = guiTransformations;
        this.transformations = transformations;
        this.gunADSTransformation = gunADSTransformation;
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

        public boolean contains(EnumGun gun) {
            return get(gun) != null;
        }

        public static TransformationList newList(AttachmentTransformations... objects) {
            TransformationList list = new TransformationList();
            list.addAll(Arrays.asList(objects));
            return list;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (int i = 0; i < EnumAttachment.values().length; i++) {
            ModelResourceLocation model = new ModelResourceLocation("minelife:" + EnumAttachment.values()[i].name(), "inventory");
            ModelLoader.setCustomModelResourceLocation(ModGuns.itemAttachment, i, model);
            ModelRegistryHelper.register(model, new RenderAttachment(() -> model));
        }
    }

}
