package com.minelife.chestshop;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.chestshop.network.PacketBuyFromShop;
import com.minelife.chestshop.network.PacketSaveShop;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModChestShop extends MLMod {

    public static BlockChestShop blockChestShop;
    public static ItemChestShop itemChestShop;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockChestShop = new BlockChestShop());
        registerItem(itemChestShop = new ItemChestShop(blockChestShop));
        registerTileEntity(TileEntityChestShop.class);
        registerPacket(PacketSaveShop.Handler.class, PacketSaveShop.class, Side.SERVER);
        registerPacket(PacketBuyFromShop.Handler.class, PacketBuyFromShop.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.chestshop.client.ClientProxy.class;
    }

}
