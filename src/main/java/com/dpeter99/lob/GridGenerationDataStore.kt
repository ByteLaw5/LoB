package com.dpeter99.lob

import com.mojang.serialization.Codec
import com.mojang.serialization.Encoder
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.AutoRegisterCapability
import net.minecraftforge.common.util.INBTSerializable
import org.joml.Vector2i

@AutoRegisterCapability
interface IGridGenerationDataStore {

    fun getCellData(pos: Vec3i);

    fun setCellData(pos: Vec3i, data: GridCellData);

}

class GridCellData(var structureName: String) : INBTSerializable<CompoundTag>{

    companion object{
        val CODEC: Codec<GridCellData> = RecordCodecBuilder.create {
            it.group(
                Codec.STRING.fieldOf(GridCellData::structureName.name).forGetter(GridCellData::structureName.getter)
            ).apply(it, ::GridCellData)
        }
    }

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            putString("structure_name", structureName);
        };
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        this.structureName = nbt.get("structure_name")?.asString.toString();
    }

}

class GridGenerationDataStore(pos: Vector2i): IGridGenerationDataStore, INBTSerializable<CompoundTag>{

    var cellData: MutableMap<Int, GridCellData> = HashMap();
    val cellSection : Vector2i = pos;
    override fun getCellData(pos: Vec3i) {
        TODO("Not yet implemented")
    }

    override fun setCellData(pos: Vec3i, data: GridCellData) {
        TODO("Not yet implemented")
    }

    override fun serializeNBT(): CompoundTag {
        val tag = CompoundTag().apply {
            for (d in cellData.entries){
                this.put(d.key.toString(), d.value.serializeNBT());
            }
        }
        return CompoundTag().apply { put("data", tag) }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt["data"]?.let {
            if(it.type == CompoundTag.TYPE){
                val data = it as CompoundTag;
                for (t in data.allKeys){
                    if(data[t]?.type == CompoundTag.TYPE){
                        deserializeCellData(t.toInt(), data[t] as CompoundTag)
                    }

                }
            }
        }
    }

    private fun deserializeCellData(t: Int, data: CompoundTag){

        cellData.put(t, GridCellData("").apply { deserializeNBT(data) })
    }

}