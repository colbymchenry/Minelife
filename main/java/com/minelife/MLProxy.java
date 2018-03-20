package com.minelife;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.item.IItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MLProxy {

    public void preInit(FMLPreInitializationEvent event) throws Exception {}

    public void init(FMLInitializationEvent event) throws Exception {}

    @SideOnly(Side.CLIENT)
    public void registerItemRenderer(Item item, int meta, String location, IItemRenderer renderer) {
        ModelResourceLocation l = new ModelResourceLocation(location);
        ModelLoader.setCustomModelResourceLocation(item, meta, l);
        ModelRegistryHelper.register(l, renderer);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockRenderer(Class<? extends TileEntity> tileEntity, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, renderer);
    }

}
