package com.minelife.gun.client.guns;

import com.minelife.Minelife;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class Attachment {

    private final IModelCustom model;
    private final ResourceLocation texture, objModel;

    public Attachment(String name) {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/" + name + ".png");
        objModel = new ResourceLocation(Minelife.MOD_ID, "models/guns/attachments/" + name + ".obj");
        model = AdvancedModelLoader.loadModel(objModel);
    }

    public IModelCustom getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
