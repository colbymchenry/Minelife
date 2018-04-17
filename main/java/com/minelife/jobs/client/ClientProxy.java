package com.minelife.jobs.client;

import com.minelife.MLProxy;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.job.bountyhunter.ItemBountyCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityJobNPC.class, RenderEntityJobNPC::new);
        registerRenderer(ItemBountyCard.INSTANCE);
    }

    private void registerRenderer(Item item) {
        ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName().toString(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, model);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, model);
    }

}
