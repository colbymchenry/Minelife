package com.minelife.economy;

import com.minelife.*;
import com.minelife.economy.client.gui.GuiBillPay;
import com.minelife.economy.server.CommandEconomy;
import com.minelife.util.MLConfig;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.economy.packet.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ModEconomy extends MLMod {

    @SideOnly(Side.SERVER)
    public static MLConfig config;

    public static int BALANCE_BANK_CLIENT = 0;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerTileEntity(TileEntityATM.class, "tileATM");

        registerPacket(PacketUpdateATMGui.Handler.class, PacketUpdateATMGui.class, Side.CLIENT);
        registerPacket(PacketUnlockATM.Handler.class, PacketUnlockATM.class, Side.CLIENT);
        registerPacket(PacketBalanceQuery.Handler.class, PacketBalanceQuery.class, Side.SERVER);
        registerPacket(PacketBalanceResult.Handler.class, PacketBalanceResult.class, Side.CLIENT);
        registerPacket(PacketDeposit.Handler.class, PacketDeposit.class, Side.SERVER);
        registerPacket(PacketWithdraw.Handler.class, PacketWithdraw.class, Side.SERVER);
        registerPacket(PacketTransferMoney.Handler.class, PacketTransferMoney.class, Side.SERVER);
        registerPacket(PacketRequestBills.Handler.class, PacketRequestBills.class, Side.SERVER);
        registerPacket(PacketResponseBills.Handler.class, PacketResponseBills.class, Side.CLIENT);
        registerPacket(Billing.PacketModifyBill.Handler.class, Billing.PacketModifyBill.class, Side.SERVER);
        registerPacket(Billing.PacketPayBill.Handler.class, Billing.PacketPayBill.class, Side.SERVER);

        ItemWallet.registerRecipes();

        GameRegistry.addShapedRecipe(new ItemStack(MLBlocks.cash), "SSS", 'S', Items.stick);
    }

    @Override
    public AbstractGuiHandler gui_handler() {
        return new GuiHandler();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandEconomy());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass()
    {
        return com.minelife.economy.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass()
    {
        return com.minelife.economy.server.ServerProxy.class;
    }

    public static String getMessage(String key)
    {
        return config.getString(key);
    }

    public static boolean handleInput(String text, boolean isFocused, char key_char, int key_id) {
        return key_id == Keyboard.KEY_BACK || isFocused && NumberConversions.isInt(text + key_char);
    }

}
