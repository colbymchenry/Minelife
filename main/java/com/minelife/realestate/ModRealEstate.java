package com.minelife.realestate;

import com.minelife.CommonProxy;
import com.minelife.AbstractMod;
import com.minelife.realestate.client.GuiZoneInfo;
import com.minelife.realestate.client.GuiZoneMembers;
import com.minelife.realestate.client.GuiZoneSell;
import com.minelife.realestate.server.CommandClaim;
import com.minelife.realestate.sign.BlockForSaleSign;
import com.minelife.realestate.sign.ItemForSaleSign;
import com.minelife.realestate.sign.TileEntityForSaleSign;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModRealEstate extends AbstractMod {

    @SideOnly(Side.SERVER)
    public static SimpleConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(SelectionController.Selector.getInstance(), "Selector");
        registerPacket(SelectionController.PacketSelection.Handler.class, SelectionController.PacketSelection.class, Side.CLIENT);
        registerPacket(ZoneInfoController.PacketRequestZoneInfo.Handler.class, ZoneInfoController.PacketRequestZoneInfo.class, Side.SERVER);
        registerPacket(ZoneInfoController.PacketRespondZoneInfo.Handler.class, ZoneInfoController.PacketRespondZoneInfo.class, Side.CLIENT);
        registerPacket(ZoneInfoController.PacketUpdateZoneStatus.Handler.class, ZoneInfoController.PacketUpdateZoneStatus.class, Side.CLIENT);
        registerPacket(SelectionController.PacketPricePerBlock.Handler.class, SelectionController.PacketPricePerBlock.class, Side.CLIENT);
        registerPacket(GuiZoneMembers.PacketModifyMembers.Handler.class, GuiZoneMembers.PacketModifyMembers.class, Side.SERVER);
        registerPacket(GuiZoneMembers.PacketModifyMember.Handler.class, GuiZoneMembers.PacketModifyMember.class, Side.SERVER);
        registerPacket(GuiZoneInfo.PacketModifyZone.Handler.class, GuiZoneInfo.PacketModifyZone.class, Side.SERVER);
        registerPacket(GuiZoneMembers.PacketUpdateMembersList.Handler.class, GuiZoneMembers.PacketUpdateMembersList.class, Side.CLIENT);
        registerPacket(GuiZoneSell.PacketSellZone.Handler.class, GuiZoneSell.PacketSellZone.class, Side.SERVER);

        GameRegistry.registerItem(ItemForSaleSign.getItem(), "ForSaleSign");
        GameRegistry.registerTileEntity(TileEntityForSaleSign.class, "ForSaleSign");
        GameRegistry.registerBlock(BlockForSaleSign.getBlock(true), "ForSaleSign_Standing");
        GameRegistry.registerBlock(BlockForSaleSign.getBlock(false), "ForSaleSign_Wall");
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandClaim());
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.realestate.server.ServerProxy.class;
    }
}
