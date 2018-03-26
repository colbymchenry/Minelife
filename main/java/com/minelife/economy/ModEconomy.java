package com.minelife.economy;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.block.BlockATMBottom;
import com.minelife.economy.block.BlockATMTop;
import com.minelife.economy.block.BlockCash;
import com.minelife.economy.client.gui.wallet.InventoryWallet;
import com.minelife.economy.item.ItemATM;
import com.minelife.economy.item.ItemCash;
import com.minelife.economy.item.ItemCashBlock;
import com.minelife.economy.item.ItemWallet;
import com.minelife.economy.network.PacketOpenATM;
import com.minelife.economy.network.PacketSendMoneyATM;
import com.minelife.economy.network.PacketWithdrawATM;
import com.minelife.economy.server.CommandEconomy;
import com.minelife.economy.server.ServerProxy;
import com.minelife.economy.tileentity.TileEntityATM;
import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.util.NumberConversions;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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

        registerPacket(PacketOpenATM.Handler.class, PacketOpenATM.class, Side.CLIENT);
        registerPacket(PacketWithdrawATM.Handler.class, PacketWithdrawATM.class, Side.SERVER);
        registerPacket(PacketSendMoneyATM.Handler.class, PacketSendMoneyATM.class, Side.SERVER);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandEconomy());
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

    public static int depositCashPiles(UUID playerID, int amount) {
        for (TileEntityCash tileCash : TileEntityCash.getCashPiles(playerID)) {
            amount = tileCash.deposit(amount);
            tileCash.sendUpdates();
            if (amount <= 0) break;
        }
        return amount;
    }

    public static void depositATM(UUID playerID, long amount) {
        if (!NumberConversions.isInt(String.valueOf(getBalanceATM(playerID) + amount))) return;

        long balance = getBalanceATM(playerID) + amount;
        try {
            ResultSet result = getDatabase().query("SELECT * FROM atm WHERE player='" + playerID.toString() + "'");
            if (result.next()) {
                getDatabase().query("UPDATE atm SET balance='" + balance + "' WHERE player='" + playerID.toString() + "'");
            } else {
                getDatabase().query("INSERT INTO atm (player, balance) VALUES ('" + playerID.toString() + "', '" + balance + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int depositInventory(EntityPlayerMP player, int amount) {
        amount = ItemWallet.depositPlayer(player, amount);

        if (amount <= 0) return 0;

        Map<Integer, ItemStack> wallets = ItemWallet.getWallets(player);
        for (Integer integer : wallets.keySet()) {
            InventoryWallet inventoryWallet = new InventoryWallet(wallets.get(integer));
            amount = inventoryWallet.deposit(amount);
            inventoryWallet.writeToNBT();
            player.inventory.setInventorySlotContents(integer, inventoryWallet.getWalletStack());
            if (amount <= 0) break;
        }

        return amount;
    }

    public static int withdrawCashPiles(UUID playerID, int amount) {
        WithdrawlResult result = null;
        for (TileEntityCash tileCash : TileEntityCash.getCashPiles(playerID)) {
             result = tileCash.withdraw(amount);
            tileCash.sendUpdates();
            amount -= ItemCash.getAmount(result.stacksTaken);
            if (amount <= 0) break;
        }
        return result == null ? amount : result.changeThatDidNotFit;
    }

    public static void withdrawATM(UUID playerID, long amount) {
        long balance = getBalanceATM(playerID) - amount;
        balance = balance < 0 ? 0 : balance;
        try {
            ResultSet result = getDatabase().query("SELECT * FROM atm WHERE player='" + playerID.toString() + "'");
            if (result.next()) {
                getDatabase().query("UPDATE atm SET balance='" + balance + "' WHERE player='" + playerID.toString() + "'");
            } else {
                getDatabase().query("INSERT INTO atm (player, balance) VALUES ('" + playerID.toString() + "', '" + balance + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int withdrawInventory(EntityPlayerMP player, int amount) {
        WithdrawlResult result = ItemWallet.withdrawPlayer(player, amount);
        amount -= ItemCash.getAmount(result.stacksTaken);

        if (amount <= 0) return result.changeThatDidNotFit;

        Map<Integer, ItemStack> wallets = ItemWallet.getWallets(player);
        for (Integer integer : wallets.keySet()) {
            InventoryWallet inventoryWallet = new InventoryWallet(wallets.get(integer));
            result = inventoryWallet.withdraw(amount);
            amount -= ItemCash.getAmount(result.stacksTaken);
            if (amount <= 0) {
                inventoryWallet.writeToNBT();
                player.inventory.setInventorySlotContents(integer, inventoryWallet.getWalletStack());
                break;
            } else {
                inventoryWallet.writeToNBT();
                player.inventory.setInventorySlotContents(integer, inventoryWallet.getWalletStack());
            }
        }

        return result == null ? amount : result.changeThatDidNotFit;
    }

    public static long getBalanceCashPiles(UUID playerID) {
        List<TileEntityCash> list = TileEntityCash.getCashPiles(playerID);
        long total = 0;
        for (TileEntityCash tileEntityCash : list) total += tileEntityCash.getBalance();
        return total;
    }

    public static long getBalanceATM(UUID playerID) {
        try {
            ResultSet result = getDatabase().query("SELECT * FROM atm WHERE player='" + playerID.toString() + "'");
            if (result.next()) return result.getLong("balance");
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

    public static int deposit(InventoryRange inventoryRange, int amount) {
        int hundreds = amount / 100;
        if (hundreds > 0) {
            amount -= 100 * hundreds;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, hundreds, 5), false);
            amount += 100 * didNotFit;
        }

        int fifties = amount / 50;
        if (fifties > 0) {
            amount -= 50 * fifties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fifties, 4), false);
            amount += 50 * didNotFit;
        }

        int twenties = amount / 20;
        if (twenties > 0) {
            amount -= 20 * twenties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, twenties, 3), false);
            amount += 20 * didNotFit;
        }

        int tens = amount / 10;
        if (tens > 0) {
            amount -= 10 * tens;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, tens, 2), false);
            amount += 10 * didNotFit;
        }

        int fives = amount / 5;
        if (fives > 0) {
            amount -= 5 * fives;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fives, 1), false);
            amount += 5 * didNotFit;
        }

        int ones = (amount);
        if (ones > 0) {
            amount -= ones;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, ones, 0), false);
            amount += didNotFit;
        }

        return amount;
    }

    public static WithdrawlResult withdraw(InventoryRange inventory, int amount) {
        List<ItemStack> cashItems = Lists.newArrayList();
        List<Integer> emptySlots = Lists.newArrayList();


        for (int i = 0; i < inventory.inv.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.inv.getStackInSlot(i);
            if (itemStack.getItem() == ModEconomy.itemCash) {
                amount -= ItemCash.getAmount(itemStack);
                emptySlots.add(i);

                if (amount < 1) {
                    cashItems.addAll(ItemCash.getStacks(Math.abs(amount)));
                    break;
                } else {
                    cashItems.add(itemStack);
                }
            }
        }

        for (Integer emptySlot : emptySlots) inventory.inv.setInventorySlotContents(emptySlot, ItemStack.EMPTY);

        int changeDidNotFit = 0;

        if (amount < 0) {
            int addBack = Math.abs(amount);
            changeDidNotFit = deposit(inventory, addBack);
        }

        return new WithdrawlResult(cashItems, changeDidNotFit);
    }

}
