package com.minelife.gun;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.gun.client.RenderGun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import java.awt.geom.Line2D;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class Gun extends Item {

    public ResourceLocation texture;
    public ResourceLocation objModelLocation;
    public IModelCustom model;
    public String name;
    public long fireRate;

    @SideOnly(Side.CLIENT)
    public long nextFire;

    public Gun(String name, long fireRate, FMLPreInitializationEvent event) {
        this.name = name;
        this.fireRate = fireRate;
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
    public void onUpdate(ItemStack stack, World world, Entity holder, int arg, boolean inHand) {
        // only call on the client side
        if(!world.isRemote) return;
        if(!inHand) return;

        if (Mouse.isButtonDown(0) && System.currentTimeMillis() > nextFire) {
            nextFire = System.currentTimeMillis() + fireRate;
            Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + getSoundName(getPossibleAmmo()[0]), 0.5F, 1.0F);
            this.fire();
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

    public abstract String getSoundName(Ammo ammo);

    @SideOnly(Side.SERVER)
    public void shootBullet(EntityPlayerMP player, ItemStack itemStack) {
//        double distanceTraveled = (initialBulletVelocity * Math.cos(player.rotationPitch) / gravity);
//        distanceTraveled *= (initialBulletVelocity * Math.sin(player.rotationPitch)) + Math.sqrt((Math.pow(initialBulletVelocity * Math.sin(player.rotationPitch), 2)) + (2 * gravity * player.posY));
//        double timeTraveled = distanceTraveled / (initialBulletVelocity * Math.cos(player.rotationPitch));

        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());

        if(System.currentTimeMillis() > (!tagCompound.hasKey("nextFire") ? 0 : tagCompound.getLong("nextFire"))) {
            tagCompound.setLong("nextFire", System.currentTimeMillis() + fireRate);
            itemStack.readFromNBT(tagCompound);
        } else {
            return;
        }

        player.worldObj.playSoundToNearExcept(player, Minelife.MOD_ID + ":" + getSoundName(getPossibleAmmo()[0]), 0.5F, 1.0F);

        // TODO: Check if block is in the way
        int range = 50;
        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);

        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();

        for (EntityLivingBase entity : surrounding_entities) {
            if(entity != player) {
                double minX = (entity.boundingBox.minX - player.posX) / lookVec.xCoord;
                double maxX = (entity.boundingBox.maxX - player.posX) / lookVec.xCoord;

                double minY = (entity.boundingBox.minY - player.posY) / lookVec.yCoord;
                double maxY = (entity.boundingBox.maxY - player.posY) / lookVec.yCoord;

                double minZ = (entity.boundingBox.minZ - player.posZ) / lookVec.zCoord;
                double maxZ = (entity.boundingBox.maxZ - player.posZ) / lookVec.zCoord;

                if(lookVec.xCoord < 0) {
                    double min_x = minX;
                    minX = maxX;
                    maxX = min_x;
                }

                if(lookVec.yCoord < 0) {
                    double min_y = minY;
                    minY = maxY;
                    maxY = min_y;
                }

                if(lookVec.zCoord < 0) {
                    double min_z = minZ;
                    minZ = maxZ;
                    maxZ = min_z;
                }

                double largestMinValue = minX;

                if(minZ > minX && minZ > minY) {
                    largestMinValue = minZ;
                } else if (minY > minX && minY > minZ) {
                    largestMinValue = minY;
                }

                double smallestMaxValue = maxX;

                if(maxZ < maxX && maxZ < maxY) {
                    smallestMaxValue = maxZ;
                } else if(maxY < maxX && maxY < maxZ) {
                    smallestMaxValue = maxY;
                }

                if(smallestMaxValue > largestMinValue) {
                    MinecraftForge.EVENT_BUS.post(new EntityShotEvent(player, entity, player.getHeldItem()));
                    return;
                }
            }
        }
    }

}
