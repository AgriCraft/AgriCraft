package com.infinityraider.agricraft.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationLayer;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class IrrigationSystemHandler {
    private static final IrrigationSystemHandler INSTANCE = new IrrigationSystemHandler();

    public static IrrigationSystemHandler getInstance() {
        return INSTANCE;
    }

    private final Map<RegistryKey<World>, WorldManager> managers;

    private IrrigationSystemHandler() {
        this.managers = Maps.newHashMap();
    }

    public WorldManager getManager(World world) {
        return this.managers.computeIfAbsent(world.getDimensionKey(), key -> new WorldManager(world));
    }

    public List<IrrigationLayer> getLayers(World world, BlockPos pos) {
        return this.getManager(world).getLayers(pos);
    }

    public IrrigationLayer getBottomLayer(World world, BlockPos pos) {
        return this.getManager(world).getBottomLayer(pos);
    }

    public IrrigationLayer getTopLayer(World world, BlockPos pos) {
        return this.getManager(world).getTopLayer(pos);
    }

    public void loadLayers(IAgriIrrigationComponent component, ListNBT layerTags) {
        World world = component.world();
        if(world != null && !world.isRemote()) {
            this.getManager(world).deserializeLayers(component, layerTags);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        // this cast is safe since the event constructor takes a World object
        if(!event.getWorld().isRemote()) {
            this.getManager((World) event.getWorld()).onNeighbourUpdate(event.getPos(), event.getNotifiedSides());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkLoaded(ChunkEvent.Load event) {
        IWorld world = event.getWorld();
        if(!(world.isRemote()) && world instanceof World) {
            this.getManager((World) world).onChunkLoaded(event.getChunk());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkUnloaded(ChunkEvent.Unload event) {
        IWorld world = event.getWorld();
        if(!(world.isRemote()) && world instanceof World) {
            this.getManager((World) world).onChunkUnloaded(event.getChunk());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWorldUnloaded(WorldEvent.Unload event) {
        if(!event.getWorld().isRemote()) {
            if(event.getWorld() instanceof World) {
                this.managers.remove(((World) event.getWorld()).getDimensionKey());
            }
        }
    }

    private static final class WorldManager {
        private final World world;
        private final Map<Integer, IrrigationLayer> layers;
        private final Map<BlockPos, List<IrrigationLayer>> layersByPos;
        private int lastId;

        private WorldManager(World world) {
            this.world = world;
            this.layers = Maps.newHashMap();
            this.layersByPos = Maps.newHashMap();
            this.lastId = 0;
        }

        public World getWorld() {
            return this.world;
        }

        @Nullable
        public List<IrrigationLayer> getLayers(BlockPos pos) {
            return this.layersByPos.get(pos);
        }

        @Nullable
        public IrrigationLayer getTopLayer(BlockPos pos) {
            List<IrrigationLayer> layers = this.getLayers(pos);
            if(layers == null) {
                return null;
            }
            return layers.get(0);
        }

        @Nullable
        public IrrigationLayer getBottomLayer(BlockPos pos) {
            List<IrrigationLayer> layers = this.getLayers(pos);
            if(layers == null) {
                return null;
            }
            return layers.get(layers.size() - 1);
        }

        protected void onNeighbourUpdate(BlockPos pos, EnumSet<Direction> directions) {
            // TODO: create, merge, split and remove layers
        }

        protected void onChunkLoaded(IChunk chunk) {
            this.layers.values().forEach(layer -> layer.onChunkLoaded(chunk.getPos()));
        }

        protected void onChunkUnloaded(IChunk chunk) {
            this.layers.values().stream()
                    .filter(layer -> layer.onChunkUnloaded(chunk.getPos()))
                    .forEach(layer -> {
                        this.layers.remove(layer.getId());
                        layer.getPositions().forEach(pos -> this.layersByPos.get(pos).remove(layer));
                        this.layers.values().forEach(aLayer -> aLayer.onLayerUnloaded(layer));
                    });
        }

        protected void deserializeLayers(IAgriIrrigationComponent component, ListNBT layerTags) {
            layerTags.stream().map(tag -> (CompoundNBT) tag).forEach(tag ->
                    this.layers.computeIfAbsent(tag.getInt(AgriNBT.KEY), id -> {
                        IrrigationLayer newLayer = new IrrigationLayer(id, component);
                        newLayer.readFromTag(tag);
                        newLayer.getPositions().forEach(pos -> {
                            List<IrrigationLayer> layers = this.layersByPos.computeIfAbsent(pos, aPos -> Lists.newArrayList());
                            layers.add(newLayer);
                            layers.sort(Comparator.comparingDouble(IrrigationLayer::getMax));
                            if(this.getWorld().isAreaLoaded(pos, 0)) {
                                newLayer.onChunkLoaded(new ChunkPos(pos));
                            }
                        });
                        return newLayer;
                    }));
        }
    }
}
