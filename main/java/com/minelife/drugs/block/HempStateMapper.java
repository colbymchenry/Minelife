package com.minelife.drugs.block;

import com.minelife.Minelife;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

import java.util.Map;

public class HempStateMapper extends StateMapperBase {

    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(state.getBlock().getRegistryName(), "age=" + state.getValue(BlockHempCrop.AGE) + ",female=" + state.getValue(BlockHempCrop.FEMALE));
    }
}
