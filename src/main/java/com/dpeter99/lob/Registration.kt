package com.dpeter99.lob

import com.dpeter99.lob.LobMod.ID
import com.mojang.serialization.Codec
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureType
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier

object Registration {

    val CHUNK_GENERATORS: DeferredRegister<Codec<out ChunkGenerator?>> = DeferredRegister.create(Registries.CHUNK_GENERATOR, ID);
    val FEATURES: DeferredRegister<Feature<*>> = DeferredRegister.create(Registries.FEATURE, ID);
    val STRUCTURE_TYPE: DeferredRegister<StructureType<*>> = DeferredRegister.create(Registries.STRUCTURE_TYPE, ID);
    val STRUCTURE_PIECE: DeferredRegister<StructurePieceType> = DeferredRegister.create(Registries.STRUCTURE_PIECE, ID);

    fun init() {
        CHUNK_GENERATORS.register(MOD_BUS)
        FEATURES.register(MOD_BUS)
        STRUCTURE_TYPE.register(MOD_BUS)
        STRUCTURE_PIECE.register(MOD_BUS)
    }

    val MYSTERIOUS_CHUNKGEN = CHUNK_GENERATORS.register("lob_chunkgen", Supplier<Codec<out ChunkGenerator?>> { LobChunkGenerator.CODEC })

    val LOB_BIOME: ResourceKey<Biome> = ResourceKey.create(Registries.BIOME, LobMod.resource("lob_biome"))

    /**
     * Registers the base structure itself and sets what its path is. In this case,
     * this base structure will have the resourcelocation of structure_tutorial:sky_structures.
     */
    val GRID_STRUCTURE: RegistryObject<StructureType<GridStructure>> =
        STRUCTURE_TYPE.register("grid_structure") {
            explicitStructureTypeTyping(
                GridStructure.CODEC
            )
        }

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private fun <T : Structure> explicitStructureTypeTyping(structureCodec: Codec<T>): StructureType<T> {
        return StructureType { structureCodec }
    }


}