package com.yummy.naraka.world.structure;

import com.yummy.naraka.NarakaMod;
import com.yummy.naraka.data.worldgen.NarakaStructures;
import com.yummy.naraka.util.NarakaUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class HerobrineSanctuaryOutline extends StructurePiece {
    public static final float SPHERE_SIZE = 1.6f;
    private static final int STRUCTURE_WIDTH = 48 * 2 + 39;
    private static final int STRUCTURE_HEIGHT = 48 * 4;
    private static final int WIDTH = 48 * 6;
    private static final int HEIGHT = 48 * 6;
    private static final int DEPTH = 24;
    private static final int AIR_DEPTH = 48 * 4;

    private static final BlockPos OFFSET = new BlockPos(
            ((WIDTH - STRUCTURE_WIDTH) / -2) + 1, -DEPTH - 1, ((HEIGHT - STRUCTURE_HEIGHT) / -2) + 1
    ).offset(NarakaStructures.HEROBRINE_SANCTUARY_MAIN_OFFSET);

    private final BlockPos pos;
    private BoundingBox lavaBox;
    private BoundingBox airBox;

    private static BoundingBox createLavaBox(BlockPos pos) {
        return BoundingBox.fromCorners(pos, pos.offset(WIDTH, DEPTH, HEIGHT));
    }

    private static BoundingBox createAirBox(BlockPos pos) {
        pos = pos.offset(0, DEPTH, 0);
        return BoundingBox.fromCorners(pos, pos.offset(WIDTH, AIR_DEPTH, HEIGHT));
    }

    private static BoundingBox createBox(BlockPos pos) {
        return BoundingBox.fromCorners(pos, pos.offset(WIDTH, DEPTH + AIR_DEPTH, HEIGHT));
    }

    public HerobrineSanctuaryOutline(BlockPos pos) {
        super(NarakaStructureTypes.HEROBRINE_SANCTUARY_OUTLINE.get(), 0, createBox(pos.offset(OFFSET)));
        this.pos = pos.offset(OFFSET);
        this.lavaBox = createLavaBox(this.pos);
        this.airBox = createAirBox(this.pos);
        NarakaMod.context().addProtectedArea(getBoundingBox());
    }

    public HerobrineSanctuaryOutline(StructurePieceSerializationContext context, CompoundTag tag) {
        super(NarakaStructureTypes.HEROBRINE_SANCTUARY_OUTLINE.get(), tag);
        this.pos = NbtUtils.readBlockPos(tag, "pos").orElse(BlockPos.ZERO);
        this.lavaBox = createLavaBox(pos);
        this.airBox = createAirBox(pos);
        NarakaMod.context().addProtectedArea(getBoundingBox());
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag tag) {
        tag.put("pos", NbtUtils.writeBlockPos(pos));
    }

    @Override
    public void move(int x, int y, int z) {
        super.move(x, y, z);
        airBox = airBox.moved(x, y, z);
        lavaBox = lavaBox.moved(x, y, z);
    }

    @Override
    public void postProcess(WorldGenLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, RandomSource pRandom, BoundingBox pBox, ChunkPos pChunkPos, BlockPos pPos) {
        generateSphere(pLevel, pBox, boundingBox, airBox.minY(), airBox.maxY(), Blocks.AIR.defaultBlockState(), SPHERE_SIZE);
        generateSphere(pLevel, pBox, boundingBox, lavaBox.minY(), lavaBox.maxY(), Blocks.LAVA.defaultBlockState(), SPHERE_SIZE);
    }

    protected void generateSphere(WorldGenLevel level, BoundingBox boundingBox, BoundingBox box, int yStart, int yEnd, BlockState state, float size) {
        NarakaUtils.sphere(box, size, (x, y, z) -> {
            if (yStart <= y && y <= yEnd)
                placeBlock(level, state, x, y, z, boundingBox);
        });
    }
}
