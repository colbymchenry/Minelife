package com.minelife.gun.packet;

import com.google.common.collect.Sets;
import com.minelife.gun.turrets.EnumMob;
import com.minelife.gun.turrets.TileEntityTurret;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Set;
import java.util.UUID;

public class PacketSetTurretSettings implements IMessage {

    private int TileX, TileY, TileZ;
    private Set<EnumMob> MobWhiteList;
    private Set<UUID> GangWhiteList;

    public PacketSetTurretSettings() {
    }

    public PacketSetTurretSettings(Set<EnumMob> mobWhiteList, Set<UUID> gangWhiteList, int TileX, int TileY, int TileZ) {
        MobWhiteList = mobWhiteList;
        GangWhiteList = gangWhiteList;
        this.TileX = TileX;
        this.TileY = TileY;
        this.TileZ = TileZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        MobWhiteList = Sets.newTreeSet();
        GangWhiteList = Sets.newTreeSet();
        int mobSize = buf.readInt();
        for (int i = 0; i < mobSize; i++) MobWhiteList.add(EnumMob.valueOf(ByteBufUtils.readUTF8String(buf)));
        int gangSize = buf.readInt();
        for (int i = 0; i < gangSize; i++) GangWhiteList.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));

        TileX = buf.readInt();
        TileY = buf.readInt();
        TileZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(MobWhiteList.size());
        MobWhiteList.forEach(mob -> ByteBufUtils.writeUTF8String(buf, mob.name()));
        buf.writeInt(GangWhiteList.size());
        GangWhiteList.forEach(gang -> ByteBufUtils.writeUTF8String(buf, gang.toString()));

        buf.writeInt(TileX);
        buf.writeInt(TileY);
        buf.writeInt(TileZ);
    }

    public static class Handler implements IMessageHandler<PacketSetTurretSettings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetTurretSettings message, MessageContext ctx) {
            EntityPlayerMP Player = ctx.getServerHandler().playerEntity;

            if (Player.worldObj.getTileEntity(message.TileX, message.TileY, message.TileZ) == null) return null;

            TileEntityTurret TileTurret = (TileEntityTurret) Player.worldObj.getTileEntity(message.TileX, message.TileY, message.TileZ);

            if (TileTurret == null) return null;

            if (TileTurret.HasPermissionToModifySettings(Player)) {
                TileTurret.setGangWhiteList(message.GangWhiteList);
                TileTurret.setMobWhiteList(message.MobWhiteList);
            }

            return null;
        }
    }

}
