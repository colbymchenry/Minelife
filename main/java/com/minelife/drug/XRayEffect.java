package com.minelife.drug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.realestate.util.GUIUtil;
import com.minelife.util.Vector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class XRayEffect extends Potion {

    public static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x_ray_effect.png");

    public XRayEffect(int id, boolean is_harmful, int amplifier)
    {
        super(id, is_harmful, amplifier);
        setIconIndex(0, 0).setPotionName("potion.x_ray_effect");
    }

    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        return super.getStatusIconIndex();
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent e)
    {
        if (e.entityLiving.isPotionActive(ModDrugs.x_ray_potion)) {
            if (e.entityLiving.getActivePotionEffect(ModDrugs.x_ray_potion).getDuration() == 0) {
                e.entityLiving.removePotionEffect(ModDrugs.x_ray_potion.id);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLastWorldEvent(RenderWorldLastEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int player_x = (int) player.posX;
        int player_y = (int) player.posY;
        int player_z = (int) player.posZ;
        int radius = 10;

        if (!player.isPotionActive(ModDrugs.x_ray_potion)) return;

        Map<Block, Color> whitelist = Maps.newHashMap();
        whitelist.put(Blocks.diamond_ore, new Color(0, 217, 255, 50));
        whitelist.put(Blocks.iron_ore, new Color(255, 154, 0, 50));
        whitelist.put(Blocks.gold_ore, new Color(250, 255, 0, 50));
        whitelist.put(Blocks.lapis_ore, new Color(0, 1, 255, 50));
        whitelist.put(Blocks.redstone_ore, new Color(255, 0, 1, 50));
        whitelist.put(Blocks.emerald_ore, new Color(7, 255, 0, 50));
        whitelist.put(Block.getBlockFromItem(Ic2Items.copperOre.getItem()), new Color(165, 93, 53, 50));
        whitelist.put(Block.getBlockFromItem(Ic2Items.tinOre.getItem()), new Color(211, 212, 213, 50));
        whitelist.put(Block.getBlockFromItem(Ic2Items.uraniumOre.getItem()), new Color(93, 165, 116, 50));
        whitelist.put(Block.getBlockFromItem(Ic2Items.leadOre.getItem()), new Color(132, 116, 90, 50));
        whitelist.put(MLBlocks.zinc_ore, new Color(186, 196, 200, 50));
        whitelist.put(MLBlocks.sulfur_ore, new Color(163, 165, 66, 50));
        whitelist.put(MLBlocks.pyrolusite_ore, new Color(26, 27, 27, 50));

        List<Vector> block_vectors = Lists.newArrayList();

        for (int x = player_x + radius; x > player_x - radius; x--)
            for (int y = player_y + radius; y > player_y - radius; y--)
                for (int z = player_z + radius; z > player_z - radius; z--) {
                    Block block = player.worldObj.getBlock(x, y, z);
                    if (block != null && block != Blocks.air && whitelist.containsKey(block))
                        block_vectors.add(new Vector(x, y, z));
                }


        for (Vector block_vector : block_vectors) {
            Block block = player.worldObj.getBlock(block_vector.getBlockX(), block_vector.getBlockY(), block_vector.getBlockZ());
            GUIUtil.drawCuboidAroundBlocks(Minecraft.getMinecraft(), block_vector, block_vector, event.partialTicks, whitelist.get(block), false);
        }

    }

}
