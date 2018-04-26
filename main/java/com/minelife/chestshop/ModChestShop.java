package com.minelife.chestshop;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.chestshop.network.PacketBuyFromShop;
import com.minelife.chestshop.network.PacketSaveShop;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModChestShop extends MLMod {

    public static BlockChestShop blockChestShop;
    public static ItemChestShop itemChestShop;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockChestShop = new BlockChestShop());
        registerItem(itemChestShop = new ItemChestShop(blockChestShop));
        registerTileEntity(TileEntityChestShop.class);
        registerPacket(PacketSaveShop.Handler.class, PacketSaveShop.class, Side.SERVER);
        registerPacket(PacketBuyFromShop.Handler.class, PacketBuyFromShop.class, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(this);

        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":chest_shop");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemChestShop),
                "AAA",
                "AGA",
                "ACA",
                'A', Ingredient.fromStacks(new ItemStack(IEContent.itemMetal, 1, 31)),
                'C', Ingredient.fromStacks(new ItemStack(IEContent.itemMaterial, 1, 27)),
                'G', Ingredient.fromStacks(new ItemStack(Item.getItemFromBlock(Blocks.GLASS))));
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.chestshop.client.ClientProxy.class;
    }

    @SubscribeEvent
    public void placeBlock(BlockEvent.PlaceEvent event) {
        if(event.getPlacedAgainst().getBlock() == ModChestShop.blockChestShop) {
            event.setCanceled(true);
        }
    }

}
