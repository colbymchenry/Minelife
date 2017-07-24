package com.minelife.realestate.sign;

import com.minelife.realestate.Zone;
import com.minelife.realestate.client.GuiZoneSell;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class ListenerForSaleSign {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event)
    {
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        EntityPlayer player = event.player;

        if(player.getHeldItem() == null) return;

        if(player.getHeldItem().getItem() != ItemForSaleSign.getItem()) return;

        Zone zone = Zone.getZone(world, Vec3.createVectorHelper(x, y, z));
        if (zone == null) {
            player.addChatComponentMessage(new ChatComponentText("There is no zone there."));
            event.setCanceled(true);
            return;
        }

        if (zone.getOwner() == null || !zone.getOwner().equals(player.getUniqueID())) {
            player.addChatComponentMessage(new ChatComponentText("You are not the owner of this zone."));
            event.setCanceled(true);
            return;
        }

        TileEntityForSaleSign tileEntityForSaleSign = (TileEntityForSaleSign) world.getTileEntity(x, y, z);

        for (Object o : world.loadedTileEntityList) {
            TileEntity tileEntity = (TileEntity) o;
            if (tileEntity instanceof TileEntityForSaleSign) {
                if(tileEntity != tileEntityForSaleSign) {
                    if (zone.getRegion().contains(world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) {
                        event.setCanceled(true);
                        player.addChatComponentMessage(new ChatComponentText("There is arleady a for sale sign in this zone."));
                        return;
                    }
                }
            }
        }

        if (tileEntityForSaleSign != null)
            GuiZoneSell.PacketOpenGuiZoneSell.openFromServer(tileEntityForSaleSign, (EntityPlayerMP) player);
    }

}
