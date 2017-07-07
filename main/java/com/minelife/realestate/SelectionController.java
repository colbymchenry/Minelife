package com.minelife.realestate;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.client.OnScreenRenderer;
import com.minelife.util.client.GuiUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.UUID;

public class SelectionController {

    public static Vec3 pos1, pos2;
    public static int pricePerBlock = 0;

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && pos1 != null && pos2 != null) {
            ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
            if(heldItem != null && heldItem.getItem() == Selector.getInstance()) {
                Region bounds = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                int centerX = (event.resolution.getScaledWidth() / 2) + 20;
                int centerY = (event.resolution.getScaledHeight() / 2) - 15;
                fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD + "Cost per block: " + EnumChatFormatting.RED + "$" + pricePerBlock, centerX, centerY, 0xFFFFFF);
                long total = (bounds.getArea() * pricePerBlock);
                String totalStr = OnScreenRenderer.formatter.format(total);
                if(ModEconomy.BALANCE_WALLET_CLIENT < total) {
                    fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "Total: " + EnumChatFormatting.RED.toString() + EnumChatFormatting.STRIKETHROUGH.toString() + "$" + totalStr, centerX, centerY += 12, 0xFFFFFF);
                    fontRenderer.drawStringWithShadow(EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD.toString()  + "Insufficient funds.", centerX + 10, centerY += 12, 0xFFFFFF);
                } else {
                    fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD + "Total: " + EnumChatFormatting.RED + "$" + totalStr, centerX, centerY += 12, 0xFFFFFF);
                }
                fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD + "Type " + EnumChatFormatting.RED.toString() + EnumChatFormatting.BOLD + "/claim" + EnumChatFormatting.GOLD + " to claim selection.", centerX, centerY += 12, 0xFFFFFF);
                GL11.glColor4f(1, 1, 1, 1);
            }
        }
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (heldItem == null || heldItem.getItem() != Selector.getInstance()) return;

        if (pos1 != null && pos2 != null) {
            Region bounds = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));

            int color = 0xffee00;
            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getWidth(), bounds.getHeight(), 180f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getWidth(), bounds.getHeight(), 0f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getLength(), bounds.getHeight(), 90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getLength(), bounds.getHeight(), -90f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getWidth(), bounds.getHeight(), 0f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getWidth(), bounds.getHeight(), 180f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getLength(), bounds.getHeight(), -90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getLength(), bounds.getHeight(), 90f, event.partialTicks, color);
        }
    }

    @SideOnly(Side.SERVER)
    public static class ServerSelector {

        public static Map<UUID, Vec3> pos1Map = Maps.newHashMap();
        public static Map<UUID, Vec3> pos2Map = Maps.newHashMap();

        @SubscribeEvent
        public void onClick(PlayerInteractEvent event)
        {
            if (event.entityPlayer.getHeldItem() == null || event.entityPlayer.getHeldItem().getItem() != Selector.getInstance())
                return;

            Minelife.NETWORK.sendTo(new PacketPricePerBlock(Integer.parseInt(String.valueOf(ModRealEstate.config.getOptions().get("PricePerBlock")))), (EntityPlayerMP) event.entityPlayer);

            if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                pos1Map.put(event.entityPlayer.getUniqueID(), Vec3.createVectorHelper(event.x, event.y, event.z));
                event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos1 set"));
            } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                pos2Map.put(event.entityPlayer.getUniqueID(), Vec3.createVectorHelper(event.x, event.y, event.z));
                event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos2 set"));
            }

            event.setCanceled(true);

            if (pos1Map.containsKey(event.entityPlayer.getUniqueID()) && pos2Map.containsKey(event.entityPlayer.getUniqueID())) {
                Minelife.NETWORK.sendTo(new PacketSelection(
                        pos1Map.get(event.entityPlayer.getUniqueID()),
                        pos2Map.get(event.entityPlayer.getUniqueID())), (EntityPlayerMP) event.entityPlayer);
            }
        }

        @SubscribeEvent
        public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            pos1Map.remove(event.player.getUniqueID());
            pos2Map.remove(event.player.getUniqueID());
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

    public static class PacketPricePerBlock implements IMessage {

        private int pricePerBlock;

        public PacketPricePerBlock()
        {
        }

        public PacketPricePerBlock(int pricePerBlock)
        {
            this.pricePerBlock = pricePerBlock;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            pricePerBlock = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(pricePerBlock);
        }

        public static class Handler implements IMessageHandler<PacketPricePerBlock, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketPricePerBlock message, MessageContext ctx)
            {
                SelectionController.pricePerBlock = message.pricePerBlock;
                return null;
            }
        }
    }

}
