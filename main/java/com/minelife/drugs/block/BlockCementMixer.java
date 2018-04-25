package com.minelife.drugs.block;

public class BlockCementMixer {

}

//public class BlockCementMixer extends BlockBCTile_Neptune {
//
//    public BlockCementMixer() {
//        super(Material.IRON, null);
//        setRegistryName(Minelife.MOD_ID, "cement_mixer");
//        setUnlocalizedName(Minelife.MOD_ID + ":cement_mixer");
//        setHardness(3);
//        setResistance(15);
//        setCreativeTab(CreativeTabs.MISC);
//    }
//
//    @Override
//    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
//        worldIn.setBlockToAir(pos);
//        worldIn.setBlockState(pos.add(0, 1, 0), this.getDefaultState(), 2);
//    }
//
//    @Override
//    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
//        return worldIn.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.AIR;
//    }
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        if(worldIn.isRemote) return false;
//        playerIn.openGui(Minelife.getInstance(), DrugsGuiHandler.CEMENT_MIXER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
//        return false;
//    }
//
//    @Nullable
//    @Override
//    public TileBC_Neptune createTileEntity(World world, IBlockState iBlockState) {
//        return new TileEntityCementMixer();
//    }
//
//    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.TRANSLUCENT;
//    }
//
//    @Override
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.INVISIBLE;
//    }
//
//    @Override
//    public boolean isOpaqueCube(IBlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
//        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/cement_mixer.png");
//        return true;
//    }
//
//    @Override
//    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
//        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/cement_mixer.png");
//        return true;
//    }
//}
