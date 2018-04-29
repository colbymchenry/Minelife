package com.minelife.airdrop;

import blusunrize.immersiveengineering.common.IEContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.cape.ModCapes;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.locks.ModLocks;
import com.minelife.util.MLConfig;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class ModAirdrop extends MLMod {

    public static volatile List<Airdrop> airdrops = Lists.newArrayList();
    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketAirdrop.Handler.class, PacketAirdrop.class, Side.CLIENT);
        registerPacket(PacketRemoveAirdrop.Handler.class, PacketRemoveAirdrop.class, Side.CLIENT);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.SERVER) {
            List<Loot> defaultLoot = Lists.newArrayList();
            defaultLoot.add(new Loot(new ItemStack(Items.DIAMOND, 16), 16));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.M4A4.ordinal()), 140));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.AK47.ordinal()), 140));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.DESERT_EAGLE.ordinal()), 72));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.MAGNUM.ordinal()), 72));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.AWP.ordinal()), 180));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemGun, 1, EnumGun.BARRETT.ordinal()), 180));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemBullet, 64, 2), 2));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemDynamite, 32), 16));
            defaultLoot.add(new Loot(new ItemStack(ModCapes.itemCape, 2), 100));
            defaultLoot.add(new Loot(new ItemStack(ModLocks.itemLock, 2, 0), 16));
            defaultLoot.add(new Loot(new ItemStack(ModLocks.itemLock, 2, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(ModLocks.itemLock, 2, 2), 120));
            defaultLoot.add(new Loot(new ItemStack(ModLocks.itemLock, 2, 3), 120));
            defaultLoot.add(new Loot(new ItemStack(ModLocks.itemLock, 2, 4), 120));
            defaultLoot.add(new Loot(new ItemStack(ModGuns.itemTurret, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.EMERALD, 16), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.DIAMOND_SWORD, 1), 48));
            defaultLoot.add(new Loot(new ItemStack(Items.IRON_SWORD, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.CHAINMAIL_HELMET, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.CHAINMAIL_CHESTPLATE, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.CHAINMAIL_LEGGINGS, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.CHAINMAIL_BOOTS, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.IRON_HELMET, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.IRON_CHESTPLATE, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.IRON_LEGGINGS, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.IRON_BOOTS, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(Items.EMERALD, 16), 32));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemRevolver, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemRailgun, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemShield, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 0), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 1), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 2), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 3), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 4), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 5), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 6), 16));
            defaultLoot.add(new Loot(new ItemStack(IEContent.itemMetal, 32, 7), 16));

            List<String> defaultLootList = Lists.newArrayList();
            defaultLoot.forEach(loot -> defaultLootList.add(loot.toString()));

            defaultLootList.forEach(loot -> {
                System.out.println(loot);
            });

            try {
                config = new MLConfig("airdrops");
                config.addDefault("Weight", 200);
                config.addDefault("DropDurationMinutes", 20);
                config.addDefault("DropRadius", 5000);
                config.addDefault("DropCenter.x", 0);
                config.addDefault("DropCenter.y", 0);
                config.addDefault("DropCenter.z", 0);
                config.addDefault("loot", defaultLootList);
                config.save();
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.airdrop.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.airdrop.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAirdrop());
    }

    public static List<Loot> getLoot() {
        List<Loot> lootList = Lists.newArrayList();
        for (String loot : config.getStringList("loot")) {
            lootList.add(Loot.fromString(loot));
        }
        return lootList;
    }

}
