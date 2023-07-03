package com.dpeter99.lob

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.CrashReport
import net.minecraft.ReportedException
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
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
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.*
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.StructureStart
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Predicate
import java.util.function.Supplier


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

    override fun applyBiomeDecoration(level: WorldGenLevel, chunk: ChunkAccess, structureManager: StructureManager) {
        //super.applyBiomeDecoration(level, chunk, structureManager);

        //default(chunk, level, structureManager)

        val reg = level.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        val structureSet: StructureSet = reg.get(LobMod.resource("test_room"))!!


        val chunkPos = chunk.pos;
        val blockPos = BlockPos(chunkPos.minBlockX, 0, chunkPos.minBlockZ)

        val context: Structure.GenerationContext = Structure.GenerationContext(
            structureManager.registryAccess(),
            this,
            biomeSource,
            randomState,
            structureTemplateManager,
            WorldgenRandom(level.random),
            0,
            chunkPos,
            chunk.heightAccessorForGeneration,
            Predicate { return@Predicate true; }
        )

        val stub = Optional.of(GenerationStub(blockPos){
            val pos = BlockPos.MutableBlockPos(chunkPos.minBlockX, 0, chunkPos.minBlockZ)
            generatePieces(it, context, pos)
        });

        if (stub.isPresent()) {
            val structurepiecesbuilder: StructurePiecesBuilder = stub.get().getPiecesBuilder()
            val structurestart = StructureStart(structureSet.structures[0].structure.get(), chunkPos, 0, structurepiecesbuilder.build())
            if (structurestart.isValid) {
                structurestart.placeInChunk(
                    level,structureManager,this,level.random,getWritableArea(chunk), chunkPos
                )
            }
        }


    }

    private fun generatePieces(pBuilder: StructurePiecesBuilder, pContext: Structure.GenerationContext, pos: BlockPos): GridStructurePiece {
        val chunkpos = pContext.chunkPos()
        val worldgenrandom = pContext.random()
        //val blockpos = BlockPos(chunkpos.minBlockX, 0, chunkpos.minBlockZ)
        val rotation = Rotation.NONE

        val piece = GridStructurePiece(
            pContext.structureTemplateManager,
            LobMod.resource("test_struct"),
            pos,
            rotation,
            0)
        pBuilder.addPiece(piece)

        return piece
    }

    private fun default(
        chunk: ChunkAccess,
        level: WorldGenLevel,
        structureManager: StructureManager
    ) {
        val chunkPos = chunk.pos
        val sectionPos = SectionPos.of(chunkPos, level.minSection)
        val blockPos = sectionPos.origin()
        val structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE)
        val worldGenRandom = WorldgenRandom(XoroshiroRandomSource(RandomSupport.generateUniqueSeed()))
        val i = worldGenRandom.setDecorationSeed(level.seed, blockPos.x, blockPos.z)
        try {
            if (structureManager.shouldGenerateStructures()) {
                for ((l, structure) in structureRegistry.withIndex()) {
                    worldGenRandom.setFeatureSeed(i, l, GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal)
                    val supplier: Supplier<String> = Supplier {
                        structureRegistry.getResourceKey(structure).map { t -> t.toString() }
                            .orElseGet { structure.toString() }
                    }
                    try {
                        level.setCurrentlyGenerating(supplier)
                        for (structureStart in structureManager.startsForStructure(sectionPos, structure)) {
                            structureStart.placeInChunk(
                                level,
                                structureManager,
                                this,
                                worldGenRandom,
                                getWritableArea(chunk),
                                chunkPos
                            )
                        }
                    } catch (e1: Exception) {
                        val crashReport = CrashReport.forThrowable(e1, "Feature placement")
                        crashReport.addCategory("Feature").setDetail("Description", supplier)
                        throw ReportedException(crashReport)
                    }
                }
            }
        } catch (e: Exception) {
            val crashReport = CrashReport.forThrowable(e, "Biome decoration")
            crashReport.addCategory("Generation").setDetail("CenterX", chunkPos.x).setDetail("CenterZ", chunkPos.z)
                .setDetail("Seed", i)
            throw ReportedException(crashReport)
        }
    }

    private fun getWritableArea(pChunk: ChunkAccess): BoundingBox? {
        val chunkpos = pChunk.pos
        val i = chunkpos.minBlockX
        val j = chunkpos.minBlockZ
        val levelheightaccessor = pChunk.heightAccessorForGeneration
        val k = levelheightaccessor.minBuildHeight + 1
        val l = levelheightaccessor.maxBuildHeight - 1
        return BoundingBox(i, k, j, i + 15, l, j + 15)
    }

    lateinit var registryAccess: RegistryAccess

    lateinit var randomState: RandomState

    lateinit var structureTemplateManager: StructureTemplateManager

    override fun createStructures(
        pRegistryAccess: RegistryAccess,
        pStructureState: ChunkGeneratorStructureState,
        pStructureManager: StructureManager,
        pChunk: ChunkAccess,
        pStructureTemplateManager: StructureTemplateManager
    ) {
        //
        //super.createStructures(pRegistryAccess, pStructureState, pStructureManager, pChunk, pStructureTemplateManager)

        registryAccess = pRegistryAccess;
        randomState = pStructureState.randomState();
        structureTemplateManager = pStructureTemplateManager;

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
            for (y in 0 until 1)
            for (z in 0 until 16){
                //chunk.setBlockState(pos.set(x,y,z), stone, false);
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
        return 1
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