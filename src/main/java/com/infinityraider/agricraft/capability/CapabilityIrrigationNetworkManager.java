package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CapabilityIrrigationNetworkManager implements IInfCapabilityImplementation<World, CapabilityIrrigationNetworkManager.Impl> {
    private static final CapabilityIrrigationNetworkManager INSTANCE = new CapabilityIrrigationNetworkManager();

    public static CapabilityIrrigationNetworkManager getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_MANAGER);

    @CapabilityInject(Impl.class)
    public static final Capability<CapabilityIrrigationNetworkManager.Impl> CAPABILITY = null;

    public int addNetworkToWorld(IrrigationNetwork network) {
        return network.getWorld().getCapability(this.getCapability()).map(impl -> impl.addNetwork(network)).orElse(-1);
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

    public static class Impl implements ISerializable {
        private final World world;

        private final Map<Integer, IrrigationNetwork> loaded;
        private final Set<Integer> all;

        private int nextId;

        private Impl(World world) {
            this.world = world;
            this.loaded = Maps.newHashMap();
            this.all = Sets.newHashSet();
            this.nextId = 0;
        }

        public World getWorld() {
            return this.world;
        }

        public boolean hasNetwork(int id) {
            return this.all.contains(id);
        }

        public boolean isNetworkLoaded(int id) {
            return this.loaded.containsKey(id);
        }

        public Optional<IrrigationNetwork> getNetwork(int id) {
            return Optional.ofNullable(this.loaded.get(id));
        }

        public int addNetwork(IrrigationNetwork network) {
            int id = this.nextId;
            this.nextId++;
            this.loaded.put(id, network);
            this.all.add(id);
            return id;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            if(tag.contains(AgriNBT.KEY)) {
                this.nextId = tag.getInt(AgriNBT.KEY);
            }
            if(tag.contains(AgriNBT.ENTRIES)) {
                this.all.clear();
                Arrays.stream(tag.getIntArray(AgriNBT.ENTRIES)).forEach(this.all::add);
            }
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(AgriNBT.KEY, this.nextId);
            tag.putIntArray(AgriNBT.ENTRIES, this.all.stream().mapToInt(Integer::intValue).toArray());
            return tag;
        }

        public void unloadNetwork(IrrigationNetwork network) {
            this.unloadNetwork(network.getId());
        }

        public void unloadNetwork(int id) {
            this.loaded.remove(id);
        }
    }
}
