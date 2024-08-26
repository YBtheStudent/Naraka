package com.yummy.naraka.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NectariumCoreBlock extends Block {
    public static final int MAX_HEIGHT = 5;

    public NectariumCoreBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos growingPos = findUpFaceSturdyBlock(level, pos).above();
        if (growingPos.equals(pos) || pos.getY() - growingPos.getY() > MAX_HEIGHT)
            return;

        BlockState crystal = NarakaBlocks.NECTARIUM_CRYSTAL_BLOCK.defaultBlockState();
        level.setBlock(growingPos, crystal, UPDATE_ALL_IMMEDIATE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
    }

    private BlockPos findUpFaceSturdyBlock(ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.below().mutable();
        BlockState searchingState = level.getBlockState(mutableBlockPos);
        while (!searchingState.isFaceSturdy(level, mutableBlockPos, Direction.UP)) {
            mutableBlockPos.move(Direction.DOWN);
            searchingState = level.getBlockState(mutableBlockPos);
        }
        return mutableBlockPos.immutable();
    }
}
