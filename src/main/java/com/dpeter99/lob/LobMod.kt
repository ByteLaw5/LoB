package com.dpeter99.lob

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.joml.Vector2i


/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(LobMod.ID)
object LobMod {
    const val ID = "lob"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    fun resource(name:String): ResourceLocation{
        return ResourceLocation(ID, name);
    }

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        Registration.init();

        // Register the KDeferredRegister to the mod-specific event bus
        //ModBlocks.REGISTRY.register(MOD_BUS)

    }

    fun attachCapToChunk(event: AttachCapabilitiesEvent<LevelChunk>){
        LOGGER.error("asdasd");

        val provider: ICapabilityProvider = GridGenerationDataStoreProvider(Vector2i(event.`object`.pos.x,event.`object`.pos.z))

        event.addCapability(resource("grid-data-store"), provider);
    }
}

