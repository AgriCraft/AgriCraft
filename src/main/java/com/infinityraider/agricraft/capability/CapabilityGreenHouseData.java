package com.infinityraider.agricraft.capability;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseData.Impl;
import com.infinityraider.agricraft.content.world.GreenHouse;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;
import java.util.Set;

public class CapabilityGreenHouseData implements IInfSerializableCapabilityImplementation<LevelChunk, Impl> {
    private static final CapabilityGreenHouseData INSTANCE = new CapabilityGreenHouseData();

    public static CapabilityGreenHouseData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE);

    public static final Capability<CapabilityGreenHouseData.Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGreenHouseData() {}

    public void addGreenHouse(Level world, GreenHouse greenHouse) {
        greenHouse.getChunks().stream()
                .map((pos) -> world.getChunk(pos.x, pos.z))
                .map(this::getCapability)
                .map(LazyOptional::resolve)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(impl -> impl.add(greenHouse));
    }

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
        private final LevelChunk chunk;

        private final Set<GreenHouse.Part> greenhouses;

        protected Impl(LevelChunk chunk) {
            this.chunk = chunk;
            this.greenhouses = Sets.newIdentityHashSet();
        }

        public LevelChunk getChunk() {
            return this.chunk;
        }

        public ChunkPos getChunkPos() {
            return this.getChunk().getPos();
        }

        protected void add(GreenHouse greenHouse) {
            this.greenhouses.add(greenHouse.getPart(this.getChunkPos()));
        }

        @Override
        public void copyDataFrom(Impl from) {
            this.greenhouses.clear();
            this.greenhouses.addAll(from.greenhouses);
        }

        @Override
        public CompoundTag serializeNBT() {
            ListTag entries = new ListTag();
            this.greenhouses.stream()
                    .map(GreenHouse.Part::writeToTag)
                    .forEach(entries::add);
            CompoundTag tag = new CompoundTag();
            tag.put(AgriNBT.ENTRIES, entries);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            // TODO
        }
    }
}
