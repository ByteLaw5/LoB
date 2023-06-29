package com.dpeter99.lob

import com.google.common.collect.Lists
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import java.util.*
import java.util.function.Consumer

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

        return placement;
    }

    override fun type(): StructureType<*> {
        return Registration.GRID_STRUCTURE.get();
    }

}


class GridPlacement{

    fun place(){
        return Optional.of<GenerationStub>(GenerationStub(
            BlockPos(0, 0, 0)
        ) { piecesBuilder: StructurePiecesBuilder ->
            //piecesBuilder.addPiece(TemplateStructurePiece(
//
//            ))
        }
    }

}