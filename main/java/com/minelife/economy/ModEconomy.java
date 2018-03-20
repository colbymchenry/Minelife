package com.minelife.economy;

import com.google.common.collect.Lists;
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
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":cashBlock");
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
    public static int depositCashPiles(UUID playerID, int amount) {

    }

    public static int depositATM(UUID playerID, int amount) {

    }

    public static int depositInventory(EntityPlayerMP player, int amount) {

    }

    public static void withdrawCashPiles(UUID playerID, int amount) {

    }

    public static void withdrawATM(UUID playerID, int amount) {

    }

    public static void withdrawInventory(EntityPlayerMP player, int amount) {

    }

    public static int getBalanceCashPiles(UUID playerID) {
        List<TileEntityCash> list = TileEntityCash.getCashPiles(playerID);
        int total = 0;
        for (TileEntityCash tileEntityCash : list) total += tileEntityCash.getBalance();
        return total;
    }

    public static int getBalanceATM(UUID playerID) {
        try {
            ResultSet result = getDatabase().query("SELECT * FROM atm WHERE player='" + playerID.toString() + "'");
            if (result.next()) return result.getInt("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getBalanceInventory(EntityPlayerMP player) {
        int total = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getItem() == itemCash)
                total += ItemCash.getAmount(stack);
            else if (stack.getItem() == itemWallet)
                total += ItemWallet.getHoldings(stack);
        }
        return total;
    }

}
