package com.dpeter99.lob

import net.minecraft.core.Vec3i
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
import java.util.*

class GridPlacement(pLocateOffset: Vec3i,
                    pFrequencyReductionMethod: FrequencyReductionMethod,
                    pFrequency: Float,
                    pSalt: Int,
                    pExclusionZone: Optional<ExclusionZone>
) : StructurePlacement(pLocateOffset, pFrequencyReductionMethod, pFrequency, pSalt, pExclusionZone) {


    override fun isPlacementChunk(pStructureState: ChunkGeneratorStructureState, pX: Int, pZ: Int): Boolean {
        return true;
    }

    override fun type(): StructurePlacementType<*> {
        TODO("Not yet implemented")
    }

}