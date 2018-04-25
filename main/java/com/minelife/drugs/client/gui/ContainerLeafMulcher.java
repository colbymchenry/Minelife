package com.minelife.drugs.client.gui;

public class ContainerLeafMulcher {

}

//public class ContainerLeafMulcher extends ContainerBCTile<TileEntityLeafMulcher> {
//
//    public final WidgetFluidTank widgetTankFuel;
//
//    public ContainerLeafMulcher(EntityPlayer player, TileEntityLeafMulcher tile) {
//        super(player, tile);
//
//        // add input slot for item
//        this.addSlotToContainer(new SlotBase(tile.invMaterials, 0, 54, 34));
//        // add output slot
//        this.addSlotToContainer(new SlotOutput(tile.invResult, 0, 116, 34));
//
//        addFullPlayerInventory(84);
//
//        widgetTankFuel = addWidget(new WidgetFluidTank(this, tile.tankFuel));
//    }
//
//    @Override
//    public boolean canInteractWith(EntityPlayer player) {
//        return true;
//    }
//
//}