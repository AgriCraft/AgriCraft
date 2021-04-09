package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    private final Map<RegistryKey<World>, Map< ChunkPos, Map<BlockPos, Set<IListener>>>> listeners;

    private BlockUpdateHandler() {
        this.listeners = Maps.newHashMap();
    }

    public void addListener(World world, BlockPos pos, IListener listener) {
        if(world instanceof ServerWorld) {
            RegistryKey<World> dimension = world.getDimensionKey();
            this.listeners.computeIfAbsent(dimension, key -> Maps.newHashMap())
                    .computeIfAbsent(new ChunkPos(pos), chunkPos -> Maps.newHashMap())
                    .computeIfAbsent(pos, aPos -> Sets.newIdentityHashSet())
                    .add(listener);
        }
    }

    public void removeListener(World world, BlockPos pos, IListener listener) {
        if(world instanceof ServerWorld) {
            RegistryKey<World> dimension = world.getDimensionKey();
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
        if(event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            RegistryKey<World> dimension = world.getDimensionKey();
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
        if(event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            if(this.listeners.containsKey(world.getDimensionKey())) {
                this.listeners.remove(world.getDimensionKey()).values()
                        .stream()
                        .flatMap(map -> map.entrySet().stream())
                        .forEach(entry -> entry.getValue().forEach(listener -> listener.onWorldUnloaded(world, entry.getKey())));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockUpdate(BlockEvent.NeighborNotifyEvent event) {
        if(event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            RegistryKey<World> dimension = world.getDimensionKey();
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
        void onBlockUpdate(ServerWorld world, BlockPos pos);

        void onChunkUnloaded(ServerWorld world, BlockPos pos);

        void onWorldUnloaded(ServerWorld world, BlockPos pos);
    }
}
