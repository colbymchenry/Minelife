package com.minelife.economy;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.PlayerHelper;
import com.minelife.SubMod;
import com.minelife.economy.packet.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ModEconomy extends SubMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityATM.class, "tileATM");
        GameRegistry.registerBlock(BlockATM.INSTANCE, BlockATM.NAME);
        GameRegistry.registerBlock(BlockATMTop.INSTANCE, BlockATMTop.NAME);

        registerPacket(PacketOpenATM.Handler.class, PacketOpenATM.class, Side.CLIENT);
        registerPacket(PacketVerifyPin.Handler.class, PacketVerifyPin.class, Side.SERVER);
        registerPacket(PacketUpdateATMGui.Handler.class, PacketUpdateATMGui.class, Side.CLIENT);
        registerPacket(PacketUnlockATM.Handler.class, PacketUnlockATM.class, Side.CLIENT);
        registerPacket(PacketSetPin.Handler.class, PacketSetPin.class, Side.SERVER);
        registerPacket(PacketBalanceQuery.Handler.class, PacketBalanceQuery.class, Side.SERVER);
        registerPacket(PacketBalanceResult.Handler.class, PacketBalanceResult.class, Side.CLIENT);
        registerPacket(PacketDeposit.Handler.class, PacketDeposit.class, Side.SERVER);
        registerPacket(PacketWithdraw.Handler.class, PacketWithdraw.class, Side.SERVER);
        registerPacket(PacketTransferMoney.Handler.class, PacketTransferMoney.class, Side.SERVER);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return com.minelife.economy.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.economy.server.ServerProxy.class;
    }

    public static final void deposit(UUID player, long amount, boolean wallet) throws Exception {
        if (!playerExists(player)) throw new Exception("Player not found.");

        long balance = getBalance(player, wallet);
        balance += amount;
        String column = wallet ? "balanceWallet" : "balanceBank";

        Minelife.SQLITE.query("UPDATE players SET " + column + "='" + balance + "'");

        // update client
        if (PlayerHelper.getPlayer(player) != null)
            Minelife.NETWORK.sendTo(new PacketBalanceResult(getBalance(player, false), getBalance(player, true)), PlayerHelper.getPlayer(player));
    }

    public static final void withdraw(UUID player, long amount, boolean wallet) throws Exception {
        if (!playerExists(player)) throw new Exception("Player not found.");

        long balance = getBalance(player, wallet);
        balance -= amount;

        /**
         * Make sure the player balance cannot go below zero (cannot go into debt)
         */
        if (balance < 0) throw new Exception("GuiBalance cannot be less than zero.");

        String column = wallet ? "balanceWallet" : "balanceBank";
        Minelife.SQLITE.query("UPDATE players SET " + column + "='" + balance + "'");

        // update client
        if (PlayerHelper.getPlayer(player) != null)
            Minelife.NETWORK.sendTo(new PacketBalanceResult(getBalance(player, false), getBalance(player, true)), PlayerHelper.getPlayer(player));
    }

    public static final long getBalance(UUID player, boolean wallet) throws Exception {
        if (!playerExists(player)) throw new Exception("Player not found.");

        String table = wallet ? "balanceWallet" : "balanceBank";
        ResultSet result = Minelife.SQLITE.query("SELECT " + table + " AS balance FROM players WHERE uuid='" + player.toString() + "'");
        if (result.next()) return result.getLong("balance");

        return 0;
    }

    public static final void setPin(UUID player, String pin) throws Exception {
        if (!playerExists(player)) throw new Exception("Player not found.");

        Minelife.SQLITE.query("UPDATE players SET pin='" + pin + "' WHERE uuid='" + player.toString() + "'");
    }

    public static final String getPin(UUID player) throws Exception {
        if (!playerExists(player)) throw new Exception("Player not found.");

        ResultSet result = Minelife.SQLITE.query("SELECT pin AS pin FROM players WHERE uuid='" + player.toString() + "'");
        if (result.next()) return result.getString("pin");

        return null;
    }

    public static final boolean playerExists(UUID player) {
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM players WHERE uuid='" + player.toString() + "'");
            if (result.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
