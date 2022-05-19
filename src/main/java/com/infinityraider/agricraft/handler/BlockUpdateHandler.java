package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;

public class BlockUpdateHandler {
    private static final BlockUpdateHandler INSTANCE = new BlockUpdateHandler();

    public static BlockUpdateHandler getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceKey<Level>, Map<ChunkPos, Map<BlockPos, Set<IListener>>>> listeners;

    private BlockUpdateHandler() {
        this.listeners = Maps.newHashMap();
    }

    public void addListener(Level world, BlockPos pos, IListener listener) {
        if(world instanceof ServerLevel) {
            ResourceKey<Level> dimension = world.dimension();
            this.listeners.computeIfAbsent(dimension, key -> Maps.newHashMap())
                    .computeIfAbsent(new ChunkPos(pos), chunkPos -> Maps.newHashMap())
                    .computeIfAbsent(pos, aPos -> Sets.newIdentityHashSet())
                    .add(listener);
        }
    }

    public void removeListener(Level world, BlockPos pos, IListener listener) {
        if(world instanceof ServerLevel) {
            ResourceKey<Level> dimension = world.dimension();
            this.listeners.computeIfPresent(dimension, (dim, chunkMap) -> {
                chunkMap.computeIfPresent(new ChunkPos(pos), (chunkPos, posMap) -> {
                    posMap.computeIfPresent(pos, (aPos, set) -> {
                        set.remove(listener);
                        return set;
                    });
                    return posMap;
                });
                return chunkMap;
            });
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkUnloaded(ChunkEvent.Unload event) {
        if(event.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.getWorld();
            ResourceKey<Level> dimension = world.dimension();
            if(listeners.containsKey(dimension)) {
                listeners.computeIfPresent(dimension, (dim, chunkMap) -> {
                    if(chunkMap.containsKey(event.getChunk().getPos())) {
                        chunkMap.remove(event.getChunk().getPos()).forEach((pos, set) -> set.forEach(listener ->
                                listener.onChunkUnloaded(world, pos)));
                    }
                    return chunkMap;
                });
                listeners.get(dimension).remove(event.getChunk().getPos());
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onWorldUnloaded(WorldEvent.Unload event) {
        if(event.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.getWorld();
            if(this.listeners.containsKey(world.dimension())) {
                this.listeners.remove(world.dimension()).values()
                        .stream()
                        .flatMap(map -> map.entrySet().stream())
                        .forEach(entry -> entry.getValue().forEach(listener -> listener.onWorldUnloaded(world, entry.getKey())));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        if(event.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) event.getWorld();
            ResourceKey<Level> dimension = world.dimension();
            this.listeners.computeIfPresent(dimension, (dim, chunkMap) -> {
                chunkMap.computeIfPresent(new ChunkPos(event.getPos()), (chunkPos, posMap) -> {
                    posMap.computeIfPresent(event.getPos(), (pos, set) -> {
                        set.forEach(listener -> listener.onBlockUpdate(world, pos));
                        return set;
                    });
                    return posMap;
                });
                return chunkMap;
            });
        }
    }

    public interface IListener {
        void onBlockUpdate(ServerLevel world, BlockPos pos);

        void onChunkUnloaded(ServerLevel world, BlockPos pos);

        void onWorldUnloaded(ServerLevel world, BlockPos pos);
    }
}
