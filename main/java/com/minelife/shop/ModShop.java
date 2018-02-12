package com.minelife.shop;

import com.minelife.MLBlocks;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.shop.network.PacketBuyFromShop;
import com.minelife.shop.network.PacketSetShopBlock;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.ComputerCraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ModShop extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityShopBlock.class, "tileShopBlock");
        registerPacket(PacketSetShopBlock.Handler.class, PacketSetShopBlock.class, Side.SERVER);
        registerPacket(PacketBuyFromShop.Handler.class, PacketBuyFromShop.class, Side.SERVER);

        GameRegistry.addShapedRecipe(new ItemStack(MLBlocks.shopBlock), "AAA", "ABA", "AAA", 'A', Blocks.iron_block, 'B', ComputerCraft.Blocks.computer);
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.shop.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.shop.client.ClientProxy.class;
    }
}
