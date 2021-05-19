package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationLayerSection;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
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
        private final Map<Integer, IrrigationLayerSection> layers;
        private final Map<BlockPos, List<IrrigationLayerSection>> layersByPos;
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
        public List<IrrigationLayerSection> getLayers(BlockPos pos) {
            return this.layersByPos.get(pos);
        }

        @Nullable
        public IrrigationLayerSection getTopLayer(BlockPos pos) {
            List<IrrigationLayerSection> layers = this.getLayers(pos);
            if(layers == null) {
                return null;
            }
            return layers.get(0);
        }

        @Nullable
        public IrrigationLayerSection getBottomLayer(BlockPos pos) {
            List<IrrigationLayerSection> layers = this.getLayers(pos);
            if(layers == null) {
                return null;
            }
            return layers.get(layers.size() - 1);
        }

        protected void onNeighbourUpdate(BlockPos pos, EnumSet<Direction> directions) {
            // TODO: create, merge, split and remove layers
        }

        protected void onChunkLoaded(IChunk chunk) {
            // TODO
        }

        protected void onChunkUnloaded(IChunk chunk) {
            // TODO
        }
    }
}
