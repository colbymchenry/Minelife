package com.minelife.gun;

import com.minelife.Minelife;
import com.minelife.gun.client.RenderGun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
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
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class Gun extends Item {

    public ResourceLocation texture;
    public ResourceLocation objModelLocation;
    public IModelCustom model;
    public String name;
    public float fireRate, nextFire;
    public float gravity = 9.81f;
    public float initialBulletVelocity = 5f;

    public Gun(String name, float fireRate, FMLPreInitializationEvent event) {
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
    public void onUpdate(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
//        if (!p_77663_5_) this.tick = 0;
//        else {
        fireRate = 2000;
        if (Mouse.isButtonDown(0) && System.currentTimeMillis() > nextFire) {
            System.out.println("CALLED1");
            nextFire = System.currentTimeMillis() + fireRate;
            this.fire();
            this.shootBullet();
//                if (this.tick % this.rateOfFire == 0 || this.tick == 0) {
//                    this.fire();
//                }

//                this.tick++;
//            } else {
//                this.tick = 0;
//            }
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

    public void shootBullet() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

//        double distanceTraveled = (initialBulletVelocity * Math.cos(player.rotationPitch) / gravity);
//        distanceTraveled *= (initialBulletVelocity * Math.sin(player.rotationPitch)) + Math.sqrt((Math.pow(initialBulletVelocity * Math.sin(player.rotationPitch), 2)) + (2 * gravity * player.posY));
//        double timeTraveled = distanceTraveled / (initialBulletVelocity * Math.cos(player.rotationPitch));

        int range = 50;
        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);

        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();

        for (EntityLivingBase entity : surrounding_entities) {
            double minX = entity.boundingBox.minX / lookVec.xCoord;
            double minY = entity.boundingBox.minY / lookVec.yCoord;
            double minZ = entity.boundingBox.minZ / lookVec.zCoord;

            double maxX = entity.boundingBox.maxX / lookVec.xCoord;
            double maxY = entity.boundingBox.maxY / lookVec.yCoord;
            double maxZ = entity.boundingBox.maxZ / lookVec.zCoord;

            if(minZ <= minX && maxZ >= minX) {
                System.out.println("HIT");
            }
        }
    }

}
