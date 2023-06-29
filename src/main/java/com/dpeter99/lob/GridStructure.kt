package com.dpeter99.lob

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
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
            16
        )

        return placement;
    }

    override fun type(): StructureType<*> {
        return Registration.GRID_STRUCTURE.get();
    }

}