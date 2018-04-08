package com.minelife.cape.client;

import com.minelife.MLProxy;
import com.minelife.cape.ModCapes;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        registerItemRenderer(ModCapes.itemCape, new ItemCapeRenderer());
    }

}
