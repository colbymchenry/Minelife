package com.minelife.airdrop;

import blusunrize.immersiveengineering.common.IEContent;
import com.google.common.collect.Lists;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.locks.ModLocks;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.List;

public class ModAirdrop extends MLMod {

    public static volatile List<Airdrop> airdrops = Lists.newArrayList();
    public static MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "bandit"), EntityBandit.class, "bandit", 8, Minelife.getInstance(), 77, 1, true, 0x424242, 0xf44242);
        registerPacket(PacketAirdrop.Handler.class, PacketAirdrop.class, Side.CLIENT);
        registerPacket(PacketRemoveAirdrop.Handler.class, PacketRemoveAirdrop.class, Side.CLIENT);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.SERVER) {
            List<ItemStackDropable> defaultLoot = Lists.newArrayList();
            defaultLoot.add(new ItemStackDropable(new ItemStack(Items.DIAMOND), 5, false, false, true, 2, 16));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.M4A4.ordinal()), 2.2, true, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.AK47.ordinal()), 2, true, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.DESERT_EAGLE.ordinal()), 4, false, false, true, 1, 3));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.MAGNUM.ordinal()), 3, false, false, true, 1, 3));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.AWP.ordinal()), 0.5, true, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemGun, 1, EnumGun.BARRETT.ordinal()), 1, true, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(ItemGun.getAmmo(64), 40, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemDynamite, 32), 5, false, false, true, 2, 16));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModCapes.itemCape, 2), 0.56, true, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModLocks.itemLock, 2, 0), 10, false, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModLocks.itemLock, 2, 1), 6, false, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModLocks.itemLock, 2, 2), 4, false, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModLocks.itemLock, 2, 3), 2, false, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModLocks.itemLock, 2, 4), 1, false, false, true, 1, 1));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.itemTurret, 2), 8, false, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(Items.EMERALD, 16), 5, false, false, true, 2, 16));
            defaultLoot.add(new ItemStackDropable(new ItemStack(Items.DIAMOND_SWORD, 1), 10, false, false, true, 1, 3));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemShield, 1), 5, false, false, true, 1, 2));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 0), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 1), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 2), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 3), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 4), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 5), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 6), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(IEContent.itemMetal, 64, 7), 60, false, false, true, 16, 64));
            defaultLoot.add(new ItemStackDropable(new ItemStack(ModGuns.gunSkinUnlocker, 1), 5, true, false, true, 1, 3));

            List<String> defaultLootList = Lists.newArrayList();
            defaultLoot.forEach(loot -> defaultLootList.add(loot.toString()));

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

    public static List<ItemStackDropable> getLootList() {
        List<ItemStackDropable> lootList = Lists.newArrayList();
        if(!config.contains("loot")) return lootList;
        config.getStringList("loot").forEach(loot -> lootList.add(ItemStackDropable.fromString(loot)));
        return lootList;
    }

}
