package com.minelife.gun;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class MessageExtendedReachAttack implements IMessage {
    private int entityId;

    public MessageExtendedReachAttack() {
        // need this constructor
    }

    public MessageExtendedReachAttack(int parEntityId) {
        entityId = parEntityId;
        // DEBUG
        System.out.println("Constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = ByteBufUtils.readVarInt(buf, 4);
        // DEBUG
        System.out.println("fromBytes");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
        // DEBUG
        System.out.println("toBytes encoded");
    }

    public static class Handler implements IMessageHandler<MessageExtendedReachAttack,
            IMessage> {
        @Override
        public IMessage onMessage(final MessageExtendedReachAttack message, MessageContext ctx) {

            final EntityPlayerMP thePlayer = ctx.getServerHandler().playerEntity;
            Entity theEntity = thePlayer.worldObj.getEntityByID(message.entityId);

            // DEBUG
            System.out.println("Entity = " + theEntity);

            // Need to ensure that hackers can't cause trick kills,
            // so double check weapon type and reach
            if (thePlayer.getCurrentEquippedItem() == null) {
                return null;
            }
            if (thePlayer.getCurrentEquippedItem().getItem() instanceof
                    IExtendedReach) {
                IExtendedReach theExtendedReachWeapon =
                        (IExtendedReach) thePlayer.getCurrentEquippedItem().
                                getItem();
                double distanceSq = thePlayer.getDistanceSqToEntity(
                        theEntity);
                double reachSq = theExtendedReachWeapon.getReach() *
                        theExtendedReachWeapon.getReach();
                if (reachSq >= distanceSq) {
                    thePlayer.attackTargetEntityWithCurrentItem(
                            theEntity);
                }
            }

            return null;
        }
    }
}