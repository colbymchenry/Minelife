package com.minelife.guns.server;

import com.minelife.MLProxy;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.permission.ModPermission;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ListIterator;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        ListIterator<Bullet> bulletIterator = Bullet.BULLETS.listIterator();
        while (bulletIterator.hasNext()) {
            Bullet.HitResult hitResult = bulletIterator.next().tick(0, false);
            if (hitResult.isTooFar() || hitResult.getBlockState() != null || hitResult.getEntity() != null)
                bulletIterator.remove();
        }
    }

    static int tick = 0;

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        tick++;

        if (tick < 60) return;

        tick = 0;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

        ItemStack gunStack = player.getHeldItemMainhand();
        EnumGun gunType = EnumGun.values()[gunStack.getMetadata()];
        if (gunType.defaultSkin != null) {
            if (!ModPermission.hasPermission(player.getUniqueID(), "gun.skin." + EnumGun.values()[gunStack.getMetadata()].name().toLowerCase())) {
                gunStack.setItemDamage(gunType.defaultSkin.ordinal());
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }
}
