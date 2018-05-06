package com.minelife.police;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.network.PacketUnconscious;
import com.minelife.police.server.CommandPrison;
import com.minelife.police.server.ServerProxy;
import com.minelife.util.MLConfig;
import com.minelife.util.PacketPlaySound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModPolice extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "cop"), EntityCop.class, "cop", 7, Minelife.getInstance(), 77, 1, true, 0xFFFFFF, 0x4286f4);
        registerPacket(PacketUnconscious.Handler.class, PacketUnconscious.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.police.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPrison());
    }

    public static MLConfig getConfig() {
        return ServerProxy.config;
    }

    public static void setUnconscious(EntityPlayer entity, boolean value) {
        Minelife.getNetwork().sendToAll(new PacketUnconscious(entity.getEntityId(), value));

        if(value) {
            entity.getEntityData().setLong("UnconsciousTime", System.currentTimeMillis() + (60000L * 3));
            entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60 * 20, 10, false, false));
        } else {
            entity.getEntityData().removeTag("UnconsciousTime");
            entity.getEntityData().removeTag("Tazed");
            entity.removePotionEffect(MobEffects.SLOWNESS);
        }
    }

    public static boolean isUnconscious(EntityPlayer player) {
        return player.getEntityData().hasKey("UnconsciousTime") && player.getEntityData().getLong("UnconsciousTime") > System.currentTimeMillis();
    }

}
