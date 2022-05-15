package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts.Impl;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHousePart;
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

import java.util.Map;
import java.util.Optional;

public class CapabilityGreenHouseParts implements IInfSerializableCapabilityImplementation<LevelChunk, Impl> {
    private static final CapabilityGreenHouseParts INSTANCE = new CapabilityGreenHouseParts();

    public static CapabilityGreenHouseParts getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE_DATA);

    public static final Capability<CapabilityGreenHouseParts.Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGreenHouseParts() {}

    public static void addPart(LevelChunk chunk, GreenHousePart part) {
        getInstance().getCapability(chunk).ifPresent(impl -> impl.add(part));
    }

    public static Optional<GreenHousePart> getPart(Level world, ChunkPos pos, int id) {
        return getInstance().getCapability(world.getChunk(pos.x, pos.z))
                .map(o -> o)
                .flatMap(impl -> impl.getPartHolder(id));
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
        private final ChunkPos chunk;
        private final Map<Integer, GreenHousePart> greenhouses;

        protected Impl(LevelChunk chunk) {
            this.chunk = chunk.getPos();
            this.greenhouses = Maps.newHashMap();
        }

        public Optional<GreenHousePart> getPartHolder(int id) {
            return Optional.ofNullable(this.greenhouses.get(id));
        }

        public ChunkPos getChunkPos() {
            return this.chunk;
        }

        protected void add(GreenHousePart part) {
            this.greenhouses.put(part.getId(), part);
        }

        protected void remove(int id) {
            this.greenhouses.remove(id);
        }

        @Override
        public void copyDataFrom(Impl from) {
            this.greenhouses.clear();
            from.greenhouses.forEach((id, greenhouse) -> this.greenhouses.put(id.intValue(), greenhouse));
        }

        @Override
        public CompoundTag serializeNBT() {
            ListTag entries = new ListTag();
            this.greenhouses.forEach((id, part) -> entries.add(part.writeToTag()));
            CompoundTag tag = new CompoundTag();
            tag.put(AgriNBT.ENTRIES, entries);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.greenhouses.clear();
            AgriNBT.stream(tag, AgriNBT.ENTRIES).forEach(entry -> {
                GreenHousePart part = new GreenHousePart(this.getChunkPos(), entry);
                this.greenhouses.put(part.getId(), part);
            });
        }
    }
}
