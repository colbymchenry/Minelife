package com.minelife.realestate.client;

import com.google.common.collect.Lists;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ItemEstatePackageFormer;
import com.minelife.util.BlockVector;
import com.minelife.util.StringToList;
import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.List;
import java.util.UUID;

public class RenderSelection {

    public static BlockVector min, max;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if(heldItem == null || heldItem.getItem() != ItemEstatePackageFormer.instance) return;

        NBTTagCompound tagCompound = heldItem.hasTagCompound() ? heldItem.getTagCompound() : new NBTTagCompound();

        List<Chunk> estates = Lists.newArrayList();

        if (tagCompound.hasKey("estates")) {
            StringToList<Chunk> stringToList = new StringToList<Chunk>(tagCompound.getString("estates")) {
                @Override
                public Chunk parse(String s)
                {
                    String[] data = s.split(";")[1].split(",");
                    int x = Integer.parseInt(data[0]);
                    int z = Integer.parseInt(data[1]);
                    return Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
                }
            };

            estates.addAll(stringToList.getList());
        }

        double playerX = Math.floor(Minecraft.getMinecraft().thePlayer.posX);
        double playerZ = Math.floor(Minecraft.getMinecraft().thePlayer.posZ);

        for(Chunk estate : estates) {
            int x = estate.xPosition * 16;
            int y = (int) Minecraft.getMinecraft().thePlayer.posY;
            int z = estate.zPosition * 16;

            if(playerX >= x && playerZ >= z && playerX <= x + 16 && playerZ <= z + 16) {
                int color = 0xffee00;

                GuiUtil.drawSquareInWorld(x + 16, y, z, 16, 1, 180f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(x, y, z, 16, 1, 0f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(x + 16, y, z + 17, 17, 1, 90f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(x + 16, y, z, 16, 1, -90f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(x, y, z + 17, 16, 1, 0f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(x + 16, y, z + 17, 16, 1, 180f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(x, y, z, 17, 1, -90f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(x, y, z + 17, 16, 1, 90f, event.partialTicks, color);
                return;
            }
        }
    }

}
