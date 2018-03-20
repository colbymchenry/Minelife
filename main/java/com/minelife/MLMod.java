package com.minelife;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public abstract class MLMod {

    @SideOnly(Side.SERVER)
    public MLProxy serverProxy;

    @SideOnly(Side.CLIENT)
    public MLProxy clientProxy;

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
    }

    public void serverStarting(FMLServerStartingEvent event) {
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public Class<? extends MLProxy> getClientProxyClass() {
        return null;
    }

    public Class<? extends MLProxy> getServerProxyClass() {
        return null;
    }

    public AbstractGuiHandler getGuiHandler() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event) {

    }

    private static int PACKET_ID = 0;
    private static Map<Integer, Class> gui_map = Maps.newHashMap();
    private static Map<Integer, Class> container_map = Maps.newHashMap();

    public static final void registerPacket(Class messageHandler, Class message, Side receivingSide) {
        Minelife.getNetwork().registerMessage(messageHandler, message, PACKET_ID++, receivingSide);
    }

    protected void registerItem(Item item) {
        ForgeRegistries.ITEMS.register(item);
    }

    protected void registerBlock(Block block) {
        ForgeRegistries.BLOCKS.register(block);
    }

    protected void registerRecipe(IRecipe recipe) {
        ForgeRegistries.RECIPES.register(recipe);
    }

    protected void registerPotion(Potion potion) {
        ForgeRegistries.POTIONS.register(potion);
    }

    protected void registerTileEntity(Class<? extends TileEntity> tile) {
        registerTileEntity(tile, Minelife.MOD_ID + ":" + tile.getSimpleName());
    }

    protected void registerTileEntity(Class<? extends TileEntity> tile, String identifier) {
        GameRegistry.registerTileEntity(tile, identifier);
    }

}
