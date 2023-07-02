package com.dpeter99.lob

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import org.joml.Vector2i

class GridGenerationDataStoreProvider(val pos: Vector2i) : ICapabilitySerializable<CompoundTag> {

    var bacend = GridGenerationDataStore(pos);
    var optionalStore = LazyOptional.of { bacend };

    override fun <T> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
        if(cap == Registration.GRID_GEN_DATA){
            return optionalStore.cast();
        }
        return LazyOptional.empty<T>()
    }

    override fun serializeNBT(): CompoundTag {
        return bacend.serializeNBT()
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        bacend.deserializeNBT(nbt);
    }
}