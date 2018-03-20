package com.minelife.economy;

import com.google.common.collect.Sets;
import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.block.BlockATMBottom;
import com.minelife.economy.block.BlockATMTop;
import com.minelife.economy.block.BlockCash;
import com.minelife.economy.item.ItemATM;
import com.minelife.economy.item.ItemCash;
import com.minelife.economy.item.ItemCashBlock;
import com.minelife.economy.item.ItemWallet;
import com.minelife.economy.server.ServerProxy;
import com.minelife.economy.tileentity.TileEntityATM;
import com.minelife.economy.tileentity.TileEntityCash;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.sql.ResultSet;
import java.util.Set;
import java.util.UUID;

public class ModEconomy extends MLMod {

    public static BlockATMTop blockATMTop;
    public static BlockATMBottom blockATMBottom;
    public static ItemATM itemATM;
    public static BlockCash blockCash;
    public static ItemCash itemCash;
    public static ItemCashBlock itemCashBlock;
    public static ItemWallet itemWallet;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockATMTop = new BlockATMTop());
        registerBlock(blockATMBottom = new BlockATMBottom());
        registerBlock(blockCash = new BlockCash());
        registerItem(itemATM = new ItemATM(blockATMBottom));
        registerItem(itemCash = new ItemCash());
        registerItem(itemCashBlock = new ItemCashBlock(blockCash));
        registerItem(itemWallet = new ItemWallet());
        registerTileEntity(TileEntityATM.class);
        registerTileEntity(TileEntityCash.class);
        ResourceLocation name  = new ResourceLocation(Minelife.MOD_ID + ":cashBlock");
        ResourceLocation group = null;
        GameRegistry.addShapedRecipe(name, group, new ItemStack(blockCash), "###", '#', new ItemStack(Item.getItemFromBlock(Blocks.WOODEN_PRESSURE_PLATE)));
        itemWallet.registerRecipes();
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.economy.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.economy.server.ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler getGuiHandler() {
        return new GuiHandler();
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

    public static Set<Bill> getBills(UUID player) throws Exception {
        Set<Bill> bills = Sets.newTreeSet();
        ResultSet result = getDatabase().query("SELECT * FROM bills WHERE player='" + player.toString() + "'");
        while (result.next()) bills.add(new Bill(UUID.fromString(result.getString("uuid"))));
        return bills;
    }

    // TODO: Getting cash blocks in players estates

}
