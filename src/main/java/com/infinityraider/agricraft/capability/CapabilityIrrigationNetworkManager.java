package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetworkInvalid;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Optional;

public class CapabilityIrrigationNetworkManager implements IInfSerializableCapabilityImplementation<World, CapabilityIrrigationNetworkManager.Impl> {
    private static final CapabilityIrrigationNetworkManager INSTANCE = new CapabilityIrrigationNetworkManager();

    public static CapabilityIrrigationNetworkManager getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_MANAGER);

    @CapabilityInject(Impl.class)
    public static final Capability<CapabilityIrrigationNetworkManager.Impl> CAPABILITY = null;

    public int addNetworkToWorld(IrrigationNetwork network) {
        World world = network.getWorld();
        if(world == null) {
            // Shouldn't ever happen
            return -1;
        }
        return this.getCapability(world).map(impl -> impl.addNetwork(network)).orElse(-1);
    }

    public IAgriIrrigationNetwork getNetwork(World world, int id) {
        return this.getCapability(world)
                .map(impl -> impl)
                .flatMap(impl -> impl.getNetwork(id))
                .orElse(IrrigationNetworkInvalid.getInstance());
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
    public boolean shouldApplyCapability(World carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(World world) {
        return new Impl(world);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<World> getCarrierClass() {
        return World.class;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkLoaded(ChunkEvent.Load event) {
        IChunk iChunk = event.getChunk();
        if(iChunk instanceof Chunk) {
            Chunk chunk = (Chunk) iChunk;
            this.getCapability(chunk.getWorld()).ifPresent(impl -> impl.onChunkLoaded(chunk));
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkUnloaded(ChunkEvent.Unload event) {
        IChunk iChunk = event.getChunk();
        if(iChunk instanceof Chunk) {
            Chunk chunk = (Chunk) iChunk;
            this.getCapability(chunk.getWorld()).ifPresent(impl -> impl.onChunkUnloaded(chunk));
        }
    }

    public static class Impl implements ISerializable {
        private final World world;

        private final Map<Integer, IrrigationNetwork> networks;

        private int nextId;

        private Impl(World world) {
            this.world = world;
            this.networks = Maps.newHashMap();
            this.nextId = 0;
        }

        public World getWorld() {
            return this.world;
        }

        public Optional<IAgriIrrigationNetwork> getNetwork(int id) {
            return Optional.ofNullable(this.networks.get(id));
        }

        public int addNetwork(IrrigationNetwork network) {
            int id = this.nextId;
            this.nextId++;
            this.networks.put(id, network);
            return id;
        }

        public void onChunkLoaded(Chunk chunk) {
            this.networks.values().forEach(network -> network.onChunkLoaded(chunk));
        }

        public void onChunkUnloaded(Chunk chunk) {
            this.networks.values().forEach(network -> network.onChunkUnloaded(chunk));
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            if(tag.contains(AgriNBT.ENTRIES)) {
                this.networks.clear();
                ListNBT entryList = tag.getList(AgriNBT.ENTRIES, 10);
                entryList.stream().filter(entryTag -> entryTag instanceof CompoundNBT)
                        .map(entryTag -> (CompoundNBT) entryTag)
                        .forEach(entryTag -> {
                            if(entryTag.contains(AgriNBT.NETWORK) && entryTag.contains(AgriNBT.KEY)) {
                                int id = entryTag.getInt(AgriNBT.KEY);
                                this.networks.put(id, IrrigationNetwork.readFromNbt(this.getWorld(), id, entryTag.getCompound(AgriNBT.NETWORK)));
                            }
                        });
            }
            if(tag.contains(AgriNBT.KEY)) {
                this.nextId = tag.getInt(AgriNBT.KEY);
            }
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT entryList = new ListNBT();
            this.networks.values().forEach(network -> {
                CompoundNBT entryTag = new CompoundNBT();
                entryTag.putInt(AgriNBT.KEY, network.getId());
                entryTag.put(AgriNBT.NETWORK, network.writeToNBT());
                entryList.add(entryTag);
            });
            tag.put(AgriNBT.ENTRIES, entryList);
            tag.putInt(AgriNBT.KEY, this.nextId);
            return tag;
        }
    }
}
