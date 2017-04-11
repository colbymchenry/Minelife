package com.minelife.gun;

import com.minelife.Minelife;
import com.minelife.gun.client.RenderGun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.input.Mouse;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class Gun extends Item implements IExtendedReach {

    public ResourceLocation texture;
    public ResourceLocation objModelLocation;
    public IModelCustom model;
    public String name;
    public int rateOfFire;
    public int tick;

    public Gun(String name, int rateOfFire, FMLPreInitializationEvent event) {
        this.name = name;
        this.rateOfFire = rateOfFire < 1 ? 1 : rateOfFire;
        setUnlocalizedName(name);
        setCreativeTab(ModGun.tabGuns);

        if (event.getSide() == Side.CLIENT) {
            MinecraftForgeClient.registerItemRenderer(this, new RenderGun(this));
            texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/" + name + ".png");
            objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/guns/" + name + ".obj");
            model = AdvancedModelLoader.loadModel(objModelLocation);
        }
    }

    @Override
    public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        if (!p_77663_5_) this.tick = 0;
        else {
            if (Mouse.isButtonDown(0)) {
                if (this.tick % this.rateOfFire == 0 || this.tick == 0) {
                    this.fire();
                }

                this.tick++;
            } else {
                this.tick = 0;
            }
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    public abstract Ammo[] getPossibleAmmo();

    public abstract boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type);

    public abstract boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper);

    public abstract void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data);

    public abstract void fire();

    // This is mostly copied from the EntityRenderer#getMouseOver() method
    public static MovingObjectPosition getMouseOverExtended(float dist) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityLivingBase theRenderViewEntity = mc.renderViewEntity;
        AxisAlignedBB theViewBoundingBox = AxisAlignedBB.getBoundingBox(
                theRenderViewEntity.posX - 0.5D,
                theRenderViewEntity.posY - 0.0D,
                theRenderViewEntity.posZ - 0.5D,
                theRenderViewEntity.posX + 0.5D,
                theRenderViewEntity.posY + 1.5D,
                theRenderViewEntity.posZ + 0.5D
        );
        MovingObjectPosition returnMOP = null;
        if (mc.theWorld != null) {
            double var2 = dist;
            returnMOP = theRenderViewEntity.rayTrace(var2, 0);
            double calcdist = var2;
            Vec3 pos = Vec3.createVectorHelper((double) theRenderViewEntity.posX, (double) theRenderViewEntity.posY + (double) theRenderViewEntity.getEyeHeight(), (double) theRenderViewEntity.posZ);
            var2 = calcdist;
            if (returnMOP != null) {
                calcdist = returnMOP.hitVec.distanceTo(pos);
            }

            Vec3 lookvec = theRenderViewEntity.getLook(0);
            Vec3 var8 = pos.addVector(lookvec.xCoord * var2,
                    lookvec.yCoord * var2,
                    lookvec.zCoord * var2);
            Entity pointedEntity = null;
            float var9 = 1.0F;
            @SuppressWarnings("unchecked")
            List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                    theRenderViewEntity,
                    theViewBoundingBox.addCoord(
                            lookvec.xCoord * var2,
                            lookvec.yCoord * var2,
                            lookvec.zCoord * var2).expand(var9, var9, var9));
            double d = calcdist;

            for (Entity entity : list) {
                if (entity.canBeCollidedWith()) {
                    float bordersize = entity.getCollisionBorderSize();
                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                            entity.posX - entity.width / 2,
                            entity.posY,
                            entity.posZ - entity.width / 2,
                            entity.posX + entity.width / 2,
                            entity.posY + entity.height,
                            entity.posZ + entity.width / 2);
                    aabb.expand(bordersize, bordersize, bordersize);
                    MovingObjectPosition mop0 = aabb.calculateIntercept(pos, var8);

                    if (aabb.isVecInside(pos)) {
                        if (0.0D < d || d == 0.0D) {
                            pointedEntity = entity;
                            d = 0.0D;
                        }
                    } else if (mop0 != null) {
                        double d1 = pos.distanceTo(mop0.hitVec);

                        if (d1 < d || d == 0.0D) {
                            pointedEntity = entity;
                            d = d1;
                        }
                    }
                }
            }

            if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
                returnMOP = new MovingObjectPosition(pointedEntity);
            }
        }
        return returnMOP;
    }

}
