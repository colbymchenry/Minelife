package com.minelife.guns;

import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.guns.block.BlockZincOre;
import com.minelife.guns.item.*;
import com.minelife.guns.packet.PacketAttachment;
import com.minelife.guns.packet.PacketBullet;
import com.minelife.guns.packet.PacketFire;
import com.minelife.guns.packet.PacketReload;
import com.minelife.guns.turret.BlockTurret;
import com.minelife.guns.turret.PacketSetTurretSettings;
import com.minelife.guns.turret.TileEntityTurret;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModGuns extends MLMod {

    public static ItemGun itemGun;
    public static ItemAmmo itemAmmo;
    public static ItemAttachment itemAttachment;
    public static BlockZincOre blockZincOre;
    public static BlockTurret blockTurretBottom, blockTurretTop;
    public static ItemZincOre itemZincOre;
    public static ItemZincIngot itemZincIngot;
    public static ItemZincPlate itemZincPlate;
    public static ItemGunmetal itemGunmetal;
    public static ItemGunPart itemGunPart;
    public static ItemBlock itemTurret;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockTurretBottom = new BlockTurret(true));
        registerBlock(blockTurretTop = new BlockTurret(false));
        registerTileEntity(TileEntityTurret.class, "turret");
        registerItem(itemTurret = (ItemBlock) new ItemBlock(blockTurretBottom).setRegistryName(Minelife.MOD_ID, "turret_true")
                .setUnlocalizedName(Minelife.MOD_ID + ":turret_true").setCreativeTab(CreativeTabs.MISC));

        registerItem(itemGun = new ItemGun());
        registerItem(itemAmmo = new ItemAmmo());
        registerItem(itemAttachment = new ItemAttachment());
        registerBlock(blockZincOre = new BlockZincOre());
        registerItem(itemZincOre = new ItemZincOre(blockZincOre));
        registerItem(itemZincIngot = new ItemZincIngot());
        registerItem(itemZincPlate = new ItemZincPlate());
        registerItem(itemGunmetal = new ItemGunmetal());
        registerItem(itemGunPart = new ItemGunPart());
        MinecraftForge.EVENT_BUS.register(this);
        registerPacket(PacketFire.Handler.class, PacketFire.class, Side.SERVER);
        registerPacket(PacketBullet.Handler.class, PacketBullet.class, Side.CLIENT);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
        registerPacket(PacketAttachment.Handler.class, PacketAttachment.class, Side.SERVER);
        GameRegistry.registerWorldGenerator(new BlockZincOre.Generator(), 0);
        itemZincIngot.registerSmeltingRecipe();
        itemZincPlate.registerRecipe();
        itemGunmetal.registerRecipe();
        itemGunPart.registerRecipes();
        itemGun.registerRecipes();
        itemAttachment.registerRecipes();
        itemAmmo.registerRecipes();

        registerPacket(PacketSetTurretSettings.Handler.class, PacketSetTurretSettings.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.guns.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.guns.server.ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler getGuiHandler() {
        return new GuiHandler();
    }
}
