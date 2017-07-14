package com.minelife.realestate;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import org.lwjgl.opengl.GL11;

import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;

public class SaleSign {

    public static class Block extends BlockContainer {

        private static Block instanceWall, instanceStanding;
        private boolean standing;

        private Block(boolean standing)
        {
            super(Material.rock);
            this.standing = standing;
            float f = 0.25F;
            float f1 = 1.0F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        }

        public static Block getInstance(boolean standing)
        {
            if (standing) {
                if (instanceStanding == null) instanceStanding = new Block(true);
                return instanceStanding;
            } else {
                if (instanceWall == null) instanceWall = new Block(false);
                return instanceWall;
            }
        }

        @Override
        public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
        {
            return new TileEntity();
        }

        @Override
        public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
        {
            return null;
        }

        @Override
        public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
        {
            if (!this.standing) {
                int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_);
                float f = 0.28125F;
                float f1 = 0.78125F;
                float f2 = 0.0F;
                float f3 = 1.0F;
                float f4 = 0.125F;
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

                if (l == 2) {
                    this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
                }

                if (l == 3) {
                    this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
                }

                if (l == 4) {
                    this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
                }

                if (l == 5) {
                    this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
        {
            this.setBlockBoundsBasedOnState(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
            return super.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
        }

        @SideOnly(Side.CLIENT)
        public net.minecraft.item.Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
        {
            return SaleSign.Item.getInstance();
        }

        @Override
        public int getRenderType()
        {
            return -1;
        }

        @Override
        public boolean renderAsNormalBlock()
        {
            return false;
        }

        @Override
        public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_)
        {
            return true;
        }

        @Override
        public boolean isOpaqueCube()
        {
            return false;
        }

        @Override
        public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
        {
            return SaleSign.Item.getInstance();
        }

        @Override
        public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, net.minecraft.block.Block p_149695_5_)
        {
            boolean flag = false;

            if (this.standing) {
                if (!p_149695_1_.getBlock(p_149695_2_, p_149695_3_ - 1, p_149695_4_).getMaterial().isSolid()) {
                    flag = true;
                }
            } else {
                int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);
                flag = true;

                if (l == 2 && p_149695_1_.getBlock(p_149695_2_, p_149695_3_, p_149695_4_ + 1).getMaterial().isSolid()) {
                    flag = false;
                }

                if (l == 3 && p_149695_1_.getBlock(p_149695_2_, p_149695_3_, p_149695_4_ - 1).getMaterial().isSolid()) {
                    flag = false;
                }

                if (l == 4 && p_149695_1_.getBlock(p_149695_2_ + 1, p_149695_3_, p_149695_4_).getMaterial().isSolid()) {
                    flag = false;
                }

                if (l == 5 && p_149695_1_.getBlock(p_149695_2_ - 1, p_149695_3_, p_149695_4_).getMaterial().isSolid()) {
                    flag = false;
                }
            }

            if (flag) {
                this.dropBlockAsItem(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
                p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
            }

            super.onNeighborBlockChange(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
        }
    }

    public static class Item extends net.minecraft.item.Item {

        private static Item instance;

        private Item()
        {
            this.maxStackSize = 16;
            this.setCreativeTab(CreativeTabs.tabDecorations);
            this.setTextureName(Minelife.MOD_ID + ":SaleSign");
        }

        public static Item getInstance()
        {
            if (instance == null) instance = new Item();
            return instance;
        }

        @Override
        public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
        {
            if (p_77648_7_ == 0) {
                return false;
            } else if (!p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_).getMaterial().isSolid()) {
                return false;
            } else {
                if (p_77648_7_ == 1) {
                    ++p_77648_5_;
                }

                if (p_77648_7_ == 2) {
                    --p_77648_6_;
                }

                if (p_77648_7_ == 3) {
                    ++p_77648_6_;
                }

                if (p_77648_7_ == 4) {
                    --p_77648_4_;
                }

                if (p_77648_7_ == 5) {
                    ++p_77648_4_;
                }

                if (!p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_)) {
                    return false;
                } else if (!Blocks.standing_sign.canPlaceBlockAt(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_)) {
                        return false;
                } else if (p_77648_3_.isRemote) {
                    return true;
                } else {
                    if (p_77648_7_ == 1) {
                        int i1 = MathHelper.floor_double((double) ((p_77648_2_.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                        p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, SaleSign.Block.getInstance(true), i1, 3);
                    } else {
                        p_77648_3_.setBlock(p_77648_4_, p_77648_5_, p_77648_6_, SaleSign.Block.getInstance(false), p_77648_7_, 3);
                    }

                    --p_77648_1_.stackSize;
                    SaleSign.TileEntity tileentitysign = (SaleSign.TileEntity) p_77648_3_.getTileEntity(p_77648_4_, p_77648_5_, p_77648_6_);

                    if (tileentitysign != null) {
                        p_77648_2_.func_146100_a(tileentitysign);
                    }

                    return true;
                }
            }
        }
    }

    // TODO: Fix break texture and item texture
    public static class TileEntity extends net.minecraft.tileentity.TileEntity {

        private boolean forSale, forRent, sold;

        @Override
        public void readFromNBT(NBTTagCompound tag)
        {
            super.readFromNBT(tag);
            forSale = tag.getBoolean("forSale");
            forRent = tag.getBoolean("forRent");
            sold = tag.getBoolean("sold");
        }

        @Override
        public void writeToNBT(NBTTagCompound tag)
        {
            super.writeToNBT(tag);
            tag.setBoolean("forSale", forSale);
            tag.setBoolean("forRent", forRent);
            tag.setBoolean("sold", sold);
        }

        @Override
        public void updateEntity()
        {
            super.updateEntity();
            if(!worldObj.isRemote) return;

            Zone zone = Zone.getZone(worldObj, Vec3.createVectorHelper(this.xCoord, this.yCoord, this.zCoord));
            if(zone == null) {
                worldObj.getBlock(xCoord, yCoord, zCoord).dropBlockAsItem(worldObj, xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 0);
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            } else {
                if(ZoneForSale.hasListing(zone)) {
                    try {
                        ZoneForSale zoneForSale = new ZoneForSale(zone);
                        if(zoneForSale.owner != null) {
                            forRent = false;
                            forSale = false;
                            sold = true;
                            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                            this.markDirty();
                        } else {
                            if(zoneForSale.forRent) {
                                forRent = true;
                                forSale = false;
                            } else {
                                forSale = true;
                                forRent = false;
                            }
                            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                            this.markDirty();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    forSale = false;
                    forRent = false;
                    sold = false;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    this.markDirty();
                }
            }
        }

        @Override
        public Packet getDescriptionPacket()
        {
            NBTTagCompound tagCompound = new NBTTagCompound();
            this.writeToNBT(tagCompound);
            return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
        {
            this.readFromNBT(pkt.func_148857_g());
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Renderer extends TileEntitySpecialRenderer {

        private static final ResourceLocation textureForRent = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_ForRent.png");
        private static final ResourceLocation textureForSale = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_ForSale.png");
        private static final ResourceLocation textureSold = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/SaleSign_Sold.png");
        private final ModelSign modelSign = new ModelSign();

        @Override
        public void renderTileEntityAt(net.minecraft.tileentity.TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
        {
            net.minecraft.block.Block block = p_147500_1_.getBlockType();
            GL11.glPushMatrix();
            float f1 = 0.6666667F;
            float f3;

            if (block == SaleSign.Block.getInstance(true)) {
                GL11.glTranslatef((float) p_147500_2_ + 0.5F, (float) p_147500_4_ + 0.75F * f1, (float) p_147500_6_ + 0.5F);
                float f2 = (float) (p_147500_1_.getBlockMetadata() * 360) / 16.0F;
                GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
                this.modelSign.signStick.showModel = true;
            } else {
                int j = p_147500_1_.getBlockMetadata();
                f3 = 0.0F;

                if (j == 2) {
                    f3 = 180.0F;
                }

                if (j == 4) {
                    f3 = 90.0F;
                }

                if (j == 5) {
                    f3 = -90.0F;
                }

                GL11.glTranslatef((float) p_147500_2_ + 0.5F, (float) p_147500_4_ + 0.75F * f1, (float) p_147500_6_ + 0.5F);
                GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
                this.modelSign.signStick.showModel = false;
            }

            TileEntity tileEntity = (TileEntity) p_147500_1_;

            this.bindTexture(tileEntity.forRent ? textureForRent : tileEntity.forSale ? textureForSale : textureSold);
            GL11.glPushMatrix();
            GL11.glScalef(f1, -f1, -f1);
            this.modelSign.renderSign();
        }
    }

    public static class Listener {

        @SubscribeEvent
        public void onPlace(BlockEvent.PlaceEvent event) {
            if(!(event.block instanceof SaleSign.Block)) return;

            Zone zone = Zone.getZone(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));

            // TODO: Finish this
            try {
                if (zone == null) throw new CustomMessageException("Could not find a zone there.");
            }catch(Exception e) {
                if(e instanceof CustomMessageException) {
                    event.player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
                } else {
                    e.printStackTrace();
                    Minelife.getLogger().log(Level.SEVERE, "", e);
                    event.player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                }
            }
        }

    }
}
