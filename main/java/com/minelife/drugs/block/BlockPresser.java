package com.minelife.drugs.block;

public class BlockPresser {

}

//public class BlockPresser extends BlockBCTile_Neptune {
//
//    public BlockPresser() {
//        super(Material.IRON, null);
//        setRegistryName(Minelife.MOD_ID, "presser");
//        setUnlocalizedName(Minelife.MOD_ID + ":presser");
//        setHardness(3);
//        setResistance(15);
//        setCreativeTab(CreativeTabs.MISC);
//    }
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        if(worldIn.isRemote) return false;
//        playerIn.openGui(Minelife.getInstance(), DrugsGuiHandler.PRESSER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
//        return false;
//    }
//
//    public EnumBlockRenderType getRenderType(IBlockState state)
//    {
//        return EnumBlockRenderType.MODEL;
//    }
//
//    @SideOnly(Side.CLIENT)
//    public void registerModel(ItemModelMesher mesher) {
//        Item item = Item.getItemFromBlock(this);
//        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":presser", "inventory");
//        ModelLoader.registerItemVariants(item, model);
//        mesher.register(item, 0, model);
//    }
//
//    @Nullable
//    @Override
//    public TileBC_Neptune createTileEntity(World world, IBlockState iBlockState) {
//        return new TileEntityPresser();
//    }
//}
