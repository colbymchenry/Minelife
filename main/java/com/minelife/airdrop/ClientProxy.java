package com.minelife.airdrop;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.util.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.ListIterator;

public class ClientProxy extends MLProxy {

    private static List<Airdrop> stopRendering = Lists.newArrayList();

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public synchronized void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        stopRendering.clear();
        ModAirdrop.airdrops.clear();
    }

    int tick = 0;

    @SubscribeEvent
    public synchronized void renderWorld(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;


        List<Airdrop> remove = Lists.newArrayList();
        List<Airdrop> airdropList = Lists.newArrayList(ModAirdrop.airdrops);
        ListIterator<Airdrop> iterator = airdropList.listIterator();

        GlStateManager.pushAttrib();
        while (iterator.hasNext()) {
            Airdrop airdrop = iterator.next();
            GlStateManager.pushMatrix();

            tick++;
            if (tick > 3) {
                tick = 0;
                if (!stopRendering.contains(airdrop))
                    mc.player.getEntityWorld().spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, airdrop.x + 0.5, airdrop.y + 0.5, airdrop.z + 0.5, MathHelper.nextDouble(mc.world.rand, 0, 0.2), MathHelper.nextDouble(mc.world.rand, 0.4, 0.8), MathHelper.nextDouble(mc.world.rand, 0, 0.2));
            }

            double shiftX = airdrop.x - mc.getRenderManager().viewerPosX;
            double shiftY = airdrop.y - mc.getRenderManager().viewerPosY;
            double shiftZ = airdrop.z - mc.getRenderManager().viewerPosZ;
            double distance = player.getPosition().getDistance((int) airdrop.x, (int) airdrop.y, (int) airdrop.z);

            int maxRadius = mc.gameSettings.renderDistanceChunks * (100 / 8);

            shiftX = shiftX < -maxRadius ? -maxRadius : shiftX > maxRadius ? maxRadius : shiftX;
            shiftY = shiftY < -maxRadius ? -maxRadius : shiftY > maxRadius ? maxRadius : shiftY;
            shiftZ = shiftZ < -maxRadius ? -maxRadius : shiftZ > maxRadius ? maxRadius : shiftZ;

            GlStateManager.translate(shiftX, shiftY, shiftZ);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.scale(2, 2, 2);
            if (!stopRendering.contains(airdrop) && distance < 200) {
                mc.getRenderItem().renderItem(new ItemStack(Blocks.CHEST), ItemCameraTransforms.TransformType.FIXED);
            }

            float f = mc.getRenderManager().playerViewX;
            float f1 = mc.getRenderManager().playerViewY;
            boolean flag1 = mc.getRenderManager().options != null ? mc.getRenderManager().options.thirdPersonView == 2 : false;

            double scale = distance * 0.001;

            scale = scale > 0.2 ? 0.2 : scale;
            scale = scale < 0.04 ? 0.04 : scale;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (flag1 ? -1 : 1) * f, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180, 0, 0, 1);

            GlStateManager.disableDepth();
            mc.fontRenderer.drawString(StringHelper.ParseFormatting("&4[AirDrop]", '&'), 0, 0, 0xFFFFFF);
            mc.fontRenderer.drawString(StringHelper.ParseFormatting("&6" + Math.round(distance), '&'), 0, 10, 0xFFFFFF);
            GlStateManager.enableDepth();

            GlStateManager.popMatrix();
            if (mc.player.getEntityWorld().getBlockState(new BlockPos(airdrop.x, airdrop.y + 1, airdrop.z)).getBlock() == Blocks.CHEST) {
                stopRendering.add(airdrop);
            }
            ModAirdrop.airdrops.removeAll(remove);
        }
        GlStateManager.popAttrib();


    }

}
