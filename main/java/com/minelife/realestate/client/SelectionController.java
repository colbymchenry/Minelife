package com.minelife.realestate.client;

import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class SelectionController {

    private Vec3 pos1, pos2;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if (heldItem == null || heldItem.getItem() != Selector.getInstance()) return;

        // gold for main selection, red for sub selections, purple for current selection

        if(pos1 != null && pos2 != null) {
            int minX = (int) Math.min(pos1.xCoord, pos2.xCoord);
            int minY = (int) Math.min(pos1.yCoord, pos2.yCoord);
            int minZ = (int) Math.min(pos1.zCoord, pos2.zCoord);
            int maxX = (int) Math.max(pos1.xCoord, pos2.xCoord);
            int maxY = (int) Math.max(pos1.yCoord, pos2.yCoord);
            int maxZ = (int) Math.max(pos1.zCoord, pos2.zCoord);
            int width = Math.abs(maxX) - Math.abs(minX);
            int height = Math.abs(maxY) - Math.abs(minY);
            int length = Math.abs(maxZ) - Math.abs(minZ);

            int color = 0xffee00;
            GuiUtil.drawSquareInWorld(minX + width, minY, minZ, width, 1, 180f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX, minY, minZ, width, 1, 0f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX + width, minY, minZ + length, 17, 1, 90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX + width, minY, minZ, length, 1, -90f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX, minY, minZ + length, length, 1, 0f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX + width, minY, minZ + length, length, 1, 180f, event.partialTicks, color);

            GuiUtil.drawSquareInWorld(minX, minY, minZ, width, 1, -90f, event.partialTicks, color);
            GuiUtil.drawSquareInWorld(minX, minY, minZ + length, width, 1, 90f, event.partialTicks, color);
        }
    }

    @SubscribeEvent
    public void onClick(PlayerInteractEvent event) {

        if(event.entityPlayer.getHeldItem() == null || event.entityPlayer.getHeldItem().getItem() != Selector.getInstance()) return;

        if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            pos1 = Vec3.createVectorHelper(event.x, event.y, event.z);
            event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos1 set"));
        } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            pos2 = Vec3.createVectorHelper(event.x, event.y, event.z);
            event.entityPlayer.addChatComponentMessage(new ChatComponentText("Pos2 set"));
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
