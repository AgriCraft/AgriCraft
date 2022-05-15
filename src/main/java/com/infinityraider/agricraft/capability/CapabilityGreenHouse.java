package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouse.Impl;
import com.infinityraider.agricraft.content.world.GreenHouse;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CapabilityGreenHouse implements IInfSerializableCapabilityImplementation<Level, Impl> {
    private static final CapabilityGreenHouse INSTANCE = new CapabilityGreenHouse();

    public static CapabilityGreenHouse getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE);

    public static final Capability<CapabilityGreenHouse.Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGreenHouse() {}

    public static boolean addGreenHouse(Level world, GreenHouse greenHouse) {
        return getInstance().getCapability(world).map(impl -> {
            // Fetch all chunk capabilities to add the greenhouse to
            List<CapabilityGreenHouseParts.Impl> chunks = greenHouse.getChunks().stream()
                    .map(pos -> world.getChunk(pos.x, pos.z))
                    .map(chunk -> CapabilityGreenHouseParts.getInstance().getCapability(chunk))
                    .map(opt -> opt.map(o -> o))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            // If not all chunks are valid, we do not add the greenhouse
            if(chunks.size() != greenHouse.getChunks().size()) {
                return false;
            }
            // Let's add the greenhouse
            impl.addGreenHouse(greenHouse);
            chunks.forEach(chunk -> chunk.add(greenHouse));
            return true;
        }).orElse(false);
    }

    public static void onChunkLoad(Level world, ChunkPos pos) {
        getInstance().getCapability(world).ifPresent(impl -> impl.onChunkLoad(world, pos));
    }

    public static void onChunkUnload(Level world, ChunkPos pos) {
        getInstance().getCapability(world).ifPresent(impl -> impl.onChunkUnload(pos));
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
    public boolean shouldApplyCapability(Level carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(Level carrier) {
        return new Impl();
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<Level> getCarrierClass() {
        return Level.class;
    }

    public static class Impl implements Serializable<Impl> {
        private final Map<Integer, GreenHouse> greenHouses;
        private final List<Integer> free;
        private int nextId;

        protected Impl() {
            this.greenHouses = Maps.newConcurrentMap();
            this.free = Lists.newArrayList();
            this.nextId = 0;
        }

        protected void addGreenHouse(GreenHouse greenHouse) {
            if(greenHouse.getId() < 0) {
                this.removeGreenHouse(greenHouse.getId());
            }
            greenHouse.setId(this.getNextId());
            this.greenHouses.put(greenHouse.getId(), greenHouse);
        }
        protected void onChunkLoad(Level world, ChunkPos pos) {
            this.greenHouses.values().forEach(greenHouse -> greenHouse.onChunkLoaded(world, pos));
        }

        protected void onChunkUnload(ChunkPos pos) {
            this.greenHouses.values().forEach(greenHouse -> greenHouse.onChunkUnloaded(pos));
        }

        private int getNextId() {
            if(this.free.isEmpty()) {
                int next = this.nextId;
                this.nextId++;
                return next;
            } else {
                return this.free.remove(this.free.size() - 1);
            }
        }

        public void removeGreenHouse(int id) {
            if(this.greenHouses.remove(id) != null) {
                if(nextId == id + 1) {
                    nextId = id;
                } else {
                    this.free.add(id);
                }
            }
        }

        @Override
        public void copyDataFrom(Impl from) {
            this.greenHouses.clear();
            from.greenHouses.forEach((i, gh) -> this.greenHouses.put(i.intValue(), gh));
            this.free.clear();
            from.free.forEach(i -> this.free.add(i.intValue()));
            this.nextId = from.nextId;
        }

        @Override
        public CompoundTag serializeNBT() {
            // create new tag
            CompoundTag tag = new CompoundTag();
            // write greenhouses
            ListTag ghTag = new ListTag();
            this.greenHouses.values().stream()
                    .map(GreenHouse::writeToNBT)
                    .forEach(ghTag::add);
            tag.put(AgriNBT.ENTRIES, ghTag);
            // write free values
            int[] free = new int[this.free.size()];
            int index = 0;
            for (int i : this.free) {
                free[index] = i;
                index++;
            }
            tag.putIntArray(AgriNBT.FREE, free);
            // write next id
            tag.putInt(AgriNBT.KEY, this.nextId);
            // return the tag
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            // read greenhouses
            this.greenHouses.clear();
            tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                    .filter(ghTag -> ghTag instanceof CompoundTag)
                    .map(ghTag -> (CompoundTag) ghTag)
                    .map(GreenHouse::new)
                    .forEach(greenHouse -> this.greenHouses.put(greenHouse.getId(), greenHouse));
            // read free
            this.free.clear();
            Arrays.stream(tag.getIntArray(AgriNBT.FREE)).forEach(this.free::add);
            // read next
            this.nextId = tag.getInt(AgriNBT.KEY);
        }
    }
}
