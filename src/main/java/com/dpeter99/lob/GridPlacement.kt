package com.dpeter99.lob

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
import net.minecraft.world.phys.Vec2
import org.apache.commons.compress.harmony.pack200.Codec
import java.util.*

class GridPlacement(val spacing: Int) :
    StructurePlacement(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1F, 0, Optional.empty()) {

    companion object{
        val CODEC = RecordCodecBuilder.create {
            it.group(
                com.mojang.serialization.Codec.INT.fieldOf("spacing").forGetter(GridPlacement::spacing.getter)
            ).apply(it, ::GridPlacement)
        }
    }

    fun isPlacementGrid(cell: Vec3i): Boolean{
        return  cell.x % 2 == 0 &&
                cell.y % 2 == 0 &&
                cell.z % 2 == 0
    }

    override fun isPlacementChunk(pStructureState: ChunkGeneratorStructureState, pX: Int, pZ: Int): Boolean {
        return true;
    }

    override fun type(): StructurePlacementType<*> {
        TODO("Not yet implemented")
    }

}