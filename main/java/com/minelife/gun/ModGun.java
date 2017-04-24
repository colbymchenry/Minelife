package com.minelife.gun;

import com.google.common.collect.Maps;
import com.minelife.util.ClassFinder;
import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class ModGun extends SubMod {

    public static final Map<Class<? extends ItemAmmo>, ItemAmmo> ammoMap = Maps.newHashMap();
    public static final Map<Class<? extends ItemGun>, ItemGun> gunMap = Maps.newHashMap();

    public static final CreativeTabs tabGuns = new CreativeTabs("guns") {
        @Override
        public Item getTabIconItem() {
            return Items.diamond;
        }
    };

    public static final CreativeTabs tabAmmo = new CreativeTabs("ammo") {
        @Override
        public Item getTabIconItem() {
            return Items.emerald;
        }
    };

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerGuns(event);
        registerAmmo();

        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return com.minelife.gun.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.gun.server.ServerProxy.class;
    }

    private void registerGuns(FMLPreInitializationEvent event) {
        List<Class<?>> classes = ClassFinder.find(this.getClass().getPackage().getName() + ".gun");

        for (Class<?> clazz : classes) {
            if(clazz.getSuperclass() != null && clazz.getSuperclass() == ItemGun.class) {
                try {
                    ItemGun itemGun = (ItemGun) clazz.getDeclaredConstructor(FMLPreInitializationEvent.class).newInstance(event);
                    gunMap.put(itemGun.getClass(), itemGun);
                    GameRegistry.registerItem(itemGun, itemGun.getClass().getSimpleName());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void registerAmmo() {
        List<Class<?>> classes = ClassFinder.find(this.getClass().getPackage().getName() + ".ammo");

        for (Class<?> clazz : classes) {
            if(clazz.getSuperclass() != null && clazz.getSuperclass() == ItemAmmo.class) {
                try {
                    ItemAmmo itemAmmo =(ItemAmmo) clazz.newInstance();
                    ammoMap.put(itemAmmo.getClass(), itemAmmo);
                    GameRegistry.registerItem(itemAmmo, itemAmmo.getClass().getSimpleName());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
