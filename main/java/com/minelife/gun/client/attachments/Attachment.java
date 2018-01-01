package com.minelife.gun.client.attachments;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.gun.client.RenderAttachment;
import com.minelife.gun.item.guns.ItemGun;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public abstract class Attachment {

    private final IModelCustom model;
    private final ResourceLocation texture, objModel, reticle;

    public Attachment(String name) {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/" + name + ".png");
        reticle = new ResourceLocation(Minelife.MOD_ID, "textures/guns/attachments/" + name + "_reticle.png");
        objModel = new ResourceLocation(Minelife.MOD_ID, "models/guns/attachments/" + name + ".obj");
        model = AdvancedModelLoader.loadModel(objModel);
    }

    public abstract void applyTransformations(IItemRenderer.ItemRenderType type, ItemStack item);

    public abstract void applyTransformationsAttached(ItemStack gun);

    public IModelCustom getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ResourceLocation getReticleTexture() {
        return reticle;
    }

    private static AttachmentHolographicSite holographic;
    private static Attachment2xSite twoXSite;
    private static AttachmentAcogSite acogSite;
    private static AttachmentReddotSite reddotSite;

    public static void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer(MLItems.holographicSite, new RenderAttachment(holographic = new AttachmentHolographicSite()));
        MinecraftForgeClient.registerItemRenderer(MLItems.twoXSite, new RenderAttachment(twoXSite = new Attachment2xSite()));
        MinecraftForgeClient.registerItemRenderer(MLItems.acogSite, new RenderAttachment(acogSite = new AttachmentAcogSite()));
        MinecraftForgeClient.registerItemRenderer(MLItems.reddotSite, new RenderAttachment(reddotSite = new AttachmentReddotSite()));
    }

    public static AttachmentHolographicSite getHolographic() {
        return holographic;
    }

    public static Attachment2xSite getTwoXSite() {
        return twoXSite;
    }

    public static AttachmentAcogSite getAcogSite() {
        return acogSite;
    }

    public static AttachmentReddotSite getReddotSite() {
        return reddotSite;
    }
}
