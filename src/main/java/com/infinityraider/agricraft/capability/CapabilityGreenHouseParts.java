package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts.Impl;
import com.infinityraider.agricraft.content.world.GreenHouse;
import com.infinityraider.agricraft.content.world.GreenHousePartHolder;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Map;

public class CapabilityGreenHouseParts implements IInfSerializableCapabilityImplementation<LevelChunk, Impl> {
    private static final CapabilityGreenHouseParts INSTANCE = new CapabilityGreenHouseParts();

    public static CapabilityGreenHouseParts getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE_DATA);

    public static final Capability<CapabilityGreenHouseParts.Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGreenHouseParts() {}

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(LevelChunk carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(LevelChunk carrier) {
        return new Impl(carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<LevelChunk> getCarrierClass() {
        return LevelChunk.class;
    }

    public static class Impl implements Serializable<Impl> {
        private final ChunkPos chunk;
        private final Map<Integer, GreenHousePartHolder> greenhouses;

        protected Impl(LevelChunk chunk) {
            this.chunk = chunk.getPos();
            this.greenhouses = Maps.newHashMap();
        }

        public GreenHousePartHolder getPartHolder(int id) {
            return this.greenhouses.computeIfAbsent(id, i -> new GreenHousePartHolder(this.getChunkPos()));
        }

        public ChunkPos getChunkPos() {
            return this.chunk;
        }

        protected void add(GreenHouse greenHouse) {
            this.greenhouses.put(greenHouse.getId(), greenHouse.getPart(this.getChunkPos()));
        }

        @Override
        public void copyDataFrom(Impl from) {
            this.greenhouses.clear();
            from.greenhouses.forEach((id, greenhouse) -> this.greenhouses.put(id.intValue(), greenhouse));
        }

        @Override
        public CompoundTag serializeNBT() {
            ListTag entries = new ListTag();
            this.greenhouses.forEach((id, part) -> {
                CompoundTag tag = new CompoundTag();
                tag.putInt(AgriNBT.INDEX, id);
                tag.put(AgriNBT.CONTENTS, part.writeToTag());
                entries.add(tag);
            });
            CompoundTag tag = new CompoundTag();
            tag.put(AgriNBT.ENTRIES, entries);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if(tag.contains(AgriNBT.ENTRIES, Tag.TAG_LIST)) {
                tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                        .filter(entry -> entry instanceof CompoundTag)
                        .map(entry -> (CompoundTag) entry)
                        .forEach(entry -> {
                            GreenHousePartHolder part = this.getPartHolder(entry.getInt(AgriNBT.INDEX));
                            part.readFromTag(entry.getCompound(AgriNBT.CONTENTS));
                        });
            }
        }
    }
}
