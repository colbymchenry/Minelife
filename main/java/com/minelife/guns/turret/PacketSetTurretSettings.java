package com.minelife.guns.turret;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.Set;
import java.util.UUID;

public class PacketSetTurretSettings implements IMessage {

    private int TileX, TileY, TileZ;
    private Set<EnumMob> MobWhiteList;
    private boolean waitAgroPlayer;

    public PacketSetTurretSettings() {
    }

    public PacketSetTurretSettings(Set<EnumMob> mobWhiteList, boolean waitAgroPlayer, int TileX, int TileY, int TileZ) {
        MobWhiteList = mobWhiteList;
        this.TileX = TileX;
        this.TileY = TileY;
        this.TileZ = TileZ;
        this.waitAgroPlayer = waitAgroPlayer;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        MobWhiteList = Sets.newTreeSet();
        int mobSize = buf.readInt();
        for (int i = 0; i < mobSize; i++) MobWhiteList.add(EnumMob.valueOf(ByteBufUtils.readUTF8String(buf)));

        TileX = buf.readInt();
        TileY = buf.readInt();
        TileZ = buf.readInt();

        waitAgroPlayer = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(MobWhiteList.size());
        MobWhiteList.forEach(mob -> ByteBufUtils.writeUTF8String(buf, mob.name()));

        buf.writeInt(TileX);
        buf.writeInt(TileY);
        buf.writeInt(TileZ);

        buf.writeBoolean(waitAgroPlayer);
    }

    public static class Handler implements IMessageHandler<PacketSetTurretSettings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetTurretSettings message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (player.getEntityWorld().getTileEntity(new BlockPos(message.TileX, message.TileY, message.TileZ)) == null)
                    return;

                TileEntityTurret TileTurret = (TileEntityTurret) player.getEntityWorld().getTileEntity(new BlockPos(message.TileX, message.TileY, message.TileZ));

                if (TileTurret == null) return;

                if (TileTurret.hasPermissionToModifySettings(player)) {
                    TileTurret.setMobWhiteList(message.MobWhiteList);
                    TileTurret.setWaitForAgroPlayer(message.waitAgroPlayer);
                }
            });


            return null;
        }
    }
}
