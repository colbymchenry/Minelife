package com.minelife.drugs.block;

import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class BlockHempCrop extends BlockCrops {

    public static final PropertyInteger FEMALE = PropertyInteger.create("female", 0, 1);

    public BlockHempCrop() {
        setRegistryName(Minelife.MOD_ID, "hemp_crop");
        setUnlocalizedName(Minelife.MOD_ID + ":hemp_crop");
        setTickRandomly(true);
        setCreativeTab(null);
        setHardness(0.0F);
        disableStats();
    }

    // TODO: Still grows in non farmland

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {
        return 1;
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        int i = this.getAge(state);
        int j = this.getMaxAge();

        if (i > j) {
            i = j;
        }

        boolean alreadyFemale = state.getValue(FEMALE) == 1;
        worldIn.setBlockState(pos, state.withProperty(AGE, i).withProperty(FEMALE, alreadyFemale ? 1 : 0), 2);
    }

    // can be used for bonemeal in the RightClickBlockEvent if we decide to enable it
    public int getGrowValue(World worldIn, BlockPos pos, IBlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();

        if (i > j) {
            i = j;
        }

        return i;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FEMALE);
        i = i | state.getValue(AGE) << 1;
        return i;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int female = meta & 1;
        int age = Integer.valueOf((meta & 14) >> 1);
        return withAge(age).withProperty(FEMALE, female);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);

        boolean alreadyFemale = state.getValue(FEMALE) == 1;
        if (getAge(state) == 0) {
            boolean female = worldIn.rand.nextInt(100) > 49;
            worldIn.setBlockState(pos, this.withAge(0).withProperty(FEMALE, female || alreadyFemale ? 1 : 0));
        } else {
            worldIn.setBlockState(pos, state.withProperty(FEMALE, alreadyFemale ? 1 : 0));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            return;
        }

        if (!worldIn.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            int i = this.getAge(state);

            if (i < this.getMaxAge()) {
                float f = getGrowthChance(this, worldIn, pos);

                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
                    boolean alreadyFemale = state.getValue(FEMALE) == 1;
                    worldIn.setBlockState(pos, this.withAge(i + 1).withProperty(FEMALE, alreadyFemale ? 1 : 0), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = worldIn.getBlockState(pos.down());
//        System.out.println(isBlockValid(worldIn, pos) + "," + soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this));
        return isBlockValid(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return super.canGrow(worldIn, pos, state, isClient) && isBlockValid(worldIn, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{AGE, FEMALE});
    }

    private static Random random = new Random();

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);
        if (state.getValue(FEMALE) == 1 && state.getValue(AGE) == 7)
            drops.add(new ItemStack(ModDrugs.itemHempBuds, random.nextInt(2) + 1));
    }

    @Override
    protected Item getCrop() {
        return Items.AIR;
    }

    @Override
    protected Item getSeed() {
        return ModDrugs.itemHempSeed;
    }

    public boolean isBlockValid(World world, BlockPos pos) {
//        boolean blockLeft = world.getBlockState(pos.add(-1, 0, 0)).getBlock().isOpaqueCube(world.getBlockState(pos.add(-1, 0, 0))) && world.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.AIR;
//        boolean blockRight = world.getBlockState(pos.add(1, 0, 0)).getBlock().isOpaqueCube(world.getBlockState(pos.add(1, 0, 0))) && world.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.AIR;
//        boolean day = world.getWorldTime() < 13665 || world.getWorldTime() > 22310L;
//        return !day  ? world.getLightBrightness(pos) == 0 : world.getLightBrightness(pos) >= 0.27 ;
        return true;
    }

    // TODO: Implement lights: http://www.growweedeasy.com/cannabis-grow-lights
    // TODO: Main site used: http://www.growweedeasy.com/cannabis-sunlight-and-light-requirements

    /*
        Photoperiod dependent strains vs. auto-flowering strains

        So all strains of cannabis that respond to light in this way (where the light period effects what stage they're in) are called "Photoperiod dependent" strains.

        "Auto-flowering" marijuana strains pretty much ignore how much light they get each day. Generally you don't run into these unless you buy them particularly from a cannabis seed bank.
     */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void interact(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() != this) return;

        boolean isCancelled = event.isCanceled();

        event.setCanceled(true);

        if (isCancelled) return;

        int oldGrowthStage = event.getWorld().getBlockState(event.getPos()).getValue(AGE);

        if (oldGrowthStage == 7) {
            NonNullList<ItemStack> drops = NonNullList.create();
            getDrops(drops, event.getWorld(), event.getPos(), event.getWorld().getBlockState(event.getPos()), 0);
            drops.forEach(stack -> spawnAsEntity(event.getWorld(), event.getPos(), stack));
        }

        boolean holdingHoe = event.getEntityPlayer().getHeldItem(event.getHand()).getItem().getRegistryName().toString().contains("_hoe");

        int growthStage = oldGrowthStage == 7 || holdingHoe ? random.nextInt(1) : oldGrowthStage;
        event.getWorld().setBlockState(event.getPos(), this.withAge(growthStage).withProperty(FEMALE, event.getWorld().getBlockState(event.getPos()).getValue(FEMALE)), 2);

        if (holdingHoe && growthStage > 1) {
            event.getEntityPlayer().getHeldItem(event.getHand()).setItemDamage(event.getEntityPlayer().getHeldItem(event.getHand()).getItemDamage() + 1);
            event.getEntityPlayer().inventoryContainer.detectAndSendChanges();
        }
    }
}
