package com.dpeter99.lob

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
import java.util.*

class GridStructure(pSettings: StructureSettings,
                    var startPool: Holder<StructureTemplatePool>,
                    var startJigsawName: Optional<ResourceLocation>) : Structure(pSettings) {

    companion object{
        val CODEC :Codec<GridStructure> = RecordCodecBuilder.create {
            it.group(
                settingsCodec(it),
                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(GridStructure::startPool.getter),
                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(GridStructure::startJigsawName.getter),
            ).apply(it, ::GridStructure)
        }
    }

    override fun findGenerationPoint(pContext: GenerationContext): Optional<GenerationStub> {
        val startY: Int = 0

        val chunkPos: ChunkPos = pContext.chunkPos()
        val blockPos = BlockPos(chunkPos.minBlockX, startY, chunkPos.minBlockZ)

        val placement = JigsawPlacement.addPieces(
            pContext,
            this.startPool,
            this.startJigsawName,
            1,
            blockPos,
            false,
            Optional.empty(),
            0
        )

        return Optional.of(GenerationStub(blockPos){
            Optional.of(generatePieces(it, pContext))
        });
    }

    private fun generatePieces(pBuilder: StructurePiecesBuilder, pContext: GenerationContext): GridStructurePiece {
        val chunkpos = pContext.chunkPos()
        val worldgenrandom = pContext.random()
        val blockpos = BlockPos(chunkpos.minBlockX, 0, chunkpos.minBlockZ)
        val rotation = Rotation.NONE

        return GridStructurePiece(
            pContext.structureTemplateManager,
            LobMod.resource("test_struct"),
            blockpos,
            rotation,
            0)
    }

    override fun type(): StructureType<*> {
        return Registration.GRID_STRUCTURE.get();
    }

}

class GridStructurePiece : TemplateStructurePiece {

    constructor(pContext: StructurePieceSerializationContext, pTag: CompoundTag) :
            super(Registration.GRID_STRUCTURE_PIECE.get(), pTag, pContext.structureTemplateManager, {StructurePlaceSettings()}) {
    }

    constructor(pStructureTemplateManager: StructureTemplateManager,
                pLocation: ResourceLocation,
                pStartPos: BlockPos,
                pRotation: Rotation,
                pDown: Int): super(Registration.GRID_STRUCTURE_PIECE.get(),
        0,
        pStructureTemplateManager,
        pLocation,
        pLocation.toString(),
        StructurePlaceSettings(),
    pStartPos){

    }

    open fun makeSettings(pLocation: ResourceLocation): StructurePlaceSettings? {
        return StructurePlaceSettings().setMirror(Mirror.NONE)
    }

    override fun handleDataMarker(
        pName: String,
        pPos: BlockPos,
        pLevel: ServerLevelAccessor,
        pRandom: RandomSource,
        pBox: BoundingBox
    ) {

    }

}
/*
class GridPlacement{

    fun place(){
        return Optional.of<GenerationStub>(GenerationStub(
            BlockPos(0, 0, 0)
        ) { piecesBuilder: StructurePiecesBuilder ->
            //piecesBuilder.addPiece(TemplateStructurePiece(
//
            //))
        }
    }

}
*/