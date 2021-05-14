package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.handler.IrrigationSystemHandler;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IrrigationLayer {
    private final int id;
    private final World world;

    private double min;
    private double max;
    private int capacity;

    // all positions contained within this layer
    private final Set<BlockPos> positions;
    private final Set<ChunkPos> chunks;
    private final Set<ChunkPos> loadedChunks;

    // connections below
    private Map<BlockPos, Function<BlockPos, IrrigationLayer>> layersBelowMap;
    private final Set<IrrigationLayer> layersBelow;

    // connections above
    private Map<BlockPos, Function<BlockPos, IrrigationLayer>> layersAboveMap;
    private final Set<IrrigationLayer> layersAbove;

    // contents
    private int content;
    private double height;

    public IrrigationLayer(int id, IAgriIrrigationComponent component) {
        this.id = id;
        this.world = component.world();
        this.min = component.getMinFluidHeight();
        this.max = component.getMaxFluidHeight();
        this.capacity =  component.getFluidCapacity();

        this.positions = Sets.newHashSet(component.position());
        this.chunks = Sets.newHashSet();
        this.loadedChunks = Sets.newHashSet();

        this.layersBelowMap = Maps.newHashMap();
        this.layersBelow = Sets.newIdentityHashSet();

        this.layersAboveMap = Maps.newHashMap();
        this.layersAbove = Sets.newIdentityHashSet();

        this.content = 0;
        this.height = this.getMin();
    }

    public final int getId() {
        return this.id;
    }

    public final World getWorld() {
        return this.world;
    }

    public boolean isFullyLoaded() {
        return this.loadedChunks.size() == this.chunks.size();
    }

    public void onChunkLoaded(ChunkPos chunk) {
        // keep track of the amount of chunks loaded
        if(this.chunks.contains(chunk)) {
            this.loadedChunks.add(chunk);
        }
        // load the below layers
        this.layersBelowMap.entrySet().stream()
                .filter(entry ->
                        entry.getKey().getX() >= chunk.getXStart() && entry.getKey().getX() <= chunk.getXEnd()
                        && entry.getKey().getZ() >= chunk.getZStart() && entry.getKey().getZ() <= chunk.getZEnd())
                .map(entry -> entry.getValue().apply(entry.getKey()))
                .forEach(this.layersBelow::add);    // TODO: settle liquid contents
        // load the above layers
        this.layersAboveMap.entrySet().stream()
                .filter(entry ->
                        entry.getKey().getX() >= chunk.getXStart() && entry.getKey().getX() <= chunk.getXEnd()
                                && entry.getKey().getZ() >= chunk.getZStart() && entry.getKey().getZ() <= chunk.getZEnd())
                .map(entry -> entry.getValue().apply(entry.getKey()))
                .forEach(this.layersAbove::add);    // TODO: settle liquid contents
    }

    public boolean onChunkUnloaded(ChunkPos chunk) {
        // kee track of the amount of chunks loaded
        if(this.chunks.contains(chunk)) {
            this.loadedChunks.remove(chunk);
        }
        // return true if this needs to be unloaded
        return this.loadedChunks.isEmpty();
    }

    public void onLayerUnloaded(IrrigationLayer layer) {
        this.layersAbove.remove(layer);
        this.layersBelow.remove(layer);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getContent() {
        return this.content;
    }

    public double getHeight() {
        return this.height;
    }

    protected void setContent(int content) {
        this.content = Math.max(0, Math.min(this.getCapacity(), content));
        this.height = this.calculateHeight(this.getContent());
    }

    protected void setHeight(double height) {
        this.height = Math.max(this.getMin(), Math.min(this.getMax(), height));
        this.content = this.calculateContents(this.getHeight());
    }

    public Set<BlockPos> getPositions() {
        return this.positions;
    }

    public int fill(int maxFill, IFluidHandler.FluidAction action) {
        // check if this is fully loaded
        if(!this.isFullyLoaded()) {
            return 0;
        }
        // try to fill layers below first
        int delta = this.fillBelow(maxFill, action);
        if(delta >= maxFill) {
            return maxFill;
        }
        // fill own layer
        int fill = Math.min(maxFill - delta, this.getCapacity() - this.getContent());
        if(action.execute()) {
            this.setContent(this.getContent() + fill);
        }
        return fill;
    }

    protected int fillBelow(int maxFill, IFluidHandler.FluidAction action) {
        if(maxFill == 0) {
            return 0;
        }
        int remaining = maxFill;
        int divided = maxFill / this.layersBelow.size();
        int delta = 0;
        if(divided == 0) {
            delta = this.layersBelow.stream()
                    .filter(layer -> layer.fill(1, IFluidHandler.FluidAction.SIMULATE) > 0)
                    .mapToInt(layer -> layer.fill(1, action))
                    .sum();
        } else {
            delta = this.layersBelow.stream()
                    .filter(layer -> layer.fill(divided, IFluidHandler.FluidAction.SIMULATE) > 0)
                    .mapToInt(layer -> layer.fill(divided, action))
                    .sum();
        }
        remaining -= delta;
        if(remaining <= 0) {
            return delta;
        }
        if(delta <= 0) {
            return 0;
        }
        return fillBelow(remaining, action);
    }

    public int drain(int maxDrain, IFluidHandler.FluidAction action) {
        // check if this is fully loaded
        if(!this.isFullyLoaded()) {
            return 0;
        }
        // try to drain layers above first
        int delta = this.drainAbove(maxDrain, action);
        if(delta >= maxDrain) {
            return maxDrain;
        }
        // drain own layer
        if(this.getContent() <= 0) {
            return 0;
        }
        int drain =  Math.min(maxDrain, this.getContent());
        if(action.execute()) {
            this.setContent(this.getContent() - drain);
        }
        return drain;
    }

    protected int drainAbove(int maxDrain, IFluidHandler.FluidAction action) {
        if(maxDrain == 0) {
            return 0;
        }
        int remaining = maxDrain;
        int divided = maxDrain / this.layersAbove.size();
        int delta = 0;
        if(divided == 0) {
            delta = this.layersAbove.stream()
                    .filter(layer -> layer.drain(1, IFluidHandler.FluidAction.SIMULATE) > 0)
                    .mapToInt(layer -> layer.drain(1, action))
                    .sum();
        } else {
            delta = this.layersAbove.stream()
                    .filter(layer -> layer.drain(divided, IFluidHandler.FluidAction.SIMULATE) > 0)
                    .mapToInt(layer -> layer.drain(divided, action))
                    .sum();
        }
        remaining -= delta;
        if(remaining <= 0) {
            return delta;
        }
        if(delta <= 0) {
            return 0;
        }
        return drainAbove(remaining, action);
    }

    protected int calculateContents(double height) {
        if(height <= this.getMin()) {
            return 0;
        }
        if(height >= this.getMax()) {
            return this.getCapacity();
        }
        return (int) (this.getCapacity() * (height - this.getMin()) / (this.getMax() - this.getMin()));
    }

    protected double calculateHeight(int content) {
        if(content <= 0) {
            return this.getMin();
        }
        if(content >= this.getCapacity()) {
            return this.getMax();
        }
        double f = (content + 0.0D)/this.getCapacity();
        return MathHelper.lerp(f, this.getMin(), this.getMax());
    }

    public CompoundNBT writeToTag() {
        CompoundNBT tag = new CompoundNBT();
        // write layer properties
        tag.putInt(AgriNBT.KEY, this.getId());
        tag.putDouble(AgriNBT.Y1, this.getMin());
        tag.putDouble(AgriNBT.Y2, this.getMax());
        tag.putInt(AgriNBT.CAPACITY, this.getCapacity());
        tag.putDouble(AgriNBT.LEVEL, this.getHeight());
        // write layer positions
        ListNBT positionTags = new ListNBT();
        this.positions.forEach(pos -> {
            CompoundNBT posTag = new CompoundNBT();
            posTag.putInt(AgriNBT.X1, pos.getX());
            posTag.putInt(AgriNBT.Y1, pos.getY());
            posTag.putInt(AgriNBT.Z1, pos.getZ());
            positionTags.add(posTag);
        });
        tag.put(AgriNBT.ENTRIES, positionTags);
        // write chunks
        ListNBT chunkTags = new ListNBT();
        this.chunks.forEach(chunk -> {
            CompoundNBT chunkTag = new CompoundNBT();
            chunkTag.putInt(AgriNBT.X1, chunk.x);
            chunkTag.putInt(AgriNBT.Z1, chunk.z);
            chunkTags.add(chunkTag);
        });
        tag.put(AgriNBT.CHUNK, chunkTags);
        // write below positions
        ListNBT belowTags = new ListNBT();
        this.layersBelowMap.keySet().forEach(pos -> {
            CompoundNBT posTag = new CompoundNBT();
            posTag.putInt(AgriNBT.X1, pos.getX());
            posTag.putInt(AgriNBT.Y1, pos.getY());
            posTag.putInt(AgriNBT.Z1, pos.getZ());
            belowTags.add(posTag);
        });
        tag.put(AgriNBT.DOWN, belowTags);
        // write above positions
        ListNBT aboveTags = new ListNBT();
        this.layersAboveMap.keySet().forEach(pos -> {
            CompoundNBT posTag = new CompoundNBT();
            posTag.putInt(AgriNBT.X1, pos.getX());
            posTag.putInt(AgriNBT.Y1, pos.getY());
            posTag.putInt(AgriNBT.Z1, pos.getZ());
            aboveTags.add(posTag);
        });
        tag.put(AgriNBT.UP, aboveTags);
        return tag;
    }

    public void readFromTag(CompoundNBT tag) {
        // read layer properties
        this.min = tag.getDouble(AgriNBT.Y1);
        this.max = tag.getDouble(AgriNBT.Y2);
        this.capacity = tag.getInt(AgriNBT.CAPACITY);
        this.height = tag.getDouble(AgriNBT.LEVEL);
        // read layer positions
        this.positions.clear();
        tag.getList(AgriNBT.ENTRIES, 10)
                .stream()
                .map(posTag -> (CompoundNBT) posTag)
                .map(posTag -> new BlockPos(
                    posTag.getInt(AgriNBT.X1),
                    posTag.getInt(AgriNBT.Y1),
                    posTag.getInt(AgriNBT.Z1)))
                .forEach(this.positions::add);
        // read chunks
        this.chunks.clear();
        tag.getList(AgriNBT.CHUNK, 10)
                .stream()
                .map(chunkTag -> (CompoundNBT) chunkTag)
                .map(chunkTag -> new ChunkPos(
                        chunkTag.getInt(AgriNBT.X1),
                        chunkTag.getInt(AgriNBT.Z1)))
                .forEach(this.chunks::add);
        // read below positions
        this.layersBelow.clear();
        this.layersBelowMap = tag.getList(AgriNBT.DOWN, 10)
                .stream()
                .map(posTag -> (CompoundNBT) posTag)
                .map(posTag ->
                        new BlockPos(
                                posTag.getInt(AgriNBT.X1),
                                posTag.getInt(AgriNBT.Y1),
                                posTag.getInt(AgriNBT.Z1)))
                .collect(Collectors.toMap(
                        pos -> pos,
                        pos -> aPos -> IrrigationSystemHandler.getInstance().getBottomLayer(this.getWorld(), aPos)
                ));
        // read above positions
        this.layersAbove.clear();
        this.layersAboveMap = tag.getList(AgriNBT.UP, 10)
                .stream()
                .map(posTag -> (CompoundNBT) posTag)
                .map(posTag ->
                    new BlockPos(
                            posTag.getInt(AgriNBT.X1),
                            posTag.getInt(AgriNBT.Y1),
                            posTag.getInt(AgriNBT.Z1)))
                .collect(Collectors.toMap(
                        pos -> pos,
                        pos -> aPos -> IrrigationSystemHandler.getInstance().getTopLayer(this.getWorld(), aPos)
                ));
    }

    /*
    public void split(double level, Consumer<IrrigationLayer> consumer) {
        if(level <= this.getMin() || level >= this.getMax()) {
            consumer.accept(this);
        } else {
            double f = (level - this.getMin())/(this.getMax() - this.getMin());
            IrrigationLayer lower = new IrrigationLayer(
                    this.getMin(), level, (int) f * this.getCapacity());
            IrrigationLayer upper = new IrrigationLayer(
                    level, this.getMax(), this.getCapacity() - lower.getCapacity());
            if(lower.getCapacity() > 0) {
                consumer.accept(lower);
            }
            if(upper.calculateContents() > 0) {
                consumer.accept(upper);
            }
        }
    }

    public boolean merge(IrrigationLayer other, Consumer<IrrigationLayer> consumer) {
        if(this.getMin() != other.getMin() || this.getMax() != other.getMax()) {
            return false;
        }
        consumer.accept(new IrrigationLayer(this.getMin(), this.getMax(), this.calculateContents() + other.calculateContents()));
        return true;
    }

    public void distinct(IrrigationLayer other, Consumer<IrrigationLayer> consumer) {
        if(this.merge(other, consumer)) {
            // The layers have identical limits, the merging was successful
            return;
        }
        if(this.getMax() <= other.getMin() || this.getMin() >= other.getMax()) {
            // The layers do not overlap, pass them to the consumer
            consumer.accept(this);
            consumer.accept(other);
        } else {
            // The layers overlap
            if(this.getMin() < other.getMin()) {
                if(this.getMax() > other.getMax()) {
                    // this range envelopes the other range
                    this.envelope(other, consumer);
                } else {
                    // overlap from below
                    this.overlapFromBelow(other, consumer);
                }
            } else {
                if(this.getMax() > other.getMax()) {
                    // overlap from above
                    this.overlapFromAbove(other, consumer);
                } else {
                    // the other range envelopes this range
                    other.envelope(this, consumer);
                }
            }
        }
    }

    private void envelope(IrrigationLayer other, Consumer<IrrigationLayer> consumer) {
        this.split(other.getMin(), (split1) -> {
            if(split1.getMax() > other.getMax()) {
                split1.split(other.getMax(), (split2) -> {
                    if(split2.getMax() > other.getMax()) {
                        consumer.accept(split2);
                    } else {
                        other.merge(split2, consumer);
                    }
                });
            } else {
                consumer.accept(split1);
            }
        });
    }

    private void overlapFromAbove(IrrigationLayer other, Consumer<IrrigationLayer> consumer) {
        other.overlapFromBelow(this, consumer);
    }

    private void overlapFromBelow(IrrigationLayer other, Consumer<IrrigationLayer> consumer) {
        this.split(other.getMin(), (split1) -> {
            if(split1.getMax() > other.getMin()) {
                other.split(split1.getMax(), (split2) -> {
                    if(split2.getMax() > this.getMax()) {
                        consumer.accept(split2);
                    } else {
                        split1.merge(split2, consumer);
                    }
                });
            } else {
                consumer.accept(split1);
            }
        });
    }
     */
}
