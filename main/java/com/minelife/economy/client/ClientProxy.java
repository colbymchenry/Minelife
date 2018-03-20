package com.minelife.economy.client;

import com.minelife.MLProxy;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.client.render.*;
import com.minelife.economy.tileentity.TileEntityATM;
import com.minelife.economy.tileentity.TileEntityCash;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItemRenderer(ModEconomy.itemATM, 0, "minelife:atm", new RenderATMItem());
        registerItemRenderer(ModEconomy.itemCashBlock, 0, "minelife:cashBlock", new RenderCashItem());
        registerBlockRenderer(TileEntityATM.class, new RenderATMBlock());
        registerBlockRenderer(TileEntityCash.class, new RenderCashBlock());
        ModEconomy.itemCash.registerModels();
        ModEconomy.itemWallet.registerModels();
    }
}
