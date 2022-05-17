package com.infinityraider.agricraft.content.world.greenhouse;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.capability.CapabilityGreenHouse;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GreenHouse {
    private int id;

    private final Map<ChunkPos, PartCache> parts;
    private final GreenHouseProperties properties;
    private GreenHouseState state;

    public GreenHouse( Level world, int id, GreenHouseConfiguration configuration) {
        this.id = id;
        this.parts = configuration.parts().collect(Collectors.toMap(
                Tuple::getA,
                t -> new PartCache(id, t.getA()).initialize(new GreenHousePart(id, world.getChunk(t.getA().x, t.getA().z), t.getB()))
        ));
        this.properties = configuration.getProperties();
        this.state = this.getProperties().hasSufficientGlass() ? GreenHouseState.COMPLETE : GreenHouseState.INSUFFICIENT_GLASS;
    }

    public GreenHouse(CompoundTag tag) {
        // read id
        this.id = tag.getInt(AgriNBT.KEY);
        // read chunks
        this.parts = Maps.newHashMap();
        tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                .filter(chunkTag -> chunkTag instanceof CompoundTag)
                .map(chunkTag -> (CompoundTag) chunkTag)
                .forEach(chunkTag -> {
                    ChunkPos pos = AgriNBT.readChunkPos1(chunkTag);
                    boolean removed = chunkTag.contains(AgriNBT.REMOVED) && chunkTag.getBoolean(AgriNBT.REMOVED);
                    this.parts.put(pos, new PartCache(this.id, pos, removed));
                });
        // read properties
        this.properties = new GreenHouseProperties(tag.getCompound(AgriNBT.CONTENTS));
        // read state
        this.state = GreenHouseState.values()[tag.getInt(AgriNBT.FLAG)];
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GreenHouseState getState() {
        return this.state;
    }

    protected void resetState() {
        if(!this.isRemoved()) {
            GreenHouseState state = this.getProperties().getState();
            if (state != this.getState()) {
                this.state = state;
                // TODO: notify parts
            }
        }
    }

    public boolean hasGaps() {
        return this.getProperties().hasGaps();
    }

    public boolean isRemoved() {
        return this.getState().isRemoved();
    }

    public boolean isEmpty() {
        return this.parts.isEmpty();
    }

    public void remove(Level world) {
        if(world.isClientSide()) {
            return;
        }
        this.state = GreenHouseState.REMOVED;
        this.parts.entrySet().removeIf(entry -> entry.getValue().remove(world));
        if(this.isEmpty()) {
            CapabilityGreenHouse.removeGreenHouse(world, this.getId());
        }
    }

    public void onChunkLoaded(Level world, ChunkPos pos) {
        if(world.isClientSide()) {
            return;
        }
        PartCache cache = this.parts.get(pos);
        if(cache != null) {
            cache.onLoad();
            // clean up action in case this greenhouse had been removed
            if(this.isRemoved() && cache.remove(world)) {
                this.parts.remove(pos);
                if(this.isEmpty()) {
                    CapabilityGreenHouse.removeGreenHouse(world, this.getId());
                }
            }
        }
    }

    public void onChunkUnloaded(ChunkPos pos) {
        PartCache cache = this.parts.get(pos);
        if(cache != null) {
            cache.unLoad();
        }
    }

    protected GreenHouseProperties getProperties() {
        return this.properties;
    }

    public BlockPos getMin() {
        return this.getProperties().getMin();
    }

    public BlockPos getMax() {
        return this.getProperties().getMax();
    }

    public Optional<GreenHousePart> getPart(Level world, BlockPos pos) {
        return this.getPart(world, new ChunkPos(pos));
    }

    public Optional<GreenHousePart> getPart(Level world, ChunkPos pos) {
        return Optional.ofNullable(this.parts.getOrDefault(pos, null)).map(cache -> cache.getPart(world));
    }

    public GreenHouseBlockType getType(Level world, BlockPos pos) {
        // check if the position is in range of the greenhouse first, as it is cheaper
        if(this.isInRange(pos)) {
            return this.getPart(world, pos).map(part -> part.getType(pos)).orElse(GreenHouseBlockType.EXTERIOR);
        }
        return GreenHouseBlockType.EXTERIOR;
    }

    public boolean isInside(Level world, BlockPos pos) {
        return this.getType(world, pos).isInterior();
    }

    public boolean isPartOf(Level world, BlockPos pos) {
        return !this.getType(world, pos).isExterior();
    }

    public void convertAirBlocks(Level world) {
        this.parts.values().forEach(cache -> {
            GreenHousePart part = cache.getPart(world);
            if (part != null) {
                part.replaceAirBlocks(world, this.getState());
            }
        });
    }

    protected void incrementCeilingGlassCount() {
        this.getProperties().incrementCeilingGlassCount();
        this.checkAndUpdateState();
    }

    protected void decrementCeilingGlassCount() {
        this.getProperties().decrementCeilingGlassCount();
        this.checkAndUpdateState();
    }

    protected void newGap() {
        this.getProperties().addGap();
        this.resetState();
    }

    protected void removeGap() {
        this.getProperties().removeGap();
        this.resetState();
    }

    protected void checkAndUpdateState() {
        if(!this.isRemoved() && !this.hasGaps()) {
            this.state = this.getProperties().hasSufficientGlass() ? GreenHouseState.COMPLETE : GreenHouseState.INSUFFICIENT_GLASS;
        }
    }

    protected boolean isInRange(BlockPos pos) {
        return pos.getX() >= this.getMin().getX() && pos.getX() <= this.getMax().getX()
                && pos.getY() >= this.getMin().getY() && pos.getY() <= this.getMax().getY()
                && pos.getZ() >= this.getMin().getZ() && pos.getZ() <= this.getMax().getZ();
    }

    public void onBlockUpdated(Level world, BlockPos pos) {
        this.getPart(world, pos).ifPresent(part -> part.onBlockUpdated(world, pos, this));
    }

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        // write ID
        tag.putInt(AgriNBT.KEY, this.id);
        // write chunks
        ListTag chunks = new ListTag();
        this.parts.forEach((pos, cache) -> {
            CompoundTag chunkTag = new CompoundTag();
            AgriNBT.writeChunkPos1(chunkTag, pos);
            chunkTag.putBoolean(AgriNBT.REMOVED, cache.isRemoved());
            chunks.add(chunkTag);
        });
        tag.put(AgriNBT.ENTRIES, chunks);
        // write properties
        tag.put(AgriNBT.CONTENTS, this.getProperties().writeToNBT());
        // write state
        tag.putInt(AgriNBT.FLAG, this.getState().ordinal());
        return tag;
    }

    private static final class PartCache {
        private final int id;
        private final ChunkPos pos;

        private GreenHousePart cached;
        private boolean loaded;
        private boolean removed;

        private PartCache(int id, ChunkPos pos) {
            this(id, pos, false);
        }

        private PartCache(int id, ChunkPos pos, boolean removed) {
            this.id = id;
            this.pos = pos;
            this.removed = removed;
        }

        public boolean remove(Level world) {
            if(this.isRemoved()) {
                return true;
            }
            if(this.isLoaded()) {
                CapabilityGreenHouseParts.removePart(world.getChunk(this.pos.x, this.pos.z), this.id).ifPresent(part ->
                        part.onRemoved(world));
                this.removed = true;
                this.cached = null;
                return true;
            }
            return false;
        }

        public GreenHousePart getPart(Level world) {
            if(this.isLoaded()) {
                if(this.cached == null) {
                    this.cached = CapabilityGreenHouseParts.getPart(world, this.pos, this.id).orElse(null);
                }
                return this.cached;
            }
            return null;
        }

        public boolean isRemoved() {
            return this.removed;
        }

        public boolean isLoaded() {
            return this.loaded;
        }

         public void unLoad() {
             this.cached = null;
             this.loaded = false;
         }

         public void onLoad() {
            this.loaded = true;
         }

         private PartCache initialize(GreenHousePart part) {
            this.cached = part;
            this.loaded = true;
            return this;
         }
    }


}
