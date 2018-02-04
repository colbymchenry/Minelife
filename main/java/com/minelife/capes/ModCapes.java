package com.minelife.capes;

import com.minelife.MLItems;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.capes.network.*;
import com.minelife.capes.server.CommandCape;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

public class ModCapes extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketCreateGui.Handler.class, PacketCreateGui.class, Side.CLIENT);
        registerPacket(PacketCreateCape.Handler.class, PacketCreateCape.class, Side.SERVER);
        registerPacket(PacketUpdateCape.Handler.class, PacketUpdateCape.class, Side.CLIENT);
        registerPacket(PacketUpdateCapeStatus.Handler.class, PacketUpdateCapeStatus.class, Side.CLIENT);
        registerPacket(PacketEditCape.Handler.class, PacketEditCape.class, Side.SERVER);
        registerPacket(PacketEditGui.Handler.class, PacketEditGui.class, Side.CLIENT);
        registerPacket(PacketRemoveCapeItemTexture.Handler.class, PacketRemoveCapeItemTexture.class, Side.CLIENT);
        registerRecipe();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCape());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.capes.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.capes.server.ServerProxy.class;
    }

    private static void registerRecipe() {
        GameRegistry.addRecipe(new CapeRecipe(new ItemStack(MLItems.cape), "WW", "WW", "WW", 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new CapeRecipe(new ItemStack(MLItems.cape), "AWW", " WW", " WW", 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 'A', MLItems.cape));
        GameRegistry.addRecipe(new CapeRecipe(new ItemStack(MLItems.cape), " WW", "AWW", " WW", 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 'A', MLItems.cape));
        GameRegistry.addRecipe(new CapeRecipe(new ItemStack(MLItems.cape), " WW", " WW", "AWW", 'W', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 'A', MLItems.cape));
        GameRegistry.addRecipe(new CapeRecipe(new ItemStack(MLItems.cape), "AA", 'A', MLItems.cape));
    }

}
