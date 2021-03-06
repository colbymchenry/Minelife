package com.minelife.drugs.client;

import codechicken.lib.model.ModelRegistryHelper;
import com.minelife.MLProxy;
import com.minelife.drugs.ModDrugs;
import com.minelife.drugs.block.HempStateMapper;
import com.minelife.drugs.client.render.*;
import com.minelife.drugs.tileentity.TileEntityCementMixer;
import com.minelife.drugs.tileentity.TileEntityLeafMulcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModelResourceLocation model = new ModelResourceLocation("minelife:coca_leaf", "inventory");
        ModelRegistryHelper.register(model, new ItemCocaLeafRenderer(() -> model));

//        registerItemRenderer(ModDrugs.itemLeafMulcherBlock, new ItemLeafMulcherRenderer());
//        registerBlockRenderer(TileEntityLeafMulcher.class, new TileEntityLeafMulcherRenderer());
//        registerItemRenderer(ModDrugs.itemCementMixerBlock, new ItemCementMixerRenderer());
//        registerBlockRenderer(TileEntityCementMixer.class, new TileEntityCementMixerRenderer());
    }

    @Override
    public void init(FMLInitializationEvent event) throws Exception {
        ModelLoader.setCustomStateMapper(ModDrugs.blockHempCrop, new HempStateMapper());
        registerRenderer(ModDrugs.itemHempSeed);
        registerRenderer(ModDrugs.itemCocaSeed);
        registerRenderer(ModDrugs.itemLimeSeed);
        registerRenderer(ModDrugs.itemCalciumHydroxide);
        registerRenderer(ModDrugs.itemCalciumOxide);
        registerRenderer(ModDrugs.itemHempBuds);
        registerRenderer(ModDrugs.itemHempShredded);
        registerRenderer(ModDrugs.itemCocaLeafShredded);
        registerRenderer(ModDrugs.itemCocaPaste);
        registerRenderer(ModDrugs.itemLime);
        registerRenderer(ModDrugs.itemPotassiumHydroxide);
        registerRenderer(ModDrugs.itemPotassiumManganate);
        registerRenderer(ModDrugs.itemPyrolusite);
        registerRenderer(ModDrugs.itemSalt);
        registerRenderer(ModDrugs.itemSulfur);
        registerRenderer(ModDrugs.itemPotassiumHydroxidePyrolusiteMixture);
        registerRenderer(ModDrugs.itemWaxyCocaine);
        registerRenderer(ModDrugs.itemHeatedCocaine);
        registerRenderer(ModDrugs.itemPressedCocaine);
        registerRenderer(ModDrugs.itemPurpleCocaine);
        registerRenderer(ModDrugs.itemCocaLeaf);
        registerRenderer(ModDrugs.itemJoint);
        registerRenderer(ModDrugs.itemProcessedCocaine);
        registerRenderer(ModDrugs.itemGrinder);
        ModDrugs.blockSulfurOre.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
        ModDrugs.blockPyrolusiteOre.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
        ModDrugs.blockPotash.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
        ModDrugs.blockLimestone.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
//        ModDrugs.blockVacuum.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
//        ModDrugs.blockPresser.registerModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
    }

    private void registerRenderer(Item item) {
        ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName().toString(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, model);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, model);
    }
}
