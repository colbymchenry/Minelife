package com.minelife.permission;

import com.minelife.CommonProxy;
import com.minelife.AbstractMod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.UUID;

public class ModPermission extends AbstractMod {

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPermission());
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass() {
        return com.minelife.permission.ServerProxy.class;
    }

    @SideOnly(Side.SERVER)
    public static Player get(UUID player) {
        return new Player(player);
    }

    @SideOnly(Side.SERVER)
    public static Group get(String name) throws Exception {
        return new Group(name);
    }

}
