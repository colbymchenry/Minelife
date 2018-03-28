package com.minelife.guns.item;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;

public enum EnumAttachmentType {

    REDDOT(Maps.newHashMap(EnumGunType.AWP, null)),
    HOLOGRAPHIC;

    public ResourceLocation textureModel, textureReticle;
    public CCModel model;
    public Map<EnumGunType, List<Transformation>> transformations;

    EnumAttachmentType() {
        textureModel = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name());
        textureReticle = new ResourceLocation(Minelife.MOD_ID, "textures/item/guns/attachments/" + name() + "_reticle");
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) loadClientStuff();
    }

    @SideOnly(Side.CLIENT)
    public void loadClientStuff() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/guns/attachments/" + name() + ".obj"));
        model = CCModel.combine(map.values());
        model.apply(new Scale(0.3, 0.3, 0.3));
        model.apply(new Translation(0.5, 0.5, 0.5));
        model.computeNormals();
    }

}
