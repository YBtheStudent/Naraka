package com.yummy.naraka.world.structure;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

public class JumboStructure extends Structure {
    public static final MapCodec<JumboStructure> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    settingsCodec(instance),
                    Codec.STRING.fieldOf("name").forGetter(structure -> structure.name),
                    Heightmap.Types.CODEC.fieldOf("heightmap_type").forGetter(structure -> structure.heightmapType),
                    JumboPart.CODEC.codec().listOf().fieldOf("parts").forGetter(structure -> structure.parts),
                    StructurePieceFactory.CODEC.listOf()
                            .fieldOf("custom_pieces")
                            .forGetter(structure -> structure.customPieces),
                    BlockPos.CODEC.fieldOf("structure_offset").forGetter(structure -> structure.structureOffset)
            ).apply(instance, JumboStructure::new)
    );

    private final String name;
    private final Heightmap.Types heightmapType;
    private final List<JumboPart> parts;
    private final List<Holder<StructurePieceFactory>> customPieces;
    private final BlockPos structureOffset;

    public JumboStructure(StructureSettings settings, String name, Heightmap.Types heightmapType, List<JumboPart> parts, List<Holder<StructurePieceFactory>> customPieces, BlockPos structureOffset) {
        super(settings);
        this.name = name;
        this.heightmapType = heightmapType;
        this.parts = parts;
        this.customPieces = customPieces;
        this.structureOffset = structureOffset;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int height = context.chunkGenerator().getFirstFreeHeight(
                chunkPos.x, chunkPos.z,
                heightmapType,
                context.heightAccessor(),
                context.randomState()
        );
        BlockPos base = new BlockPos(chunkPos.getMinBlockX(), height, chunkPos.getMinBlockZ());
        return addPieces(context, base.offset(structureOffset));
    }

    protected Optional<GenerationStub> addPieces(GenerationContext context, BlockPos basePos) {
        StructureTemplateManager templateManager = context.structureTemplateManager();
        return Optional.of(new GenerationStub(basePos, builder -> {
            for (Holder<StructurePieceFactory> holder : customPieces) {
                StructurePieceFactory structurePieceFactory = holder.value();
                StructurePiece structurePiece = structurePieceFactory.create(basePos);
                builder.addPiece(structurePiece);
            }
            for (JumboPart part : parts)
                addPart(templateManager, builder, part, basePos);
        }));
    }

    private void addPart(StructureTemplateManager templateManager, StructurePiecesBuilder builder, JumboPart part, BlockPos basePos) {
        for (Vec3i piecePosition : part.getPositions()) {
            ResourceLocation location = part.getPieceLocation(name, piecePosition);
            LogUtils.getLogger().debug("Add {}", location);
            BlockPos actualPosition = basePos.offset(part.getPiecePosition(piecePosition));
            builder.addPiece(new JumboPiece(templateManager, location, actualPosition));
        }
    }

    @Override
    public StructureType<?> type() {
        return NarakaStructureTypes.JUMBO.get();
    }
}
