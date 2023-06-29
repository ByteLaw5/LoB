package com.dpeter99.lob

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.SectionPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.RegistryOps
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.FixedBiomeSource
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Predicate


class LobChunkGenerator(val biomeRegistry : HolderLookup.RegistryLookup<Biome>) :
    ChunkGenerator(FixedBiomeSource(biomeRegistry.get(Registration.LOB_BIOME).orElseThrow())) {

    companion object{
        val CODEC: Codec<LobChunkGenerator> = RecordCodecBuilder.create {
            it.group(
                RegistryOps.retrieveRegistryLookup(Registries.BIOME).forGetter(LobChunkGenerator::biomeRegistry.getter)
            ).apply(it, ::LobChunkGenerator)
        }
    }

    init {
    }

    override fun applyBiomeDecoration(p_223087_: WorldGenLevel, p_223088_: ChunkAccess, p_223089_: StructureManager) {
        return;
    }

    override fun createStructures(
        pRegistryAccess: RegistryAccess,
        pStructureState: ChunkGeneratorStructureState,
        pStructureManager: StructureManager,
        pChunk: ChunkAccess,
        pStructureTemplateManager: StructureTemplateManager
    ) {
        //
        //super.createStructures(pRegistryAccess, pStructureState, pStructureManager, pChunk, pStructureTemplateManager)


        pStructureState.possibleStructureSets().forEach {
            val structure = it.get().structures[0].structure.get()

            val res = structure.generate(
                pRegistryAccess,
                this,
                this.biomeSource,
                pStructureState.randomState(),
                pStructureTemplateManager,
                0,
                pChunk.pos,
                0,
                pChunk.heightAccessorForGeneration,
                Predicate { return@Predicate true; }
            );

            val pSectionPos = SectionPos.bottomOf(pChunk)

            if(!res.isValid)
                LobMod.LOGGER.debug("invalid placemnet")
            else
                pStructureManager.setStartForStructure(pSectionPos, structure, res, pChunk)
        }

    }

    override fun codec(): Codec<out ChunkGenerator> {
        return CODEC
    }

    override fun applyCarvers(p_223043_: WorldGenRegion,
                              p_223044_: Long,
                              p_223045_: RandomState,
                              p_223046_: BiomeManager,
                              p_223047_: StructureManager,
                              p_223048_: ChunkAccess,
                              p_223049_: GenerationStep.Carving
    ) {

    }

    override fun buildSurface(p_223050_: WorldGenRegion, p_223051_: StructureManager, p_223052_: RandomState, chunk: ChunkAccess) {
        val stone = Blocks.STONE.defaultBlockState()

        val pos = BlockPos.MutableBlockPos();

        for (x in 0 until 16){
            for (y in 0 until 16){
                chunk.setBlockState(pos.set(x,0,y), stone, false);
            }
        }

        //chunk.setBlockState(BlockPos(0, 0, 0), stone, false);
    }

    override fun spawnOriginalMobs(p_62167_: WorldGenRegion) {

    }

    override fun getGenDepth(): Int {
        return 256
    }

    override fun fillFromNoise(p_223209_: Executor, p_223210_: Blender, p_223211_: RandomState, p_223212_: StructureManager, chunkAccess: ChunkAccess): CompletableFuture<ChunkAccess> {
        return CompletableFuture.completedFuture(chunkAccess)
    }

    override fun getSeaLevel(): Int {
        return 0
    }

    override fun getMinY(): Int {
        return 0
    }

    override fun getBaseHeight(p_223032_: Int, p_223033_: Int, p_223034_: Heightmap.Types, p_223035_: LevelHeightAccessor, p_223036_: RandomState): Int {
        return 99
    }

    override fun getBaseColumn(x: Int, z: Int, levelHeightAccessor: LevelHeightAccessor, randomState: RandomState): NoiseColumn {
        val y = getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, levelHeightAccessor, randomState)
        val stone: BlockState = Blocks.STONE.defaultBlockState()
        val states = arrayOfNulls<BlockState>(y)
        states[0] = Blocks.BEDROCK.defaultBlockState()
        for (i in 1 until y) {
            states[i] = stone
        }
        return NoiseColumn(levelHeightAccessor.minBuildHeight, states)
    }

    override fun addDebugScreenInfo(p_223175_: MutableList<String>, p_223176_: RandomState, p_223177_: BlockPos) {

    }


}