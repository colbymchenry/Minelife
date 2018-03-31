package com.minelife.drugs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.util.client.render.LineRenderer;
import com.minelife.util.client.render.Vector;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.MaterialProperty;
import ic2.core.block.type.ResourceBlock;
import ic2.core.item.type.OreResourceType;
import ic2.core.ref.BlockName;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class XRayEffect extends Potion {

    public static Potion INSTANCE = new XRayEffect(false, 0);

    private static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/potion_xray.png");

    private XRayEffect(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        setIconIndex(0, 0).setPotionName("potion.xray");
        setRegistryName(Minelife.MOD_ID, "potion.xray");
    }

    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        return super.getStatusIconIndex();
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent e) {
        if (e.getEntityLiving().isPotionActive(this)) {
            if (e.getEntityLiving().getActivePotionEffect(this).getDuration() == 0) {
                e.getEntityLiving().removePotionEffect(this);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLastWorldEvent(RenderWorldLastEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        int player_x = (int) player.posX;
        int player_y = (int) player.posY;
        int player_z = (int) player.posZ;
        int radius = 10;

        if (!player.isPotionActive(this)) return;

        Map<IBlockState, Color> whitelist = Maps.newHashMap();
        whitelist.put(Blocks.DIAMOND_ORE.getDefaultState(), new Color(0, 217, 255, 50));
        whitelist.put(Blocks.IRON_ORE.getDefaultState(), new Color(255, 154, 0, 50));
        whitelist.put(Blocks.GOLD_ORE.getDefaultState(), new Color(250, 255, 0, 50));
        whitelist.put(Blocks.LAPIS_ORE.getDefaultState(), new Color(0, 1, 255, 50));
        whitelist.put(Blocks.REDSTONE_ORE.getDefaultState(), new Color(255, 0, 1, 50));
        whitelist.put(Blocks.EMERALD_ORE.getDefaultState(), new Color(7, 255, 0, 50));
        whitelist.put(BlockName.resource.getBlockState(ResourceBlock.copper_ore), new Color(165, 93, 53, 50));
        whitelist.put(BlockName.resource.getBlockState(ResourceBlock.tin_ore), new Color(211, 212, 213, 50));
        whitelist.put(BlockName.resource.getBlockState(ResourceBlock.uranium_ore), new Color(93, 165, 116, 50));
        whitelist.put(BlockName.resource.getBlockState(ResourceBlock.lead_ore), new Color(132, 116, 90, 50));
        whitelist.put(ModGuns.blockZincOre.getDefaultState(), new Color(186, 196, 200, 50));
//        whitelist.put(MLBlocks.sulfur_ore, new Color(163, 165, 66, 50));
//        whitelist.put(MLBlocks.pyrolusite_ore, new Color(26, 27, 27, 50));

        List<Vector> block_vectors = Lists.newArrayList();

        for (int x = player_x + radius; x > player_x - radius; x--)
            for (int y = player_y + radius; y > player_y - radius; y--)
                for (int z = player_z + radius; z > player_z - radius; z--) {
                    if (whitelist.containsKey(player.getEntityWorld().getBlockState(new BlockPos(x, y, z))))
                        block_vectors.add(new Vector(x, y, z));
                }


        for (Vector block_vector : block_vectors) {
            IBlockState block = player.getEntityWorld().getBlockState(new BlockPos(block_vector.getBlockX(), block_vector.getBlockY(), block_vector.getBlockZ()));
            LineRenderer.drawCuboidAroundBlocks(Minecraft.getMinecraft(), block_vector, block_vector, event.getPartialTicks(), whitelist.get(block), false, false);
        }

    }

}
