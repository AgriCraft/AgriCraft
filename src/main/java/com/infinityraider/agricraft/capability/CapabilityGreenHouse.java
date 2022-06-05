package com.infinityraider.agricraft.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouse.Impl;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouse;
import com.infinityraider.agricraft.content.world.greenhouse.GreenHouseConfiguration;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CapabilityGreenHouse implements IInfSerializableCapabilityImplementation<Level, Impl> {
    private static final CapabilityGreenHouse INSTANCE = new CapabilityGreenHouse();

    public static CapabilityGreenHouse getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE);

    public static final Capability<CapabilityGreenHouse.Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGreenHouse() {}

    public static boolean isInGreenHouse(Level world, BlockPos pos) {
        return getGreenHouse(world, pos).map(greenHouse -> greenHouse.isInside(world, pos)).orElse(false);
    }

    public static Optional<GreenHouse> getGreenHouse(Level world, int id) {
        return getInstance().getCapability(world).map(o -> o).flatMap(impl -> impl.getGreenHouse(id));
    }

    public static Optional<GreenHouse> getGreenHouse(Level world, BlockPos pos) {
        return getInstance().getCapability(world).map(o -> o).flatMap(impl -> impl.getGreenHouse(world, pos));
    }

    public static Optional<GreenHouse> addGreenHouse(Level world, GreenHouseConfiguration configuration) {
        return getInstance().getCapability(world).map(impl -> impl.addGreenHouse(world, configuration));
    }

    public static void removeGreenHouse(Level world, int id) {
        getInstance().getCapability(world).ifPresent(impl ->
                impl.getGreenHouse(id).ifPresent(greenHouse -> {
                    if (!greenHouse.isRemoved()) {
                        // call the remove method on the greenhouse and return to avoid infinite loops
                        greenHouse.remove(world);
                    } else if (greenHouse.isEmpty()) {
                        // if all parts have been cleaned up, we can now safely remove the greenhouse from the capability
                        impl.removeGreenHouse(id);
                    }
                })
        );
    }

    public static void onBlockUpdated(Level world, BlockPos pos) {
        getGreenHouse(world, pos).ifPresent(greenHouse -> greenHouse.onBlockUpdated(world, pos));
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

        protected GreenHouse addGreenHouse(Level world, GreenHouseConfiguration config) {
            int id = this.getNextId();
            GreenHouse greenHouse = new GreenHouse(world, id, config);
            this.greenHouses.put(id, greenHouse);
            return greenHouse;
        }

        protected Optional<GreenHouse> getGreenHouse(Level world, BlockPos pos) {
            return this.greenHouses.values().stream()
                    .filter(greenHouse -> greenHouse.isPartOf(world, pos))
                    .findFirst();
        }

        protected Optional<GreenHouse> getGreenHouse(int id) {
            return Optional.ofNullable(this.greenHouses.get(id));
        }

        protected void removeGreenHouse(int id) {
            if(this.greenHouses.remove(id) != null) {
                if(nextId == id + 1) {
                    nextId = id;
                } else {
                    this.free.add(id);
                }
            }
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
