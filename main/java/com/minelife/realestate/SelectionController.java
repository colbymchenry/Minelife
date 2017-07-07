package com.minelife.realestate;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

public class SelectionController {

    public static Vec3 pos1, pos2;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (heldItem == null || heldItem.getItem() != Selector.getInstance()) return;

        // TODO: gold for main selection, red for sub selections, purple for current selection

        if (pos1 != null && pos2 != null) {
            int minX = (int) Math.min(pos1.xCoord, pos2.xCoord);
            int minY = (int) Math.min(pos1.yCoord, pos2.yCoord);
            int minZ = (int) Math.min(pos1.zCoord, pos2.zCoord);
            int maxX = (int) Math.max(pos1.xCoord, pos2.xCoord);
            int maxY = (int) Math.max(pos1.yCoord, pos2.yCoord);
            int maxZ = (int) Math.max(pos1.zCoord, pos2.zCoord);
            int width = Math.abs(maxX) - Math.abs(minX);
            int height = Math.abs(maxY) - Math.abs(minY) + 1;
            int length = Math.abs(maxZ) - Math.abs(minZ);

            int color = 0xffee00;
            GuiUtil.drawSquareInWorld(minX, minY, minZ, width, height, 180f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX - width, minY, minZ, width, height, 0f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX, minY, minZ + length, length, height, 90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX, minY, minZ, length, height, -90f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX - width, minY, minZ + length, width, height, 0f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX, minY, minZ + length, width, height, 180f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX - width, minY, minZ, length, height, -90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX - width, minY, minZ + length, length, height, 90f, event.partialTicks, color);
        }
    }

    @SideOnly(Side.SERVER)
    public static class ServerSelector {

        private static Map<UUID, Vec3> pos1Map = Maps.newHashMap();
        private static Map<UUID, Vec3> pos2Map = Maps.newHashMap();

        @SubscribeEvent
        public void onClick(PlayerInteractEvent event)
        {
            if (event.entityPlayer.getHeldItem() == null || event.entityPlayer.getHeldItem().getItem() != Selector.getInstance())
                return;

            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                pos1Map.put(event.entityPlayer.getUniqueID(), Vec3.createVectorHelper(event.x, event.y, event.z));
                event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos1 set"));
            } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                pos2Map.put(event.entityPlayer.getUniqueID(), Vec3.createVectorHelper(event.x, event.y, event.z));
                event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos2 set"));
            }

            if (pos1Map.containsKey(event.entityPlayer.getUniqueID()) && pos2Map.containsKey(event.entityPlayer.getUniqueID())) {
                Minelife.NETWORK.sendTo(new PacketSelection(
                        pos1Map.get(event.entityPlayer.getUniqueID()),
                        pos2Map.get(event.entityPlayer.getUniqueID())), (EntityPlayerMP) event.entityPlayer);
            }
        }

    }

    public static class PacketSelection implements IMessage {

        public PacketSelection()
        {
        }

        private Vec3 pos1, pos2;

        public PacketSelection(Vec3 pos1, Vec3 pos2)
        {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.pos1 = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
            this.pos2 = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeDouble(pos1.xCoord);
            buf.writeDouble(pos1.yCoord);
            buf.writeDouble(pos1.zCoord);
            buf.writeDouble(pos2.xCoord);
            buf.writeDouble(pos2.yCoord);
            buf.writeDouble(pos2.zCoord);
        }

        public static class Handler implements IMessageHandler<PacketSelection, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketSelection message, MessageContext ctx)
            {
                SelectionController.pos1 = message.pos1;
                SelectionController.pos2 = message.pos2;
                return null;
            }
        }
    }

    public static class Selector extends Item {

        private static Selector instance;

        private Selector()
        {
            setCreativeTab(CreativeTabs.tabTools);
            setUnlocalizedName("Selector");
            setTextureName(Minelife.MOD_ID + ":Selector");
        }

        public static Selector getInstance()
        {
            instance = instance == null ? new Selector() : instance;
            return instance;
        }

    }

}
