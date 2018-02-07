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
        registerPacket(GuiBillPay.PacketRequestBills.Handler.class, GuiBillPay.PacketRequestBills.class, Side.SERVER);
        registerPacket(GuiBillPay.PacketResponseBills.Handler.class, GuiBillPay.PacketResponseBills.class, Side.CLIENT);
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

    public static final String getMessage(String key)
    {
        return config.getString(key);
    }

    public static boolean isValidAmount(String str) {
        if(!NumberConversions.isDouble(str)) return false;
        String[] data = String.valueOf(str + ".").split("\\.");
        if(data[0].startsWith("0") && data[0].length() > 1) return false;
        if(data.length > 1 && data[1].length() > 2) return false;
        return true;
    }

    public static boolean handleInput(String text, boolean isFocused, char key_char, int key_id) {
        if (NumberConversions.isInt(String.valueOf(key_char)) || key_id == Keyboard.KEY_BACK) {
            if (isFocused && NumberConversions.isDouble(text + key_char)) {
                if (key_id == Keyboard.KEY_BACK || ModEconomy.isValidAmount(text + key_char))
                    return true;
            }
        }

        return false;
    }

}
