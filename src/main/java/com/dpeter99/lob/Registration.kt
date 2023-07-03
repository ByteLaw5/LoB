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
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier


object Registration {

    val CHUNK_GENERATORS: DeferredRegister<Codec<out ChunkGenerator?>> = DeferredRegister.create(Registries.CHUNK_GENERATOR, ID);
    val FEATURES: DeferredRegister<Feature<*>> = DeferredRegister.create(Registries.FEATURE, ID);
    val STRUCTURE_TYPE: DeferredRegister<StructureType<*>> = DeferredRegister.create(Registries.STRUCTURE_TYPE, ID);
    val STRUCTURE_PIECE_TYPES: DeferredRegister<StructurePieceType> = DeferredRegister.create(Registries.STRUCTURE_PIECE, ID);
    val STRUCTURE_PLACEMENT_TYPE: DeferredRegister<StructurePlacementType<*>> = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, ID);

    fun init() {
        CHUNK_GENERATORS.register(MOD_BUS)
        FEATURES.register(MOD_BUS)
        STRUCTURE_TYPE.register(MOD_BUS)
        STRUCTURE_PIECE_TYPES.register(MOD_BUS)
    }

    val MYSTERIOUS_CHUNKGEN = CHUNK_GENERATORS.register("lob_chunkgen", Supplier<Codec<out ChunkGenerator?>> { LobChunkGenerator.CODEC })

    val LOB_BIOME: ResourceKey<Biome> = ResourceKey.create(Registries.BIOME, LobMod.resource("lob_biome"))

    val GRID_STRUCTURE: RegistryObject<StructureType<GridStructure>> =
        STRUCTURE_TYPE.register("grid_structure") {
            explicitStructureTypeTyping(
                GridStructure.CODEC
            )
        }

    val GRID_STRUCTURE_PIECE: RegistryObject<StructurePieceType> = registerPieceType("grid_piece", ::GridStructurePiece);

    val GRID_PLACEMENT: RegistryObject<StructurePlacementType<*>> = STRUCTURE_PLACEMENT_TYPE.register("grid_placement") { GridPlacement.CODEC as StructurePlacementType<*> };


    val GRID_GEN_DATA: Capability<IGridGenerationDataStore> = CapabilityManager.get(object : CapabilityToken<IGridGenerationDataStore>() {})



    private fun <T : Structure> explicitStructureTypeTyping(structureCodec: Codec<T>): StructureType<T> {
        return StructureType { structureCodec }
    }

    private fun registerPieceType(name: String, structurePieceType: StructurePieceType): RegistryObject<StructurePieceType> {
        return STRUCTURE_PIECE_TYPES.register(name.lowercase()) { structurePieceType }
    }
}