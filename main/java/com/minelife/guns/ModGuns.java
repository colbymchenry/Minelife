package com.minelife.guns;

import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.guns.block.BlockZincOre;
import com.minelife.guns.item.*;
import com.minelife.guns.packet.*;
import com.minelife.guns.turret.BlockTurret;
import com.minelife.guns.turret.PacketSetTurretSettings;
import com.minelife.guns.turret.TileEntityTurret;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModGuns extends MLMod {

    public static ItemGun itemGun;
//    public static ItemAmmo itemAmmo;
    public static ItemAttachment itemAttachment;
    public static BlockZincOre blockZincOre;
    public static BlockTurret blockTurretBottom, blockTurretTop;
    public static ItemZincOre itemZincOre;
    public static ItemZincIngot itemZincIngot;
//    public static ItemZincPlate itemZincPlate;
    public static ItemGunmetal itemGunmetal;
    public static ItemGunPart itemGunPart;
    public static ItemBlock itemTurret;
    public static ItemDynamite itemDynamite;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockTurretBottom = new BlockTurret(true));
        registerBlock(blockTurretTop = new BlockTurret(false));
        registerTileEntity(TileEntityTurret.class, "turret");
        registerItem(itemTurret = (ItemBlock) new ItemBlock(blockTurretBottom).setRegistryName(Minelife.MOD_ID, "turret_true")
                .setUnlocalizedName(Minelife.MOD_ID + ":turret_true").setCreativeTab(CreativeTabs.MISC));

        registerItem(itemGun = new ItemGun());
//        registerItem(itemAmmo = new ItemAmmo());
        registerItem(itemAttachment = new ItemAttachment());
        registerBlock(blockZincOre = new BlockZincOre());
        registerItem(itemZincOre = new ItemZincOre(blockZincOre));
        registerItem(itemZincIngot = new ItemZincIngot());
//        registerItem(itemZincPlate = new ItemZincPlate());
        registerItem(itemGunmetal = new ItemGunmetal());
        registerItem(itemGunPart = new ItemGunPart());
        registerItem(itemDynamite = new ItemDynamite());
        MinecraftForge.EVENT_BUS.register(this);

        registerPacket(PacketFire.Handler.class, PacketFire.class, Side.SERVER);
        registerPacket(PacketBullet.Handler.class, PacketBullet.class, Side.CLIENT);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
        registerPacket(PacketAttachment.Handler.class, PacketAttachment.class, Side.SERVER);
        registerPacket(PacketChangeSkin.Handler.class, PacketChangeSkin.class, Side.SERVER);
        registerPacket(PacketOpenModifyGUI.Handler.class, PacketOpenModifyGUI.class, Side.CLIENT);
        registerPacket(PacketRequestModifyGUI.Handler.class, PacketRequestModifyGUI.class, Side.SERVER);
        registerPacket(PacketChangeSkinResponse.Handler.class, PacketChangeSkinResponse.class, Side.CLIENT);

        GameRegistry.registerWorldGenerator(new BlockZincOre.Generator(), 0);
        itemZincIngot.registerSmeltingRecipe();
//        itemZincPlate.registerRecipe();
        itemGunmetal.registerRecipe();
        itemGunPart.registerRecipes();
        itemGun.registerRecipes();
        itemAttachment.registerRecipes();
        itemDynamite.registerRecipe();
//        itemAmmo.registerRecipes();

        registerPacket(PacketSetTurretSettings.Handler.class, PacketSetTurretSettings.class, Side.SERVER);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        blockTurretBottom.registerRecipe();
    }

    @Override
    public void entityRegistration(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(EntityDynamite.class)
                        .id(new ResourceLocation("minecraft", "dynamite"), 76)
                        .name("Dynamite")
                        .tracker(64, 20, false)
                        .build()
        );
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
