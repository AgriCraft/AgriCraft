package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class IrrigationNetwork {
    // network structure
    private final Set<IrrigationLayerManager> layers;

    // chunk loading trackers
    private final Set<ChunkPos> chunks;
    private final Set<ChunkPos> loadedChunks;

    protected IrrigationNetwork() {
        this.layers = Sets.newIdentityHashSet();
        this.chunks = Sets.newHashSet();
        this.loadedChunks = Sets.newHashSet();
    }

    public boolean isFullyLoaded() {
        return this.chunks.size() == this.loadedChunks.size();
    }

    public void onChunkLoaded(IChunk chunk) {
        if(this.chunks.contains(chunk.getPos())) {
            this.loadedChunks.add(chunk.getPos());
        }
    }

    public void onChunkUnloaded(IChunk chunk) {
        this.loadedChunks.removeIf(pos -> chunk.getPos().equals(pos));
    }

    private static class IrrigationLayerManager implements IFluidHandler {
        private final IrrigationNetwork network;
        private final IrrigationLayerSection layer;

        private final Map<IrrigationLayerSection.Pos, Set<IrrigationLayerManager>> connectionsAbove;
        private final Map<IrrigationLayerSection.Pos, Set<IrrigationLayerManager>> connectionsBelow;
        private final Map<IrrigationLayerSection.Pos, Set<IrrigationLayerManager>> connectionsHorizontal;

        protected IrrigationLayerManager(IrrigationNetwork network, IrrigationLayerSection layer) {
            this.network = network;
            this.layer = layer;
            this.connectionsAbove = Maps.newHashMap();
            this.connectionsBelow = Maps.newHashMap();
            this.connectionsHorizontal = Maps.newHashMap();
        }

        public IrrigationNetwork getNetwork() {
            return this.network;
        }

        public IrrigationLayerSection getLayer() {
            return this.layer;
        }

        @Override
        public int getTanks() {
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return null;
        }

        @Override
        public int getTankCapacity(int tank) {
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return null;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return null;
        }
    }
}
